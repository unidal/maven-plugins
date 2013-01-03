package org.unidal.maven.plugin.project;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.tuple.Ref;

/**
 * Start TestServer from source code.
 * 
 * @goal start-test-server
 * @requiresDependencyResolution test
 */
public class StartTestServerMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   /**
    * @parameter expression="${verbose}" default-value="false"
    * @required
    */
   private boolean verbose;

   private String detectTestServerName() throws MojoExecutionException {
      final Ref<String> testServerName = new Ref<String>();

      Scanners.forDir().scan(new File("target/test-classes"), new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (testServerName.getValue() == null && path.endsWith("/TestServer.class")) {
               String className = path.substring(0, path.length() - 6).replace('/', '.');

               testServerName.setValue(className);
            }

            return Direction.DOWN;
         }
      });

      if (testServerName.getValue() != null) {
         return testServerName.getValue();
      } else {
         throw new MojoExecutionException("No TestServer was found!");
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      Map<String, String> properties = new HashMap<String, String>();
      String dependencyClasspathFile = "target/dependency.classpath";
      StringBuilder classpath = new StringBuilder(2048);

      properties.put("mdep.outputFile", dependencyClasspathFile);

      runMavenApplication(properties, "test-compile dependency:copy-dependencies dependency:build-classpath");

      classpath.append("target/classes").append(File.pathSeparator);
      classpath.append("target/test-classes").append(File.pathSeparator);

      try {
         String dependencyClasspath = Files.forIO().readFrom(new File(dependencyClasspathFile), "utf-8");

         classpath.append(dependencyClasspath);
      } catch (IOException e) {
         throw new MojoExecutionException(e.toString(), e);
      }

      String testServerName = detectTestServerName();

      runJavaApplication(classpath.toString(), testServerName, null);
   }

   protected void runJavaApplication(String classpath, String application, Map<String, String> properties)
         throws MojoExecutionException {
      Commandline cli = new Commandline();

      cli.setWorkingDirectory(m_project.getBasedir());
      cli.setExecutable("java");

      StringBuilder sb = new StringBuilder(4096);

      if (classpath != null) {
         sb.append("-cp \"").append(classpath).append("\" ");
      }

      if (properties != null) {
         for (Map.Entry<String, String> e : properties.entrySet()) {
            String name = e.getKey();
            String value = e.getValue();

            sb.append("-D").append(name).append("=");

            if (value.indexOf(' ') >= 0) {
               sb.append("\"").append(value).append("\"");
            } else {
               sb.append(value);
            }

            sb.append(' ');
         }
      }

      sb.append(application);

      Arg arg = cli.createArg();

      arg.setLine(sb.toString());

      if (verbose) {
         getLog().info("Executing java " + sb);
      }

      try {
         CommandLineUtils.executeCommandLine(cli, new StreamConsumer() {
            @Override
            public void consumeLine(String line) {
               getLog().info(line);
            }
         }, new StreamConsumer() {
            @Override
            public void consumeLine(String line) {
               getLog().error(line);
            }
         });
      } catch (CommandLineException e) {
         throw new MojoExecutionException("Fail to execute java command!", e);
      }
   }

   protected void runMavenApplication(Map<String, String> properties, String... arguments) throws MojoExecutionException {
      Commandline cli = new Commandline();

      cli.setWorkingDirectory(m_project.getBasedir());
      cli.setExecutable("mvn");

      StringBuilder sb = new StringBuilder(4096);

      if (properties != null) {
         for (Map.Entry<String, String> e : properties.entrySet()) {
            String name = e.getKey();
            String value = e.getValue();

            sb.append("-D").append(name).append("=");

            if (value.indexOf(' ') >= 0) {
               sb.append("\"").append(value).append("\"");
            } else {
               sb.append(value);
            }

            sb.append(' ');
         }
      }

      for (String argument : arguments) {
         sb.append(' ').append(argument);
      }

      Arg arg = cli.createArg();

      arg.setLine(sb.toString());

      if (verbose) {
         getLog().info("Executing mvn " + sb);
      }

      try {
         CommandLineUtils.executeCommandLine(cli, new StreamConsumer() {
            private String m_info = "[INFO] ";

            private String m_warn = "[WARN] ";

            @Override
            public void consumeLine(String line) {
               if (line.startsWith(m_info)) {
                  getLog().info(line.substring(m_info.length()));
               } else if (line.startsWith(m_warn)) {
                  getLog().warn(line.substring(m_warn.length()));
               } else {
                  getLog().info(line);
               }
            }
         }, new StreamConsumer() {
            @Override
            public void consumeLine(String line) {
               getLog().error(line);
            }
         });
      } catch (CommandLineException e) {
         throw new MojoExecutionException("Fail to execute mvn command!", e);
      }
   }
}
