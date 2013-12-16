package org.unidal.maven.plugin.uml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.GeneratedImage;
import net.sourceforge.plantuml.ISourceFileReader;
import net.sourceforge.plantuml.Option;
import net.sourceforge.plantuml.SourceFileReader2;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Splitters;

/**
 * Generate UML diagram from text files.
 * 
 * @goal generate
 * @phase generate-resources
 * @author Frankie Wu
 */
public class GenerateMojo extends AbstractMojo {
   /**
    * @parameter expression="${source}"
    * @required
    */
   private String source;

   /**
    * @parameter expression="${output}"
    *            default-value="${project.build.outputDirectory}"
    * @required
    */
   private String output;

   /**
    * @parameter expression="${imageType}" default-value="png"
    * @required
    */
   private String imageType;

   /**
    * @parameter expression="${verbose}" default-value="false"
    * @required
    */
   private boolean verbose;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final File sourceDir = new File(source).getCanonicalFile();
         final File targetDir = new File(output).getCanonicalFile();
         final List<String> files = new ArrayList<String>();

         if (verbose) {
            getLog().info(String.format("Scanning %s ...", sourceDir));
         }

         Scanners.forDir().scan(sourceDir, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (path.endsWith(".uml")) {
                  if (verbose) {
                     getLog().info(String.format("Found %s", new File(base, path)));
                  }

                  files.add(path);
               }

               return Direction.DOWN;
            }
         });

         List<String> types = Splitters.by(',').noEmptyItem().trim().split(imageType);
         int count = 0;

         for (String type : types) {
            FileFormat format = getFileFormat(type);

            if (format != null) {
               for (String file : files) {
                  count += generateUml(sourceDir, targetDir, file, format);
               }
            }
         }

         getLog().info(String.format("%s UML diagrams generated", count));
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating UML diagram!", e);
      }
   }

   private int generateUml(File from, File to, String path, FileFormat format) throws Exception {
      File source = new File(from, path);
      File target = new File(to, path.substring(0, path.length() - 4) + format.getFileSuffix());
      Option option = new Option();

      option.setFileFormat(format);
      target.getParentFile().mkdirs();

      ISourceFileReader reader = new SourceFileReader2(option.getDefaultDefines(), source, target, option.getConfig(),
            option.getCharset(), option.getFileFormatOption());
      List<GeneratedImage> images = reader.getGeneratedImages();

      if (verbose) {
         for (GeneratedImage image : images) {
            getLog().info(String.format("File generated: %s", image.getPngFile()));
         }
      }

      return 1;
   }

   private FileFormat getFileFormat(String type) {
      try {
         return FileFormat.valueOf(type.toUpperCase());
      } catch (Exception e) {
         return null;
      }
   }
}
