package org.unidal.maven.plugin.common;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.lifecycle.internal.LifecycleDependencyResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.project.MavenProject;

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
    * @component
    * @required
    * @readonly
    */
   private LifecycleDependencyResolver m_resolver;

   /**
    * LegacySupport to get MavenSession and RepositorySystemSession.
    * 
    * @component
    * @required
    * @readonly
    */
   protected LegacySupport m_legacySupport;

   protected void resolveRuntimeDependencies() throws LifecycleExecutionException {
      m_resolver.resolveProjectDependencies(m_project, Arrays.asList("compile", "runtime"),
            Arrays.asList("compile", "runtime"), m_legacySupport.getSession(), false, new HashSet<Artifact>());
   }
}
