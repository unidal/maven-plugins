package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.ZipEntryMatcher;

/**
 * Prepare war resource from web modules.
 * 
 * @goal web-module
 * @phase prepare-package
 * @requiresDependencyResolution compile
 * @author Frankie Wu
 */
public class WebModuleMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   protected boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   protected boolean debug;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         String webappDir = m_project.getBuild().getDirectory() + "/" + m_project.getBuild().getFinalName();
         List<String> classpathElements = m_project.getCompileClasspathElements();
         WebModuleResourceManager manager = new WebModuleResourceManager(webappDir, classpathElements);

         manager.process();
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating plexus components descriptor!", e);
      }
   }

   class WebModuleResourceManager {
      private File m_webappDir;

      private List<String> m_classpathElements;

      public WebModuleResourceManager(String webappDir, List<String> classpathElements) throws Exception {
         m_webappDir = new File(webappDir).getCanonicalFile();
         m_classpathElements = classpathElements;
      }

      public void process() throws Exception {
         for (String classpathElement : m_classpathElements) {
            processElement(new File(classpathElement).getCanonicalFile());
         }
      }

      private void processElement(File classpathElement) throws IOException {
         if (!classpathElement.isDirectory()) {
            List<String> entries = Scanners.forJar().scan(classpathElement, new ZipEntryMatcher() {
               @Override
               public Direction matches(ZipEntry entry, String path) {
                  if (path.startsWith("WEB-MODULE")) {
                     return Direction.MATCHED;
                  }

                  return Direction.DOWN;
               }
            });

            for (String entry : entries) {
               URL url = new URL("jar:file:" + classpathElement + "!/" + entry);
               File dst = new File(m_webappDir, entry.substring("WEB-MODULE".length()));

               dst.getParentFile().mkdirs();

               if (!dst.exists()) {
                  Files.forIO().copy(url.openStream(), new FileOutputStream(dst));
               }
            }
         } else {
            File base = new File(classpathElement, "WEB-MODULE");

            if (base.exists()) {
               final List<String> pathes = new ArrayList<String>();

               Scanners.forDir().scan(base, new FileMatcher() {
                  @Override
                  public Direction matches(File base, String path) {
                     pathes.add(path);
                     return Direction.DOWN;
                  }
               });

               for (String path : pathes) {
                  File src = new File(base, path);
                  File dst = new File(m_webappDir, path);

                  if (src.isDirectory()) {
                     dst.mkdirs();
                  } else if (!dst.exists()) {
                     Files.forDir().copyFile(src, dst);
                  }
               }
            }
         }
      }
   }
}
