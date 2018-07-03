package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.helper.Files;
import org.unidal.tuple.Pair;

/**
 * Create a test configurator class
 * 
 * @goal test-configurator
 */
public class TestConfiguratorMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Test class name or file path to generate test configurator for.
    * 
    * @parameter expression="${test}"
    */
   protected String test;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      Pair<File, String> pair = resolve();
      File testDir = pair.getKey();
      String className = pair.getValue();
      int pos = className.lastIndexOf('.');
      File targetFile = new File(testDir, className.replace('.', '/') + "Configurator.java");

      try {
         if (targetFile.exists()) {
            getLog().warn("File " + targetFile.getCanonicalPath() + " is already existed. IGNORED!");
         } else {
            String template = Files.forIO().readFrom(getClass().getResourceAsStream("TestConfigurator.origin"), "utf-8");
            String result = template.replaceAll(Pattern.quote("${package}"), className.substring(0, pos)) //
                  .replaceAll(Pattern.quote("${testClass}"), className.substring(pos + 1));

            Files.forIO().writeTo(targetFile, result);
            getLog().info("File " + targetFile.getCanonicalPath() + " generated.");
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating test configurator: " + targetFile + "!", e);
      }
   }

   private Pair<File, String> resolve() throws MojoFailureException {
      String name = PropertyProviders.fromConsole().forString("test", "Test class name or file path:", null, null);
      String path;
      String className;

      if (name.endsWith(".java")) {
         if (name.startsWith("src/test/java/")) {
            int prefix = "src/test/java/".length();

            path = name.substring(prefix);
            className = name.substring(prefix, name.length() - 5).replace('/', '.');
         } else {
            path = name;
            className = name.substring(0, name.length() - 5).replace('/', '.');
         }
      } else {
         path = name.replace('.', '/') + ".java";
         className = name;
      }

      File testDir = null;

      for (String testSourceRoot : m_project.getTestCompileSourceRoots()) {
         File file = new File(testSourceRoot, path);

         if (file.isFile()) {
            testDir = new File(testSourceRoot);
            break;
         }
      }

      if (testDir == null) {
         throw new MojoFailureException(String.format("No corresponding test class(%s) found!", path));
      }

      return new Pair<File, String>(testDir, className);
   }
}
