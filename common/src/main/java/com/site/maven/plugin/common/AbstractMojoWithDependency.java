package com.site.maven.plugin.common;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.ProjectDependenciesResolver;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.impl.DependencyCollector;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.graph.selector.AndDependencySelector;
import org.sonatype.aether.util.graph.selector.ExclusionDependencySelector;
import org.sonatype.aether.util.graph.selector.OptionalDependencySelector;
import org.sonatype.aether.util.graph.selector.ScopeDependencySelector;

public abstract class AbstractMojoWithDependency extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * LegacySupport to get MavenSession and RepositorySystemSession.
    * 
    * @component
    * @required
    * @readonly
    */
   protected LegacySupport m_legacySupport;

   /**
    * Project dependencies resolver, needed to download source jars for
    * inclusion in classpath.
    * 
    * @component
    * @required
    * @readonly
    */
   protected ProjectDependenciesResolver m_dependenciesResolver;

   /**
    * Artifact dependency collector
    * 
    * @component
    * @required
    * @readonly
    */
   protected DependencyCollector m_dependencyCollector;

   /**
    * Artifact dependency collector
    * 
    * @component
    * @required
    * @readonly
    */
   protected RepositorySystem m_repositorySystem;

   /**
    * Artifact repository layout
    * 
    * @component
    * @required
    * @readonly
    */
   protected ArtifactRepositoryLayout m_repositoryLayout;

   protected RepositorySystemSession createSession() {
      DependencySelector depFilter = new AndDependencySelector(new ScopeDependencySelector("test"),
            new OptionalDependencySelector(), new ExclusionDependencySelector());
      DefaultRepositorySystemSession session = new DefaultRepositorySystemSession(
            m_legacySupport.getRepositorySession());

      session.setDependencySelector(depFilter);
      session.setArtifactTypeRegistry(session.getArtifactTypeRegistry());

      return session;
   }

   protected ClassLoader makeClassLoader() throws ArtifactResolutionException, ArtifactNotFoundException {
      final List<Artifact> artifacts = new ArrayList<Artifact>();
      final ArtifactHandler handler = new ArtifactHandler() {
         @Override
         public void handle(Artifact artifact) {
            artifacts.add(artifact);
         }
      };

      resolveProjectDependency(m_project, ScopeArtifactFilter.NO_TEST, handler, "compile");

      return makeClassLoader(artifacts);
   }

   protected ClassLoader makeClassLoader(Collection<Artifact> artifacts) {
      List<URL> urls = new ArrayList<URL>(artifacts.size());

      try {
         for (Artifact artifact : artifacts) {
            File file = artifact.getFile();

            if (file != null) {
               urls.add(file.toURI().toURL());
            } else {
               getLog().warn(String.format("Can't resolve artifact(%s)!", artifact));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
   }

   protected ClassLoader makeClassLoader(List<String> classpathElements) {
      List<URL> urls = new ArrayList<URL>(classpathElements.size());

      try {
         for (String element : classpathElements) {
            File file = new File(element);

            urls.add(file.toURI().toURL());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
   }

   protected void resolveDependency(org.apache.maven.model.Dependency root, ArtifactFilter filter,
         ArtifactHandler handler) throws DependencyCollectionException, DependencyResolutionException {
      RepositorySystemSession session = createSession();
      Dependency d = RepositoryUtils.toDependency(root, session.getArtifactTypeRegistry());
      CollectRequest cr = new CollectRequest(d, m_project.getRemoteProjectRepositories());
      CollectResult cresult = m_dependencyCollector.collectDependencies(session, cr);
      DependencyRequest dr = new DependencyRequest(cresult.getRoot(), null);
      DependencyResult dresult = m_repositorySystem.resolveDependencies(session, dr);

      for (ArtifactResult result : dresult.getArtifactResults()) {
         Artifact artifact = toArtifact(result);

         if (filter == null || filter.include(artifact)) {
            handler.handle(artifact);
         }
      }
   }

   protected void resolveProjectDependency(MavenProject project, ArtifactFilter filter, ArtifactHandler handler,
         String... scopes) throws ArtifactResolutionException, ArtifactNotFoundException {
      getLog().warn(m_legacySupport+":"+m_dependenciesResolver+":"+m_dependencyCollector);
      Set<Artifact> artifacts = m_dependenciesResolver.resolve(project, Arrays.asList(scopes),
            m_legacySupport.getSession());

      // hack here
      project.getArtifact().setFile(new File(project.getBuild().getOutputDirectory()));
      handler.handle(project.getArtifact());

      for (Artifact artifact : artifacts) {
         if (filter == null || filter.include(artifact)) {
            handler.handle(artifact);
         }
      }
   }

   protected Artifact toArtifact(ArtifactResult result) {
      if (result == null) {
         return null;
      }

      org.sonatype.aether.artifact.Artifact a = result.getArtifact();
      Map<String, String> properties = a.getProperties();
      String scope = result.getRequest().getDependencyNode().getPremanagedScope();
      Artifact artifact = new DefaultArtifact(a.getGroupId(), a.getArtifactId(), a.getVersion(), scope,
            properties.get("type"), a.getClassifier(), null);

      artifact.setFile(a.getFile());
      return artifact;
   }

   protected static interface ArtifactHandler {
      public void handle(Artifact artifact);
   }

   protected static enum ScopeArtifactFilter implements ArtifactFilter {
      NO_TEST {
         @Override
         public boolean include(Artifact artifact) {
            String scope = artifact.getScope();

            // exclude test scope
            return !"test".equals(scope);
         }
      };
   }
}
