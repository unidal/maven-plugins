package com.site.maven.plugin.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;

/**
 * Package all source files in the project for backup purpose.
 * 
 * @aggregator true
 * @goal package
 */
public class PackageMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * @parameter expression="${excludeDirs}" default-value="ws"
    */
   private String excludeDirs;

   private static final String[] DEFAULT_INCLUDES = new String[] { "**/*" };

   public void execute() throws MojoExecutionException, MojoFailureException {
      File baseDir = project.getBasedir();
      String targetDirectory = project.getBuild().getDirectory();
      String outputFile = targetDirectory + "/" + baseDir.getName() + ".sources.jar";

      try {
         Archiver a = new ZipArchiver();
         String[] list = baseDir.list();
         List<String> dirs = getExcludedDirs();
         String[] excludes = getDefaultExcludes();

         for (String item : list) {
            if (item.startsWith(".") || dirs.contains(item)) {
               continue;
            }

            File file = new File(baseDir, item);

            if (file.isFile()) {
               a.addFile(file, item);
            } else if (file.isDirectory()) {
               if (!file.getPath().equals(targetDirectory)) {
                  a.addDirectory(file, item + "/", DEFAULT_INCLUDES, excludes);
               }
            }
         }

         a.setDestFile(new File(outputFile));
         a.createArchive();

         getLog().info(String.format("File(%s) created.", a.getDestFile().getCanonicalPath()));
      } catch (Exception e) {
         throw new MojoExecutionException("Can't create archiver: " + outputFile, e);
      }
   }

   private String[] getDefaultExcludes() {
      List<String> excludes = new ArrayList<String>();

      excludes.add(".*");
      excludes.add("**/.*");
      excludes.add("**/.*/**");
      excludes.add("**/WEB-INF/tmp");
      excludes.add("**/target");
      excludes.add("**/target/**/*");
      excludes.add("**/*.jar");

      for (String exclude : FileUtils.getDefaultExcludes()) {
         excludes.add(exclude);
      }

      return excludes.toArray(new String[0]);
   }

   private List<String> getExcludedDirs() {
      List<String> excludes = new ArrayList<String>();

      for (String exclude : excludeDirs.split(",")) {
         excludes.add(exclude);
      }

      return excludes;
   }
}
