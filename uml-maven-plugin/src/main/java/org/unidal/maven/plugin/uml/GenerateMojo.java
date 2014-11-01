package org.unidal.maven.plugin.uml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.SourceStringReader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.helper.Files;
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

         if (verbose) {
            getLog().info(String.format("Generating UML to %s ...", targetDir));
         }

         List<String> types = Splitters.by(',').noEmptyItem().trim().split(imageType);
         int count = 0;

         for (String type : types) {
            FileFormat format = getFileFormat(type);

            if (format != null) {
               for (String file : files) {
                  File sourceFile = new File(sourceDir, file);
                  File targetFile = new File(targetDir, file.substring(0, file.length() - 4) + format.getFileSuffix());

                  try {
                     String uml = Files.forIO().readFrom(sourceFile, "utf-8");
                     byte[] content = generateImage(uml, type);

                     if (content != null) {
                        Files.forIO().writeTo(targetFile, content);
                        count++;

                        if (verbose) {
                           getLog().info(String.format("File generated: %s", targetFile));
                        }
                     } else {
                        getLog().warn(String.format("Error when generating : %s", sourceFile));
                     }
                  } catch (Exception e) {
                     getLog().warn(String.format("Error when generating : %s", sourceFile), e);
                  }
               }
            }
         }

         getLog().info(String.format("%s UML diagrams generated", count));
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating UML diagram!", e);
      }
   }

   private byte[] generateImage(String uml, String type) throws IOException {
      if (!uml.trim().startsWith("@startuml")) {
         uml = "@startuml\n" + uml;
      }

      if (!uml.trim().endsWith("@enduml")) {
         uml = uml + "\n@enduml";
      }

      SourceStringReader reader = new SourceStringReader(uml);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
      FileFormat format = FileFormat.PNG;

      for (FileFormat e : FileFormat.values()) {
         if (e.name().equalsIgnoreCase(type)) {
            format = e;
            break;
         }
      }

      reader.generateImage(baos, new FileFormatOption(format));

      if (!hasError(reader.getBlocks())) {
         return baos.toByteArray();
      } else {
         return null;
      }
   }

   private boolean hasError(List<BlockUml> blocks) throws IOException {
      for (BlockUml b : blocks) {
         if (b.getDiagram() instanceof PSystemError) {
            return true;
         }
      }

      return false;
   }

   private FileFormat getFileFormat(String type) {
      try {
         return FileFormat.valueOf(type.toUpperCase());
      } catch (Exception e) {
         getLog().warn("Invalid type: " + type);
         return null;
      }
   }
}
