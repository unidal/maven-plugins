package com.site.maven.plugin.misc;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Delete all of duplicated files from the target directory if they also exist
 * in the source directory.
 * 
 * <pre>
 * Exmaple:
 * &lt;plugin&gt;
 *   &lt;groupId&gt;com.site.maven.plugins&lt;/groupId&gt;
 *   &lt;artifactId&gt;maven-misc-plugin&lt;/artifactId&gt;
 *       &lt;configuration&gt;
 *               &lt;targetDirectory&gt;path/to/generated/source&lt;/targetDirectory&gt;
 *               &lt;excludes&gt;
 *                       &lt;exclude&gt;parkage.name&lt;/exclude&gt;
 *                       &lt;exclude&gt;path/to/sub/dir&lt;/exclude&gt;
 *               &lt;/excludes&gt;
 *       &lt;/configuration&gt;
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @goal xdelete
 */
public class XdeleteMojo extends AbstractMojo {
   /**
    * Source directory to check files with
    * 
    * @parameter expression="${project.build.sourceDirectory}"
    * @required
    */
   private File sourceDirectory;

   /**
    * Target directory to delete files from
    * 
    * @parameter
    * @required
    */
   private File targetDirectory;

   /**
    * Which directories or files should also be deleted from target directory
    * 
    * @parameter
    */
   private List<String> excludes;

   /**
    * If or not delete empty directory
    * 
    * @parameter default-value="true"
    */
   private boolean deleteEmptyDirectory;

   private int deleteFiles(File source, File target) {
      if (!target.exists()) {
         return 0;
      }

      int total = 0;

      if (target.isDirectory()) { // Dir
         String[] list = target.list();

         if (list != null) {
            for (String name : list) {
               File src = (source == null ? null : new File(source, name));
               File dst = new File(target, name);

               total += deleteFiles(src, dst);
            }

            if (deleteEmptyDirectory) {
               target.delete();
            }
         }
      } else if (target.isFile()) { // File
         if (source == null || source.exists()) {
            if (target.delete()) {
               getLog().debug("File " + target + " deleted");

               total++;
            }
         }
      }

      return total;
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      int total = 0;

      getLog().info("Source directory is " + sourceDirectory);

      total += deleteFiles(sourceDirectory, targetDirectory);

      if (excludes != null) {
         for (String exclude : excludes) {
            String subDirName = exclude.replace('.', '/');
            File dst = new File(targetDirectory, subDirName);

            total += deleteFiles(null, dst);
         }
      }

      getLog().info(total + " files deleted from " + targetDirectory);
   }
}
