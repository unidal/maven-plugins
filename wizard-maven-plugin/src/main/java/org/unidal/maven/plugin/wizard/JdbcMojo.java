package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.meta.TableMeta;
import org.unidal.maven.plugin.wizard.meta.JdbcWizardBuilder;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Table;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.pom.JdbcPomBuilder;

/**
 * Enable project to access database via java jdbc.
 * 
 * @goal jdbc
 * @author Frankie Wu
 */
public class JdbcMojo extends AbstractWizardMojo {
   /**
    * Wizard builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private JdbcWizardBuilder m_wizardBuilder;

   /**
    * POM builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private JdbcPomBuilder m_pomBuilder;

   /**
    * XSL code generator implementation
    * 
    * @component role="org.unidal.codegen.generator.Generator" role-hint="wizard-jdbc"
    * @required
    * @readonly
    */
   private Generator m_generator;

   /**
    * Table meta component
    * 
    * @component
    * @required
    * @readonly
    */
   private TableMeta m_tableMeta;

   // ---

   /**
    * @parameter expression="${outputDir}" default-value="${basedir}/src/main/resources/META-INF/dal/jdbc"
    * @required
    */
   private String outputDir;

   private Connection m_conn;

   public void execute() throws MojoExecutionException, MojoFailureException {
      MavenProject project = getProject();

      try {
         // prepare the wizard.xml and manifest.xml files
         Wizard wizard = m_wizardBuilder.build(project);

         m_conn = m_wizardBuilder.getConnection();

         // generate or regenerate the *-codegen.xml files
         MetaGenerator generator = new MetaGenerator();

         for (Jdbc jdbc : wizard.getJdbcs()) {
            for (Group group : jdbc.getGroups()) {
               generator.generate(group);
            }
         }

         m_generator.generate(super.createGenerateContext());

         // modify the pom.xml
         m_pomBuilder.build(project.getFile(), wizard);
      } catch (Exception e) {
         e.printStackTrace();
         throw new MojoExecutionException("Error when generating model meta: " + e, e);
      }
   }

   @Override
   protected String getWizardType() {
      return "jdbc";
   }

   private class MetaGenerator {
      public void generate(Group group) throws SQLException, IOException {
         generateCodegenFile(group);
         generateModelFile(group);
         generateManifestFile(group);
      }

      private void generateCodegenFile(Group group) throws SQLException, IOException {
         List<Table> tables = group.getTables();
         Element entities = new Element("entities");
         File file = new File(outputDir, group.getName() + "-codegen.xml");

         Collections.sort(tables, new Comparator<Table>() {
            @Override
            public int compare(Table t1, Table t2) {
               return t1.getName().compareTo(t2.getName());
            }
         });

         for (Table table : group.getTables()) {
            Element entity = m_tableMeta.getTableMeta(m_conn.getMetaData(), table.getName());

            entities.addContent(entity);
         }

         resolveAliasConfliction(entities);

         Document doc = new Document(entities);

         saveDocument(doc, file);
      }

      private void generateManifestFile(Group group) throws IOException {
         String name = group.getName();
         File file = new File(outputDir, name + "-manifest.xml");

         if (!file.exists()) {
            Document doc = m_tableMeta.getManifest(name + "-codegen.xml", name + "-dal.xml");

            saveDocument(doc, file);
         }
      }

      private void generateModelFile(Group group) throws IOException {
         File file = new File(outputDir, group.getName() + "-dal.xml");

         if (!file.exists()) {
            Document doc = m_tableMeta.getModel(group.getPackage());

            saveDocument(doc, file);
         }
      }

      @SuppressWarnings("unchecked")
      private void resolveAliasConfliction(Element entities) {
         Map<String, Integer> map = new HashMap<String, Integer>();
         List<Element> children = entities.getChildren("entity");

         for (Element entity : children) {
            String alias = entity.getAttributeValue("alias");
            Integer count = map.get(alias);

            if (count == null) {
               map.put(alias, 1);
            } else {
               count++;
               map.put(alias, count);
               entity.setAttribute("alias", alias + (count));
            }
         }
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
