package com.site.maven.plugin.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.site.codegen.meta.XmlMeta;

/**
 * DAL Metadata generator for XML
 * 
 * @goal dal-xml-meta
 * @author Frankie Wu
 */
public class DalXmlMetaMojo extends AbstractMojo {
   /**
    * XML meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected XmlMeta m_meta;

   /**
    * Current project base directory
    * 
    * @parameter expression="${basedir}"
    * @required
    * @readonly
    */
   protected File baseDir;

   /**
    * @parameter expression="${inputFile}"
    * @required
    */
   protected String inputFile;

   /**
    * @parameter expression="${outputFile}"
    *            default-value="src/main/resources/META-INF/dal/xml/codegen.xml"
    * @required
    */
   protected String outputFile;

   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         File inFile = getFile(inputFile);
			Reader reader = new InputStreamReader(new FileInputStream(inFile), "utf-8");
         Element root = m_meta.getMeta(reader);
         XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
         File outFile = getFile(outputFile);

         if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
         }

         outputter.output(root, new FileWriter(outFile));
         getLog().info("File " + outFile.getCanonicalPath() + " generated.");
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating XML meta: " + e, e);
      }
   }

   private File getFile(String path) {
      File file;

      if (path.startsWith("/") || path.indexOf(':') > 0) {
         file = new File(path);
      } else {
         file = new File(baseDir, path);
      }

      return file;
   }
}
