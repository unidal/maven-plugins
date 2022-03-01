package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.meta.ModelMeta;
import org.unidal.maven.plugin.wizard.meta.ModelWizardBuilder;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.pom.ModelPomBuilder;

/**
 * Enable project to build model.
 * 
 * @goal model
 * @author Frankie Wu
 */
public class ModelMojo extends AbstractWizardMojo {
   /**
    * Wizard builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private ModelWizardBuilder m_wizardBuilder;

   /**
    * POM builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private ModelPomBuilder m_pomBuilder;

   /**
    * Model meta component
    * 
    * @component
    * @required
    * @readonly
    */
   private ModelMeta m_modelMeta;

   /**
    * @parameter expression="${outputDir}" default-value="${basedir}/src/main/resources/META-INF/dal/model"
    * @required
    */
   private String outputDir;

   public void execute() throws MojoExecutionException, MojoFailureException {
      MavenProject project = getProject();

      try {
         // prepare the wizard.xml and manifest.xml files
         Wizard wizard = m_wizardBuilder.build(project);

         // generate or regenerate the *-codegen.xml files
         MetaGenerator generator = new MetaGenerator();

         for (Model model : wizard.getModels()) {
            generator.generateModel(model);
         }

         // modify the pom.xml
         m_pomBuilder.build(project.getFile(), wizard);
      } catch (Exception e) {
         e.printStackTrace();
         throw new MojoExecutionException("Error when generating model meta: " + e, e);
      }
   }

   @Override
   protected String getWizardType() {
      return "model";
   }

   private class MetaGenerator {
      public void generateModel(Model model) throws SQLException, IOException {
         String name = model.getName();

         // codegen.xml file
         File sampleFile = new File(model.getSampleModel());
         Document codegenDoc = m_modelMeta.getCodegen(new FileReader(sampleFile));
         File outFile = new File(outputDir, name + "-codegen.xml");

         saveDocument(codegenDoc, outFile);

         // model.xml file
         File modelFile = new File(outputDir, name + "-model.xml");

         if (!modelFile.exists()) {
            Document modelDoc = m_modelMeta.getModel(model.getPackage());

            saveDocument(modelDoc, modelFile);
         }

         // manifest.xml file
         File manifestFile = new File(outputDir, name + "-manifest.xml");
         Document manifestDoc = m_modelMeta.getManifest(outFile.getName(), modelFile.getName());

         saveDocument(manifestDoc, manifestFile);
      }

      private void saveDocument(Document codegen, File file) throws IOException {
         File folder = file.getParentFile();

         if (!folder.exists()) {
            folder.mkdirs();
         }

         FileWriter writer = new FileWriter(file);

         try {
            new XMLOutputter(Format.getPrettyFormat()).output(codegen, writer);

            getLog().info("File " + file.getCanonicalPath() + " generated.");
         } finally {
            writer.close();
         }
      }
   }
}
