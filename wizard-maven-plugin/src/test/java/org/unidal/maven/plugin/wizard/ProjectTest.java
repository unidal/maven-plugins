package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Test;
import org.unidal.maven.plugin.wizard.ProjectMojo;

public class ProjectTest {
   @Test
   public void testProject() throws MojoExecutionException, MojoFailureException {
      ProjectMojo mojo = new ProjectMojo();

      mojo.groupId = "org.unidal.test";
      mojo.artifactId = "test";
      mojo.version = "1.0";
      mojo.packaging = "pom";
      mojo.name = "Test";
      mojo.m_pomFile = new File("target/pom.xml");

      mojo.execute();
   }
}
