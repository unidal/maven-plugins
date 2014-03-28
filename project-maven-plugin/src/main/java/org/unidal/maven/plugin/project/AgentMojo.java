package org.unidal.maven.plugin.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.helper.Files;

/**
 * Prepare resource file for phoenix java agent.
 * 
 * @goal agent
 */
public class AgentMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   private boolean verbose;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         int count = 0;

         count += process(m_project.getBuild().getOutputDirectory(), m_project.getBasedir() + "/src/main/resources");
         count += process(m_project.getBuild().getTestOutputDirectory(), m_project.getBasedir() + "/src/test/resources");

         getLog().info(String.format("%s files processed.", count));
      } catch (Exception e) {
         throw new MojoFailureException("Error when preparing phoenix agent resources!", e);
      }
   }

   private int process(String baseDir, String target) throws IOException {
      String base = m_project.getBuild().getTestOutputDirectory();
      Properties properties = loadAgentProperties(baseDir);
      int count = 0;

      for (Object key : properties.keySet()) {
         String className = (String) key;
         String path = className.replace('.', '/');
         File src = new File(base, path + ".class");
         File dst = new File(target, "META-INF/agent/" + className);

         if (!src.exists()) {
            getLog().warn(String.format("File %s not found!", src));
         } else {
            dst.getParentFile().mkdirs();

            if (verbose) {
               getLog().info(String.format("Copy file %s to %s", src, dst));
            }

            Files.forDir().copyFile(src, dst);
            count++;
         }
      }

      return count;
   }

   private Properties loadAgentProperties(String baseDir) throws IOException {
      File file = new File(baseDir, "META-INF/agent/agent.properties");
      Properties properties = new Properties();

      if (file.exists()) {
         if (verbose) {
            getLog().info(String.format("Loading properties %s", file));
         }

         properties.load(new FileInputStream(file));
      }

      return properties;
   }
}
