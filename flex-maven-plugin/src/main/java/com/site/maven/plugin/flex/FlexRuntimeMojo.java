package com.site.maven.plugin.flex;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;

import com.site.maven.plugin.common.AbstractMojoWithDependency;

/**
 * Build Flex runtime environment for a web application.
 * 
 * Following steps will be taken:
 * <ul>
 * <li>Unzip Flex runtime</li>
 * <li>For each swf dependency in the dependencies, generate html file from the
 * template file and copy it to war source directory as well as swf file</li>
 * <li>Copy resource files if specified</li>
 * </ul>
 * 
 * @goal runtime
 * @author qwu
 */
public class FlexRuntimeMojo extends AbstractMojoWithDependency {
   /**
    * war source directory of current project
    * 
    * @parameter default-value="src/main/webapp"
    * @required
    */
   private String warSourceDirectory;

   /**
    * Flex runtime zip file
    * 
    * @parameter
    * @required
    */
   private File flexRuntime;

   /**
    * Template file name within Flex runtime.zip. It's used as a template to
    * create Flex landing page for each of swf files.
    * 
    * @parameter default-value="flex/main.html"
    * @required
    */
   private String template;

   /**
    * Resource files need to be copied and packaged into war file. Optional.
    * 
    * @parameter
    */
   private List<Resource> resources;

   /**
    * swf dependencies, which will be used to generate html template files and
    * .swf files will be copied to the war source directory
    * 
    * @parameter
    */
   private List<Dependency> dependencies;

   /**
    * If overwrite Flex runtime files if exists
    * 
    * @parameter default-value="false"
    * @required
    */
   private boolean overwrite;

   private Map<String, File> m_swfMap;

   private File m_workingDirectory;

   private void copyResource(File srcDir, String name, File dstDir) throws MojoExecutionException {
      File srcFile = new File(srcDir, name);
      File dstFile = new File(dstDir, name);

      if (!srcFile.exists()) {
         getLog().info("Resource not exist: " + srcFile);
      } else if (srcFile.isFile()) {
         try {
            if (!dstFile.getParentFile().exists()) {
               dstFile.getParentFile().mkdirs();
            }

            FileUtils.copyFile(srcFile, dstFile);
         } catch (IOException e) {
            throw new MojoExecutionException("Error when copying resource from " + srcFile + " to " + dstFile, e);
         }
      } else if (srcFile.isDirectory()) {
         String[] list = srcFile.list();

         if (list != null) {
            for (String item : list) {
               copyResource(srcFile, item, dstFile);
            }
         }
      }
   }

   private void copyResources() throws MojoExecutionException {
      if (resources != null) {
         File dstDir = new File(m_workingDirectory, template).getParentFile();

         for (Resource resource : resources) {
            copyResource(resource.getDir(), resource.getFile(), dstDir);
            getLog().info("Resource " + resource.getFile() + " copied from " + resource.getDir());
         }
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      validate();

      unzipFlexRuntime();

      for (Map.Entry<String, File> e : m_swfMap.entrySet()) {
         String swfName = e.getKey();
         File swfFile = e.getValue();

         generateHtmlFile(swfName, swfFile);
      }

      copyResources();
   }

   private void generateHtmlFile(String swfName, File swfFile) throws MojoExecutionException {
      File templateFile = new File(m_workingDirectory, template);

      if (!templateFile.exists()) {
         throw new MojoExecutionException("Template file(" + template + ") does not exist within runtime zip: "
               + flexRuntime);
      }

      File parent = templateFile.getParentFile();
      File htmlFile = new File(parent, swfName + ".html");

      try {
         String content = FileUtils.fileRead(templateFile);
         String replaced = content.replaceAll(Pattern.quote("@PROJECT_NAME@"), swfName);
         FileOutputStream fos = new FileOutputStream(htmlFile);

         IOUtil.copy(replaced, fos);
         fos.close();

         getLog().info("HTML file: " + htmlFile + " created from template " + template);
      } catch (IOException e) {
         throw new MojoExecutionException("Error when reading template file: " + templateFile, e);
      }

      try {
         String path = swfFile.getPath();
         int pos = path.lastIndexOf('.');
         File dstFile = new File(parent, swfName + "." + path.substring(pos + 1));

         FileUtils.copyFile(swfFile, dstFile);
         getLog().info("SWF file: " + dstFile + " copied from " + swfFile);
      } catch (IOException e) {
         throw new MojoExecutionException("Error when copying file: " + swfFile, e);
      }
   }

   public void setDependencies(Dependency[] list) {
      dependencies = Arrays.asList(list);
   }

   public void setResources(PlexusConfiguration configuration) {
      PlexusConfiguration[] children = configuration.getChildren("resource");

      resources = new ArrayList<Resource>();

      for (PlexusConfiguration child : children) {
         String dir = child.getAttribute("dir", null);
         String file = child.getValue("");

         if (dir != null) {
            resources.add(new Resource(new File(dir), file));
         } else {
            getLog().warn("dir attribute expected for " + child);
         }
      }
   }

   private void unzipFlexRuntime() throws MojoExecutionException {
      try {
         ZipFile zip = new ZipFile(flexRuntime);
         Enumeration<? extends ZipEntry> entries = zip.entries();

         while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            File file = new File(m_workingDirectory, entry.getName());

            if (entry.isDirectory()) {
               if (!file.exists()) {
                  file.mkdirs();
               }
               continue;
            }

            if (!file.getParentFile().exists()) {
               file.getParentFile().mkdirs();
            }

            if (!overwrite && file.exists() && file.length() == entry.getSize()) {
               continue;
            }

            InputStream zis = zip.getInputStream(entry);
            FileOutputStream fos = new FileOutputStream(file);

            IOUtil.copy(zis, fos);
            zis.close();
            fos.close();
         }

         getLog().info("Flex runtime unzipped");
      } catch (ZipException e) {
         throw new MojoExecutionException("Invalid zip file: " + flexRuntime, e);
      } catch (IOException e) {
         throw new MojoExecutionException("Error when doing unzip: " + flexRuntime, e);
      }
   }

   private void validate() throws MojoExecutionException {
      if (!flexRuntime.canRead()) {
         throw new MojoExecutionException("Flex runtime zip file not found: " + flexRuntime);
      }

      m_workingDirectory = new File(m_project.getBasedir(), warSourceDirectory);

      if (!m_workingDirectory.exists()) {
         throw new MojoExecutionException("War source directory not exist: " + warSourceDirectory);
      }

      m_swfMap = new TreeMap<String, File>();

      if (dependencies != null) {
         List<Artifact> artifacts = new ArrayList<Artifact>();

         // resolveDependencies(dependencies, artifacts, new
         // TypeClassiferArtifactFilter("swf", null));

         for (Artifact a : artifacts) {
            if (a.getFile() != null) {
               m_swfMap.put(a.getArtifactId(), a.getFile());
            }
         }
      }
   }

   static final class Resource {
      private File m_dir;

      private String m_file;

      public Resource(File dir, String file) {
         m_dir = dir;
         m_file = file;
      }

      public File getDir() {
         return m_dir;
      }

      public String getFile() {
         return m_file;
      }
   }
}
