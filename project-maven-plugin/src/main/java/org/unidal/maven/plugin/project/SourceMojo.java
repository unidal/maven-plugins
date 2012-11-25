package org.unidal.maven.plugin.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;

/**
 * Add generated sources to eclipse class path (.classpath).
 * 
 * @goal source
 */
public class SourceMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   public void execute() throws MojoExecutionException, MojoFailureException {
      String outputDir = m_project.getBuild().getDirectory();
      final Set<String> paths = new LinkedHashSet<String>();

      Scanners.forDir().scan(new File(outputDir), new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.startsWith("generated-")) {
               if (path.endsWith(".java")) {
                  int pos = path.indexOf("/src/");

                  if (pos < 0) {
                     int off = path.indexOf('/');

                     if (off > 0) {
                        paths.add("target/" + path.substring(0, off));
                     }
                  } else {
                     // src/main/java
                     // src/test/java
                     // src/main/resources
                     // src/test/resources
                     int off = path.indexOf("/java/", pos);

                     if (off < 0) {
                        off = path.indexOf("/resources/", pos);

                        if (off > 0) {
                           paths.add("target/" + path.substring(0, off + "/resources".length()));
                        }
                     } else {
                        paths.add("target/" + path.substring(0, off + "/java".length()));
                     }
                  }
               } else {
                  return Direction.DOWN;
               }
            }

            return Direction.NEXT;
         }
      });

      try {
         modifyClasspathFile(paths);
      } catch (Exception e) {
         throw new MojoExecutionException("Failed to add generated source to classpath!", e);
      }
   }

   @SuppressWarnings("unchecked")
   protected void modifyClasspathFile(Set<String> paths) throws Exception {
      File file = new File(m_project.getBasedir(), ".classpath");

      if (!file.exists()) {
         throw new IllegalStateException("Please run 'mvn eclipse:eclipse' first!");
      }

      Document doc = new SAXBuilder().build(file);
      Element classpath = doc.getRootElement();
      List<Element> entries = classpath.getChildren();

      for (Element entry : entries) {
         String kind = entry.getAttributeValue("kind");
         String path = entry.getAttributeValue("path");

         if (kind != null && kind.equals("src")) {
            if (path != null) {
               paths.remove(path);
            }
         }
      }

      boolean dirty = false;

      for (String path : paths) {
         Element entry = new Element("classpathentry");

         entry.setAttribute("kind", "src");
         entry.setAttribute("path", path);
         classpath.addContent(entry);

         dirty = true;
         getLog().info(String.format("Adding %s to source folder ...", path));
      }

      if (dirty) {
         saveXml(doc, file);
      }
   }

   protected void saveXml(Document doc, File file) throws IOException {
      File parent = file.getCanonicalFile().getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(doc, writer);
         getLog().info("File " + file.getCanonicalPath() + " saved.");
      } finally {
         writer.close();
      }
   }
}
