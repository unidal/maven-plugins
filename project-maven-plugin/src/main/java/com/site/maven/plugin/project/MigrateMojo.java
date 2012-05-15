package com.site.maven.plugin.project;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.site.helper.Files;
import com.site.helper.Joiners;
import com.site.helper.Splitters;
import com.site.maven.plugin.common.PropertyProviders;

/**
 * Migrate all source files from one project to another project using a
 * different package base.If current project is a pom project, then all its
 * module project will be migrated as well.
 * 
 * @goal migrate
 */
public class MigrateMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * @parameter expression="${sourcePackage}"
    */
   private String sourcePackage;

   /**
    * @parameter expression="${targetPackage}"
    */
   private String targetPackage;

   /**
    * @parameter expression="${targetDir}"
    *            default-value="target/${project.artifactId}"
    * @required
    */
   private String targetDir;

   /**
    * @parameter expression="${verbose}" default-value="false"
    * @required
    */
   private boolean verbose;

   private String m_reversedSourcePackage;

   private String m_reversedTargetPackage;

   private int m_success;

   private int m_changed;

   private int m_failure;

   public void execute() throws MojoExecutionException, MojoFailureException {
      sourcePackage = PropertyProviders.fromConsole().forString("sourcePackage", "Java source package:", sourcePackage, null);
      targetPackage = PropertyProviders.fromConsole().forString("targetPackage", "Java target package:", targetPackage, null);

      m_reversedSourcePackage = reversePackage(sourcePackage);
      m_reversedTargetPackage = reversePackage(targetPackage);

      try {
         long start = System.currentTimeMillis();
         File baseDir = project.getBasedir().getCanonicalFile();
         File currentDir = new File(".").getCanonicalFile();
         File targetBase;

         if (baseDir.equals(currentDir)) { // current project
            if (targetDir.startsWith("/")) {
               targetBase = new File(targetDir);
            } else {
               targetBase = new File(currentDir, targetDir);
            }
         } else {
            String basePath = baseDir.getCanonicalPath();
            String currentPath = currentDir.getCanonicalPath();

            if (basePath.startsWith(currentPath)) { // sub-module
               String relativePath = basePath.substring(currentPath.length());

               if (targetDir.startsWith("/")) {
                  targetBase = new File(targetDir, relativePath);
               } else {
                  targetBase = new File(new File(currentDir, targetDir), relativePath);
               }
            } else {
               throw new IllegalStateException(String.format(
                     "Invalid directory to run maven build! base: %s, current: %s.", basePath, currentPath));
            }
         }

         m_success = 0;
         m_changed = 0;
         m_failure = 0;

         for (String source : (List<String>) project.getCompileSourceRoots()) {
            migrateSource(baseDir, targetBase, source);
         }

         for (String testSource : (List<String>) project.getTestCompileSourceRoots()) {
            migrateSource(baseDir, targetBase, testSource);
         }

         for (Resource resource : (List<Resource>) project.getResources()) {
            migrateSource(baseDir, targetBase, resource.getDirectory());
         }

         for (Resource testResource : (List<Resource>) project.getTestResources()) {
            migrateSource(baseDir, targetBase, testResource.getDirectory());
         }

         if (new File(baseDir, "pom.xml").exists()) {
            migrateFile(new File(baseDir, "pom.xml"), new File(targetBase, "pom.xml"));
         }

         if ("war".equals(project.getPackaging())) {
            migrateSource(baseDir, targetBase, "src/main/webapp");
         }

         long timeUsed = System.currentTimeMillis() - start;

         getLog().info(
               String.format("%s files migrated in %s ms, with %s changed and %s failures.", m_success, timeUsed,
                     m_changed, m_failure));
      } catch (Exception e) {
         String message = String.format(
               "Error when migrating project[sourcePackage: %s, targetPackage: %s, targetDir: %s].", sourcePackage,
               targetPackage, targetDir);

         getLog().error(message);
         throw new MojoFailureException(message, e);
      }
   }

   protected void migrateDirectory(File source, File target, String sourcePath, String sourcePackageName) {
      String targetPath;

      if (sourcePackageName != null && sourcePackageName.startsWith(sourcePackage)) {
         String targetPackageName = targetPackage + sourcePackageName.substring(sourcePackage.length());

         targetPath = targetPackageName.replace('.', '/');
      } else {
         targetPath = sourcePath;
      }

      File base = sourcePath == null ? source : new File(source, sourcePath);
      String[] names = base.list();

      if (names != null) {
         for (String name : names) {
            File file = new File(base, name);

            if (file.isDirectory()) {
               String newPath = sourcePath == null ? name : sourcePath + "/" + name;
               String newPackageName = sourcePackageName == null ? name : sourcePackageName + "." + name;

               migrateDirectory(source, target, newPath, newPackageName);
            } else if (file.isFile()) {
               migrateFile(file, new File(target, targetPath + "/" + name));
            }
         }
      }
   }

   protected void migrateFile(File source, File target) {
      try {
         String original = Files.forIO().readFrom(source, "utf-8");
         String migrated = replaceAll(original, sourcePackage, targetPackage);

         // for tld uri, cookie domain name etc.
         migrated = replaceAll(migrated, m_reversedSourcePackage, m_reversedTargetPackage);

         Files.forIO().writeTo(target, migrated);
         m_success++;

         boolean changed = !original.equals(migrated);

         if (changed) {
            m_changed++;
         }

         if (verbose) {
            if (changed) {
               getLog().info(String.format("File(%s) migrated, content length is %s.", target, target.length()));
            } else {
               getLog().info(String.format("File(%s) copied, content length is %s.", target, target.length()));
            }
         }
      } catch (Exception e) {
         getLog().warn(String.format("Error when migrating file(%s)!", source), e);
         m_failure++;
      }
   }

   protected void migrateSource(File sourceBase, File targetBase, String path) {
      String basePath = sourceBase.getPath();
      String relativePath;

      if (path.startsWith(basePath)) {
         relativePath = path.substring(basePath.length());
      } else {
         relativePath = path;
      }

      File source = new File(sourceBase, relativePath);
      File target = new File(targetBase, relativePath);

      migrateDirectory(source, target, null, null);
   }

   protected String replaceAll(String source, String fromToken, String toToken) {
      int toLength = toToken.length();
      int fromLength = fromToken.length();
      StringBuilder sb = new StringBuilder(source.length() + (toLength < fromLength ? 0 : toLength - fromLength) * 30);
      int offset = 0;
      int index = source.indexOf(fromToken, offset);

      while (true) {
         if (index == -1) {
            sb.append(source.substring(offset));
            break;
         } else {
            sb.append(source.substring(offset, index));
            sb.append(toToken);

            offset = index + fromLength;
            index = source.indexOf(fromToken, offset);
         }
      }

      return sb.toString();
   }

   protected String reversePackage(String packageName) {
      List<String> parts = Splitters.by('.').split(packageName);

      Collections.reverse(parts);

      return Joiners.by('.').join(parts);
   }
}
