package com.site.maven.plugin.webres;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.unidal.webres.helper.Files;
import org.unidal.webres.helper.Files.AutoClose;
import org.unidal.webres.helper.Scanners;
import org.unidal.webres.helper.Scanners.IDirectoryProvider;
import org.unidal.webres.helper.Scanners.UrlMatcher;

/**
 * Clone the Apache directory into local file system.
 * 
 * @goal download
 * @author Frankie Wu
 */
public class DownloadMojo extends AbstractMojo {
   /**
    * @component
    * @Required
    * @readonly
    */
   protected ArchiverManager m_archiverManager;

   /**
    * Base URL to start clone
    * 
    * @parameter expression="${baseUrl}"
    * @required
    */
   protected URL baseUrl;

   /**
    * Output directory for result files
    * 
    * @parameter expression="${outputDir}"
    *            default-value="${basedir}/target/download"
    * @required
    */
   protected File outputDir;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="true"
    */
   protected boolean verbose;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final File zipFile = getZipFile();
         final File tmpDir = new File(zipFile.getParentFile(), "tmp");
         final Archiver archiver = m_archiverManager.getArchiver(zipFile);

         Scanners.forUrl().scan(baseUrl, new ApacheDirectoryProvider(), new UrlMatcher() {
            @Override
            public boolean isDirEligible() {
               return false;
            }

            @Override
            public Direction matches(URL base, String path) {
               try {
                  File file = new File(tmpDir, path);

                  if (!file.exists()) {
                     URL url = new URL(base, path);

                     file.getParentFile().mkdirs();
                     Files.forIO().copy(url.openStream(), new FileOutputStream(file), AutoClose.INPUT_OUTPUT);

                     if (verbose) {
                        getLog().info(String.format("Found file %s ...", path));
                     }
                  }

                  archiver.addFile(file, path);
               } catch (Exception e) {
                  e.printStackTrace();
               }

               return Direction.NEXT;
            }
         });

         archiver.setDestFile(zipFile);
         archiver.createArchive();

         getLog().info(String.format("File %s created", zipFile.getCanonicalFile()));
      } catch (Exception e) {
         getLog().error(e);

         throw new MojoFailureException(e.toString(), e);
      }
   }

   protected File getZipFile() {
      String path = baseUrl.getPath();
      String name;

      if (path.endsWith("/")) {
         int pos = path.lastIndexOf('/', path.length() - 2);

         name = path.substring(pos, path.length() - 1);
      } else {
         int pos = path.lastIndexOf('/');

         name = path.substring(pos);
      }

      File dir = new File(outputDir, name);
      File zipFile = new File(dir, name + ".zip");

      zipFile.getParentFile().mkdirs();
      return zipFile;
   }

   static class ApacheDirectoryProvider implements IDirectoryProvider<URL> {
      private MessageFormat m_format = new MessageFormat("{3}<a href=\"{0}\"{2}>{1}");

      @Override
      public boolean isDirectory(URL base, String path) {
         return path.endsWith("/");
      }

      @Override
      public List<String> list(URL base, final String path) throws IOException {
         List<String> list = new ArrayList<String>();
         String content = Files.forIO().readFrom(new URL(base, path).openStream(), "utf-8");

         try {
            while (true) {
               Object[] parts = m_format.parse(content);
               String link = (String) parts[0];

               content = (String) parts[1];

               if (!link.startsWith("?") && !link.startsWith("/") && !link.startsWith("http")) {
                  list.add(link);
               }
            }
         } catch (ParseException e) {
            // do nothing here
         }

         return list;
      }
   }
}