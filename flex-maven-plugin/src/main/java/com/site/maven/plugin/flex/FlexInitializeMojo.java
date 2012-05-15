package com.site.maven.plugin.flex;

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Initialize/setup Flex environment.
 * 
 * <ul>
 * <li>Set default source directory to ${basedir}/src/main/flex</li>
 * <li>Set default test source directory to ${basedir}/src/test/flex</li>
 * <li>Set default output directory to ${project.build.directory}/flex</li>
 * <li>Set default test output directory to
 * ${project.build.directory}/test-flex</li>
 * </ul>
 * 
 * @goal initialize
 * @author qwu
 */
public class FlexInitializeMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * Source directory of Flex.
    * 
    * @parameter default-value="${basedir}/src/main/flex"
    * @required
    */
   private File sourceDirectory;

   /**
    * Test source directory of Flex.
    * 
    * @parameter default-value="${basedir}/src/test/flex"
    * @required
    */
   private File testSourceDirectory;

   /**
    * Output directory of Flex.
    * 
    * @parameter default-value="${project.build.directory}/flex"
    * @required
    */
   private File outputDirectory;

   /**
    * Test output directory of Flex.
    * 
    * @parameter default-value="${project.build.directory}/test-flex"
    * @required
    */
   private File testOutputDirectory;

   public void execute() throws MojoExecutionException, MojoFailureException {
      Build build = project.getBuild();

      build.setSourceDirectory(getPath("project.build.sourceDirectory", build.getSourceDirectory(), "src/main/java",
            sourceDirectory));
      build.setTestSourceDirectory(getPath("project.build.testSourceDirectory", build.getTestSourceDirectory(),
            "src/test/java", testSourceDirectory));
      build.setOutputDirectory(getPath("project.build.outputDirectory", build.getOutputDirectory(), "target/classes",
            outputDirectory));
      build.setTestOutputDirectory(getPath("project.build.testOutputDirectory", build.getTestOutputDirectory(),
            "target/test-classes", testOutputDirectory));
   }

   private String getPath(String propertyName, String userPath, String suffix, File defaultPath) {
      if (userPath.replace('\\', '/').endsWith(suffix)) {
         getLog().debug("Set " + propertyName + " to " + defaultPath);

         return defaultPath.getAbsolutePath();
      } else {
         getLog().debug("Set " + propertyName + " to " + userPath);

         return userPath;
      }
   }
}
