package com.site.maven.plugin.flex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

import com.site.maven.plugin.common.AbstractMojoWithDependency;

/**
 * Flex compile mojo to generate .swc or/and .swf file on Flex SDK 2 &amp; 3.
 * 
 * A forked java process will be created to do Flex compilation. JVM arguments
 * and additional arguments could be specified if needed.
 * 
 * Note: The plugin will check the output file and sources files to determine if
 * source is up to date or it should be re-generated.
 * 
 * @goal compile
 * @author qwu
 */
public class FlexCompileMojo extends AbstractMojoWithDependency {
   /**
    * Maven Project Helper
    * 
    * @component role="org.apache.maven.m_project.MavenProjectHelper"
    * @required
    * @readonly
    */
   protected MavenProjectHelper projectHelper;

   /**
    * Type of output, currently two types supported: swf and swc
    * 
    * @parameter default-value="${m_project.packaging}"
    * @readonly
    */
   private String packaging;

   /**
    * Home location of Flex SDK 2 or 3, please checking the existance of
    * lib/mxmlc.jar and lib/compc.jar and lib/xercesImpl.jar
    * 
    * @parameter expression="${flex.home}"
    * @required
    */
   private File flexHome;

   /**
    * Source file (for example, .mxml/.css). It's required for swf compilation.
    * 
    * @parameter expression="${flex.sourceFile}"
    *            default-value="${m_project.build.sourceDirectory}/main.mxml"
    */
   private File sourceFile;

   /**
    * Flex configuration file for Flex compiler.
    * 
    * @parameter expression="${flex.config}"
    */
   private File flexConfig;

   /**
    * Flex build parameters in the format of below (within configuration):
    * 
    * <pre>
    * &lt;configuration&gt;
    *    ...
    *    &lt;parameters&gt;
    *       &lt;locale&gt;en_US&lt;locale&gt;
    *       &lt;include-classes&gt;
    *          &lt;class&gt;com.site.A&lt;/class&gt;
    *          &lt;class&gt;com.site.B&lt;/class&gt;
    *          ...
    *       &lt;/include-classes&gt;
    *       &lt;include-file&gt;file.png com/site/image/file.png&lt;/include-file&gt;
    *       &lt;include-file&gt;folder.png com/site/image/folder.png&lt;/include-file&gt;
    *       &lt;publisher&gt;site.com&lt;/publisher&gt;
    *       &lt;--
    *       &lt;dump-config&gt;flex-config&lt;/dump-config&gt;
    *       --&gt;
    *       &lt;benchmark/&gt;
    *    &lt;/parameters&gt;
    *    ...
    * &lt;/configuration&gt;
    * </pre>
    * 
    * as shown above, following parameters will be added into the command line:
    * 
    * <pre>
    * -locate en_US
    * -include-classes com.site.A,com.site.B,...
    * -include-file file.png com/site/image/file.png
    * -include-file folder.png com/site/image/folder.png
    * ...
    * -publisher site.com
    * -benchmark
    * </pre>
    * 
    * @parameter
    */
   private List<String> parameters;

   /**
    * JVM arguments for forked java process
    * 
    * @parameter expression="${jvmargs}"
    */
   private String jvmargs;

   /**
    * additional arguments to be passed at the end of command line to build Flex
    * 
    * @parameter expression="${flex.args}"
    */
   private String args;

   /**
    * Artifact classifier
    * 
    * @parameter expression="${flex.classifier}" default-value="flex2"
    */
   private String classifier;

   /**
    * Skip the Flex build or not. If not enabled, then the build will be skipped
    * 
    * @parameter expression="${flex.enabled}" default-value="true"
    */
   private boolean enabled;

   /**
    * swf dependencies, which will be used to generate html template files and
    * .swf files will be copied to the war source directory
    * 
    * @parameter
    */
   private List<Dependency> dependencies;

   private File m_dumpConfig;

   private void addZipFiles(ZipOutputStream zos, File source, String prefix) throws IOException {
      String[] files = source.list();

      if (prefix.length() > 0) {
         ZipEntry entry = new ZipEntry(prefix + (source.isDirectory() ? "/" : ""));

         entry.setTime(source.lastModified());
         zos.putNextEntry(entry);

         if (source.isFile()) {
            FileInputStream fis = new FileInputStream(source);

            zos.write(IOUtil.toByteArray(fis));
            fis.close();
         }
      }

      if (files != null) {
         for (String file : files) {
            addZipFiles(zos, new File(source, file), prefix.length() == 0 ? file : prefix + "/" + file);
         }
      }
   }

   /**
    * Call Flex SDK java class to generate SWF file
    * 
    * @throws MojoExecutionException
    *            if there is an error
    */
   private void doBuild() throws MojoExecutionException {
      StringBuffer sb = new StringBuffer(1024);
      List<String> arguments = getBuildArguments();

      sb.append("java");

      for (String argument : arguments) {
         if (argument != null && argument.length() > 0) {
            sb.append(' ').append(argument);
         }
      }

      getLog().info("Executing command at " + m_project.getBasedir());
      getLog().info("   " + sb.toString());

      try {
         Process process = Runtime.getRuntime().exec(sb.toString(), null, m_project.getBasedir());
         InputStream stdin = process.getInputStream();
         InputStream stderr = process.getErrorStream();

         int exitCode = redirectOutput(process, stdin, stderr);

         if (exitCode != 0) {
            throw new MojoExecutionException("Failed with exit code: " + exitCode);
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Execution failure", e);
      }
   }

   private void doFixesForAsdoc() {
      int len = parameters.size();

      for (int i = len - 1; i >= 0; i--) {
         String parameter = parameters.get(i);

         if (parameter.startsWith("-include-classes")) {
            parameters.set(i, "-doc-classes" + parameter.substring("-include-classes".length()));
         } else if (parameter.startsWith("-include-sources")) {
            parameters.set(i, "-doc-sources" + parameter.substring("-include-sources".length()));
         } else if (parameter.startsWith("-include-file")) {
            parameters.remove(i);
         }
      }
   }

   /**
    * Fix some path issues on the dump file caused by Flex compiler
    * 
    * @param dumpConfig
    *           dump file path
    */
   private void doFixesForDumpConfig() {
      if (m_dumpConfig != null && m_dumpConfig.exists()) {
         try {
            FileReader reader = new FileReader(m_dumpConfig);
            String content = IOUtil.toString(reader);

            reader.close();
            content = StringUtils.replace(content, "<path-element>libs/playerglobal.swc</path-element>",
                  "<path-element>${flexlib}/libs/playerglobal.swc</path-element>");
            content = StringUtils.replace(content, "<path-element>libs</path-element>",
                  "<path-element>${flexlib}/libs</path-element>");
            content = StringUtils.replace(content, "<path-element>locale/{locale}</path-element>",
                  "<path-element>${flexlib}/locale/{locale}</path-element>");
            content = StringUtils.replace(content, "<manifest>mxml-manifest.xml</manifest>",
                  "<manifest>${flexlib}/mxml-manifest.xml</manifest>");

            FileWriter writer = new FileWriter(m_dumpConfig);

            IOUtil.copy(content, writer);
            writer.close();
            getLog().info("Dump file path issues fixed");
         } catch (IOException e) {
            getLog().warn("Can't fix path issues due to " + e, e);
         }
      }
   }

   private File doPackageAsdoc() throws MojoExecutionException {
      File outputDirectory = getAbsoluteFile(m_project.getBuild().getOutputDirectory());
      File source = new File(outputDirectory, "asdoc");
      File outputFile = new File(outputDirectory, m_project.getArtifactId() + "-" + m_project.getVersion() + "-"
            + classifier + ".jar");

      try {
         ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));

         addZipFiles(zos, source, "");
         zos.finish();
         zos.close();
      } catch (Exception e) {
         throw new MojoExecutionException("Error when creating asdoc package file: " + outputFile, e);
      }

      getLog().info("Asdoc archive file created: " + outputFile);

      return outputFile;
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (!enabled) {
         getLog().info("The Flex build is skipped explicitly");
         return;
      }

      validate();

      if ("flex2".equals(classifier) || "flex3".equals(classifier)) {
         doBuild();
         doFixesForDumpConfig();

         File file = getOutput();

         if (m_project.getArtifact().getFile() == null) {
            m_project.getArtifact().setFile(file);
         }

         projectHelper.attachArtifact(m_project, m_project.getPackaging(), classifier, file);
      } else if ("asdoc".equals(classifier)) {
         try {
            doFixesForAsdoc();
            doBuild();

            File file = doPackageAsdoc();
            projectHelper.attachArtifact(m_project, "jar", classifier, file);
         } catch (Exception e) {
            getLog().warn("Error when building asdoc:" + e, e);
            getLog().info("It's skipped.");
         }
      } else {
         throw new MojoExecutionException("Unsupported Flex classifier: " + classifier);
      }
   }

   private File getAbsoluteFile(String file) {
      if (new File(file).isAbsolute()) {
         return new File(file);
      } else {
         File basedir = m_project.getBasedir();

         return new File(basedir, file);
      }
   }

   private String getBuildArgument(String name, List<String> values) {
      StringBuffer sb = new StringBuffer(1024);

      if (values != null) {
         int len = values.size();

         for (int i = 0; i < len; i++) {
            if (i == 0) {
               sb.append(name).append('=');
            } else {
               sb.append(',');
            }

            sb.append(values.get(i).trim());
         }
      }

      return sb.toString();
   }

   private List<String> getBuildArguments() throws MojoExecutionException {
      List<String> as = new ArrayList<String>();

      as.add("-Xbootclasspath/p:" + flexHome + "/lib/xercesImpl.jar");

      if (jvmargs != null && jvmargs.length() > 0) {
         as.add(jvmargs);
      }

      as.add("-Dapplication.home=" + flexHome);
      as.add(getExecutableJar());
      as.add("-output=" + getOutput());
      as.add("-source-path=" + getSourcePath());

      if (flexConfig != null) {
         as.add("-load-config " + flexConfig);
      }

      if (parameters != null) {
         for (String parameter : parameters) {
            if (flexConfig != null && parameter.startsWith("-load-config")) {
               // ignore it
               continue;
            }

            as.add(parameter);
         }
      }

      as.add(getBuildArgument("-include-libraries+", getDependentLibraries()));

      if (args != null && args.length() > 0) {
         as.add(args);
      }

      if ("swf".equals(packaging) && !"asdoc".equals(classifier)) {
         as.add(sourceFile.toString());
      }

      return as;
   }

   private List<String> getDependentLibraries() throws MojoExecutionException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      List<String> libraries = new ArrayList<String>();

      if (dependencies != null) {
         // ArtifactFilter filter = "asdoc".equals(classifier) ? new
         // TypeClassiferArtifactFilter("swc", null)
         // : new TypeClassiferArtifactFilter("swc", classifier);

         // resolveDependencies(dependencies, artifacts, filter);
      }

      for (Artifact artifact : artifacts) {
         libraries.add(artifact.getFile().getPath());
         getLog().info("Adding dependency " + artifact.getFile());
      }

      return libraries;
   }

   private String getExecutableJar() throws MojoExecutionException {
      StringBuffer sb = new StringBuffer(1024);

      if ("flex2".equals(classifier) || "flex3".equals(classifier)) {
         if ("swf".equals(packaging)) {
            sb.append("-jar ").append(flexHome).append("/lib/mxmlc.jar");
         } else if ("swc".equals(packaging)) {
            sb.append("-jar ").append(flexHome).append("/lib/compc.jar");
         } else {
            throw new MojoExecutionException("Packaging(" + packaging + ") is not supported yet");
         }
      } else if ("asdoc".equals(classifier)) {
         sb.append("-classpath ").append(flexHome).append("/lib/asdoc.jar flex2.tools.ASDoc");
      } else {
         throw new MojoExecutionException("Unsupported Flex classifier: " + classifier
               + ", please use flex2, flex3 or asdoc instead");
      }

      return sb.toString();
   }

   private File getOutput() throws MojoExecutionException {
      File outputDirectory = getAbsoluteFile(m_project.getBuild().getOutputDirectory());

      if ("flex2".equals(classifier) || "flex3".equals(classifier)) {
         return new File(outputDirectory, m_project.getArtifactId() + "-" + m_project.getVersion() + "-" + classifier
               + "." + m_project.getPackaging());
      } else if ("asdoc".equals(classifier)) {
         return new File(outputDirectory, "asdoc");
      } else {
         throw new MojoExecutionException("Unsupported Flex classifier: " + classifier
               + ", please use flex2, flex3 or asdoc instead");
      }
   }

   private File getSourcePath() {
      File sourceDirectory = getAbsoluteFile(m_project.getBuild().getSourceDirectory());

      return sourceDirectory;
   }

   /**
    * Redirect the stdout & stderr of Java process to Maven log
    * 
    * @param process
    * @param stdin
    * @param stderr
    * @return exit code of Java process
    * @throws IOException
    */
   private int redirectOutput(Process process, InputStream stdin, InputStream stderr) throws IOException {
      BufferedReader in = new BufferedReader(new InputStreamReader(stdin));
      BufferedReader err = new BufferedReader(new InputStreamReader(stderr));
      boolean inEOF = false;
      boolean errEOF = false;

      while (!inEOF && !errEOF) {
         while (!inEOF && in.ready()) {
            String line = in.readLine();

            if (line == null) {
               inEOF = true;
               in.close();
               break;
            } else {
               getLog().info(line);
            }
         }

         while (!errEOF && err.ready()) {
            String line = err.readLine();

            if (line == null) {
               errEOF = true;
               err.close();
               break;
            } else {
               getLog().info(line);
            }
         }

         try {
            return process.exitValue();
         } catch (Exception e) {
            // ignore it
         }

         try {
            Thread.sleep(10);
         } catch (InterruptedException e) {
            break;
         }
      }

      return -1;
   }

   public void setDependencies(Dependency[] list) {
      dependencies = Arrays.asList(list);
   }

   public void setParameters(PlexusConfiguration config) {
      PlexusConfiguration[] children = config.getChildren();
      Set<String> done = new HashSet<String>(children.length);
      StringBuffer sb = new StringBuffer(1024);

      parameters = new ArrayList<String>();

      for (PlexusConfiguration child : children) {
         String name = child.getName();

         sb.setLength(0);
         sb.append('-').append(name);

         if (child.getChildCount() == 0) { // single element
            String value = child.getValue("");

            if (value.length() > 0) {
               sb.append(' ').append(value);
            }
         } else { // list
            PlexusConfiguration[] grandChildren = child.getChildren();
            boolean first = true;

            for (PlexusConfiguration grandChild : grandChildren) {
               String value = grandChild.getValue("");

               if (first) {
                  first = false;
                  sb.append('=');
               } else {
                  sb.append(',');
               }

               sb.append(value);
            }
         }

         parameters.add(sb.toString());
         done.add(name);

         // For dump config, we need to fix some path issues of Flex compiler
         if (name.equals("dump-config")) {
            m_dumpConfig = new File(child.getValue(""));
         }
      }
   }

   private void validate() throws MojoExecutionException {
      if (flexHome.exists()) {
         if (!new File(flexHome, "lib/mxmlc.jar").exists() || !new File(flexHome, "lib/compc.jar").exists()
               || !new File(flexHome, "lib/xercesImpl.jar").exists()) {
            throw new MojoExecutionException("Incorrect Flex home directory: " + flexHome);
         }
      } else {
         throw new MojoExecutionException("Flex home directory not found: " + flexHome);
      }

      if (!"swf".equalsIgnoreCase(packaging) && !"swc".equalsIgnoreCase(packaging)) {
         throw new MojoExecutionException("Only 'swf' or 'swc' is allowed as m_project packaging");
      }

      if (flexConfig != null && !flexConfig.exists()) {
         throw new MojoExecutionException("Flex config file(" + flexConfig + ") specified does not exist");
      }

      // we need a smart way for the dependencies
      if (dependencies != null && !"asdoc".equals(classifier)) {
         for (Dependency d : dependencies) {
            if (d.getClassifier() == null) {
               d.setClassifier(classifier);
            }
         }
      }
   }

   static final class TypeClassiferArtifactFilter implements ArtifactFilter {
      private String m_type = "jar";

      private String m_classifier;

      public TypeClassiferArtifactFilter(String type, String classifier) {
         m_type = type;
         m_classifier = (classifier == null ? "" : classifier);
      }

      public boolean include(Artifact artifact) {
         String classifier = (artifact.getClassifier() == null ? "" : artifact.getClassifier());

         return m_type.equals(artifact.getType()) && m_classifier.equals(classifier);
      }
   }
}
