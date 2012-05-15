package com.site.maven.plugin.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.site.codegen.meta.ModelMeta;
import com.site.helper.Files;
import com.site.maven.plugin.common.PropertyProviders;

/**
 * DAL Metadata generator for Model
 * 
 * @goal dal-model-meta
 * @author Frankie Wu
 */
public class DalModelMetaMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * XML meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected ModelMeta m_meta;

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
    */
   protected String inputFile;

   /**
    * @parameter expression="${outputDir}"
    *            default-value="${basedir}/src/main/resources/META-INF/dal/model"
    * @required
    */
   protected String outputDir;

   /**
    * @parameter expression="${packageName}"
    */
   protected String packageName;

   /**
    * @parameter expression="${prefix}"
    */
   protected String prefix;

   private String defaultPackageName() {
      if (packageName != null) {
         return packageName;
      }

      String groupId = m_project.getGroupId();
      String artifactId = m_project.getArtifactId();

      return (groupId + "." + artifactId + ".model").replace('-', '.');
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      String f = getProperty(inputFile, "inputFile", "Sample XML file path:", null);

      if (f == null) {
         throw new MojoExecutionException("please provide sample XML file path via -DinputFile=...");
      }

      try {
         File inFile = getFile(f);
         String xml = Files.forIO().readFrom(inFile, "utf-8");
         Document doc = m_meta.getCodegen(new StringReader(xml));
         String rootName = getRootEntityName(doc);
         String p = getProperty(prefix, "prefix", "Prefix name of target files:", rootName);
         File outDir = getFile(outputDir);
         File outFile = new File(outDir, p == null ? "codegen.xml" : p + "-codegen.xml");

         if (!outDir.exists()) {
            outDir.mkdirs();
         }

         saveFile(doc, outFile);

         File modelFile = new File(outDir, p == null ? "model.xml" : p + "-model.xml");

         if (!modelFile.exists()) {
            String n = getProperty(packageName, "packageName", "Package name of generated model:", defaultPackageName());
            Document model = m_meta.getModel(n);

            saveFile(model, modelFile);

            File testResource = new File(m_project.getBasedir(), "src/test/resources");
            File testModel = new File(testResource, n.replace('.', '/') + "/" + rootName + ".xml");

            if (!testModel.exists()) {
               testModel.getParentFile().mkdirs();
               Files.forIO().writeTo(testModel, xml);
               getLog().info("File " + testModel.getCanonicalPath() + " generated.");
            }
         }

         File manifestFile = new File(outDir, p == null ? "manifest.xml" : p + "-manifest.xml");

         if (!manifestFile.exists()) {
            Document manifest = m_meta.getManifest(outFile.getName(), modelFile.getName());

            saveFile(manifest, manifestFile);
         }

      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating model meta: " + e, e);
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

   private String getProperty(String value, String name, String prompt, String defaultValue) {
      if (value != null) {
         return value;
      } else {
         return PropertyProviders.fromConsole().forString(name, prompt, defaultValue, null);
      }
   }

   @SuppressWarnings({ "unchecked" })
   private String getRootEntityName(Document doc) {
      List<Element> children = doc.getRootElement().getChildren();

      if (!children.isEmpty()) {
         Element first = children.get(0);

         return first.getAttributeValue("name");
      }

      return null;
   }

   private void saveFile(Document codegen, File file) throws IOException {
      Format format = Format.getPrettyFormat();
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(codegen, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }
}
