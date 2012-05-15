package com.site.maven.plugin.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * Unzip a package to a specified location and set property if necessary
 * 
 * @goal unzip
 */
public class UnzipMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * Where is the zip file located
    * 
    * @parameter expression="${zipFile}"
    * @required
    */
   private File zipFile;

   /**
    * Where the unzipped files will be stored to
    * 
    * @parameter expression="${outputDirectory}"
    * @required
    */
   private File outputDirectory;

   /**
    * Whihc property name will be set if specified. It's optional
    * 
    * @parameter expression="${propertyName}"
    */
   private String propertyName;

   /**
    * Overwrite the existing files or not
    * 
    * @parameter expression="${overwrite}" default-value="false"
    */
   private boolean overwrite;

   /**
    * Verbose output
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   private boolean verbose;

   public void execute() throws MojoExecutionException, MojoFailureException {
      validate();
      unzip();
      setProperty();
   }

   private void setProperty() {
      if (propertyName != null && propertyName.length() > 0) {
         String path = outputDirectory.getAbsolutePath();

         project.getProperties().setProperty(propertyName, path);
         getLog().info("Set property " + propertyName + "=" + path);
      }
   }

   private void unzip() throws MojoExecutionException {
      try {
         ZipFile zip = new ZipFile(zipFile);
         Enumeration<? extends ZipEntry> entries = zip.entries();
         int total = 0;
         int created = 0;

         getLog().info("Expanding " + zipFile + " into " + outputDirectory);

         while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(outputDirectory, entry.getName());

            if (entry.isDirectory()) {
               if (!file.exists()) {
                  file.mkdirs();
               }
               continue;
            }

            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }

            total++;

            if (!overwrite && file.exists() && file.length() == entry.getSize()) {
               continue;
            }

            if (verbose) {
               getLog().info("   creating " + file);
            }

            InputStream zis = zip.getInputStream(entry);
            FileOutputStream fos = new FileOutputStream(file);

            IOUtil.copy(zis, fos);
            zis.close();
            fos.close();
            created++;
         }

         getLog().info(created + " of " + total + " files created");
      } catch (ZipException e) {
         throw new MojoExecutionException("Invalid zip file: " + zipFile, e);
      } catch (IOException e) {
         throw new MojoExecutionException("Error when unzipping: " + zipFile, e);
      }
   }

   private void validate() throws MojoExecutionException {
      if (!zipFile.exists()) {
         throw new MojoExecutionException("Zip file does not exist: " + zipFile);
      }

      if (!outputDirectory.exists()) {
         outputDirectory.mkdirs();
      }
   }
}
