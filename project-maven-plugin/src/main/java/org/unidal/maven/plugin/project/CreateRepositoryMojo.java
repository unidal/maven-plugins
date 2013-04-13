package org.unidal.maven.plugin.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.sonatype.aether.RepositorySystemSession;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;

/**
 * Create a Maven repository to hold some jars (build & plugin) referenced by
 * current project
 * 
 * @goal create-repository
 */
public class CreateRepositoryMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   /**
    * The current repository/network configuration of Maven.
    * 
    * @parameter default-value="${repositorySystemSession}"
    * @readonly
    */
   private RepositorySystemSession m_repositorySession;

   /**
    * @parameter expression="${localRepository}"
    * @readonly
    * @required
    */
   private ArtifactRepository m_localRepository;

   /**
    * @component
    * @required
    * @readonly
    */
   private ProjectDependenciesResolver m_dependencyResolver;

   /**
    * @component
    * @required
    * @readonly
    */
   private ProjectBuilder m_projectBuilder;

   /**
    * @parameter expression="${targetDir}"
    *            default-value="${user.dir}/target/repository"
    */
   private File m_targetDir;

   /**
    * @parameter expression="${include}"
    *            default-value="org.unidal,com.site,com.dianping"
    */
   private String include;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   private boolean verbose;

   private void collectDependencies(Set<Artifact> all, List<String> prefixes, MavenProject project, boolean withPlugin, int level)
         throws ProjectBuildingException {
      DependencyResolutionResult resolutionResult;

      try {
         DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, m_repositorySession);

         resolutionResult = m_dependencyResolver.resolve(resolution);
      } catch (DependencyResolutionException e) {
         resolutionResult = e.getResult();
      }

      List<org.sonatype.aether.graph.Dependency> dependencies = resolutionResult.getResolvedDependencies();

      if (!dependencies.isEmpty()) {
         for (org.sonatype.aether.graph.Dependency d : dependencies) {
            org.sonatype.aether.artifact.Artifact artifact = d.getArtifact();
            Artifact a = RepositoryUtils.toArtifact(artifact);

            // logLine(a.getId(), level + 1);
            resolveDependency(all, prefixes, a, level + 1);
         }
      }

      if (withPlugin) {
         for (Plugin plugin : m_project.getBuildPlugins()) {
            Artifact pluginArtifact = new DefaultArtifact(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(),
                  "compile", "jar", null, new DefaultArtifactHandler("jar"));

            resolveDependency(all, prefixes, pluginArtifact, 0);

            for (Dependency dependency : plugin.getDependencies()) {
               Artifact dependencyArtifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                     dependency.getVersion(), "compile", dependency.getType(), null, new DefaultArtifactHandler("jar"));

               resolveDependency(all, prefixes, dependencyArtifact, 1);
            }
         }
      }
   }

   private void logLine(String id, int level) {
      StringBuilder sb = new StringBuilder(128);

      for (int i = 0; i < level - 1; i++) {
         sb.append("|  ");
      }

      if (level > 0) {
         sb.append("+- ");
      }

      sb.append(id);

      getLog().info(sb.toString());
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      List<String> prefixes = Splitters.by(',').trim().noEmptyItem().split(include);
      Set<Artifact> all = new HashSet<Artifact>();

      try {
         if (verbose) {
            logLine(m_project.getId(), 0);
         }

         collectDependencies(all, prefixes, m_project, true, 0);

         List<String> result = new ArrayList<String>();

         for (Artifact artifact : all) {
            result.add(m_localRepository.pathOf(artifact));

            resolveParentPom(result, artifact);
         }

         save(result, m_targetDir);
      } catch (Exception e) {
         throw new MojoFailureException("Error when creating repository! " + e, e);
      }
   }

   private void resolveDependency(Set<Artifact> all, List<String> prefixes, Artifact artifact, int level)
         throws ProjectBuildingException {
      boolean eligible = false;

      for (String prefix : prefixes) {
         if (artifact.getGroupId().startsWith(prefix)) {
            eligible = true;
            break;
         }
      }

      if (!eligible || all.contains(artifact)) {
         return;
      }

      all.add(artifact);

      if (verbose) {
         logLine(artifact.getId(), level);
      }

      ProjectBuildingRequest request = m_project.getProjectBuildingRequest();
      String path = m_localRepository.pathOf(artifact);
      File pomFile = new File(m_localRepository.getBasedir(), path.substring(0, path.length() - 4) + ".pom");
      MavenProject project = m_projectBuilder.build(pomFile, request).getProject();

      collectDependencies(all, prefixes, project, false, level);
   }

   private void resolveParentPom(List<String> result, Artifact artifact) {
      ProjectBuildingRequest request = m_project.getProjectBuildingRequest();
      String repositoryBase = m_localRepository.getBasedir();
      String pomPath = null;

      try {
         if (artifact.getType() == "jar") {
            String jarPath = m_localRepository.pathOf(artifact);

            pomPath = jarPath.substring(0, jarPath.length() - 4) + ".pom";
         } else {
            pomPath = m_localRepository.pathOf(artifact);
         }

         if (result.contains(pomPath)) {
            return;
         }

         result.add(pomPath);

         File pomFile = new File(repositoryBase, pomPath);
         MavenProject project = m_projectBuilder.build(pomFile, request).getProject();
         Artifact parentArtifact = project.getParentArtifact();

         if (parentArtifact != null) {
            resolveParentPom(result, parentArtifact);
         }
      } catch (ProjectBuildingException e) {
         throw new IllegalStateException("Failed to build parent project for " + artifact.getId(), e);
      }
   }

   private void save(List<String> files, File targetBase) throws IOException {
      File sourceBase = new File(m_localRepository.getBasedir());

      for (String file : files) {
         File source = new File(sourceBase, file);
         File target = new File(targetBase, file);

         if (!target.isFile()) {
            target.getParentFile().mkdirs();

            Files.forDir().copyFile(source, target);
            getLog().info(String.format("File %s created.", target));

            File sourceSign = new File(sourceBase, file + ".sha1");
            File targetSign = new File(targetBase, file + ".sha1");

            if (sourceSign.isFile()) {
               Files.forDir().copyFile(sourceSign, targetSign);
               getLog().info(String.format("File %s created.", targetSign));
            }
         }
      }
   }
}
