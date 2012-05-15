package com.site.maven.plugin.codegen;

import java.io.File;
import java.net.ConnectException;
import java.sql.SQLException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.generator.Generator;
import com.site.codegen.manifest.ManifestCreator;
import com.site.codegen.meta.DefaultModelMeta;
import com.site.codegen.meta.DefaultTableMeta;
import com.site.codegen.meta.DefaultXmlMeta;
import com.site.codegen.meta.XmlMetaHelper;
import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class MojoTest extends ComponentTestCase {
   @Test
   public void testGenerateDalJdbc() throws Exception {
      DalJdbcMojo mojo = new DalJdbcMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File(".");

      project.setFile(new File(baseDir, ""));
      mojo.m_project = project;
      mojo.m_generator = lookup(Generator.class, "dal-jdbc");
      mojo.manifest = getResource("jdbc_manifest.xml");
      mojo.resouceBase = "/META-INF/dal/jdbc";
      mojo.sourceDir = baseDir + "/target/generated-sources/dal-jdbc";
      mojo.verbose = false;
      mojo.execute();

      assertTrue("Compile sources is not added", project.getCompileSourceRoots().contains(mojo.sourceDir));
      assertTrue("Files are not generated", new File(mojo.sourceDir).exists());
   }

   @Test
   public void testGenerateDalModel() throws Exception {
      DalModelMojo mojo = new DalModelMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File(".");

      project.setFile(new File(baseDir, ""));
      mojo.m_project = project;
      mojo.m_generator = lookup(Generator.class, "dal-model");
      mojo.manifest = getResource("model_manifest.xml");
      mojo.resouceBase = "/META-INF/dal/model";
      mojo.sourceDir = baseDir + "/target/generated-sources/dal-model";
      mojo.verbose = false;
      mojo.execute();

      assertTrue("Compile sources is not added", project.getCompileSourceRoots().contains(mojo.sourceDir));
      assertTrue("Files are not generated", new File(mojo.sourceDir).exists());
   }

   @Test
   public void testGenerateDalModelMeta() throws Exception {
      DalModelMetaMojo mojo = new DalModelMetaMojo();

      mojo.m_meta = new DefaultModelMeta();
      mojo.baseDir = new File(".");
      mojo.inputFile = getClass().getResource("sanguo.xml").getPath();
      mojo.outputDir = "target/generated-resources";
      mojo.packageName = "com.site.sango.xml";
      mojo.prefix = "sango";
      mojo.execute();

      assertTrue("File is not generated", mojo.baseDir.exists());
   }

   @Test
   public void testGenerateDalJdbcMeta() throws Exception {
      DalJdbcMetaMojo mojo = new DalJdbcMetaMojo();

      mojo.driver = "com.mysql.jdbc.Driver";
      mojo.url = "jdbc:mysql://localhost:3306/test";
      mojo.user = "root";
      mojo.packageName = "a.b";
      mojo.password = "";
      mojo.connectionProperties = "useUnicode=true&characterEncoding=gbk&autoReconnect=true";

      mojo.m_meta = new DefaultTableMeta();
      mojo.baseDir = new File(".");
      mojo.outputDir = "target/generated-resources/dal-jdbc";

      try {
         mojo.execute();

         assertTrue("File is not generated", mojo.baseDir.exists());
      } catch (MojoExecutionException e) {
         Throwable cause = e.getCause();

         if (cause instanceof SQLException) {
            SQLException sqle = (SQLException) cause;

            if (sqle.getCause() instanceof ConnectException || "08001".equals(sqle.getSQLState())) {
               System.out.println("Warning: " + sqle.getCause().getMessage());
               return;
            }
         }

         throw e;
      }
   }

   @Test
   public void testGenerateDalXml() throws Exception {
      DalXmlMojo mojo = new DalXmlMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File(".");

      // following for debug purpose
      String debugBaseDir = "D:/dev/workshop/mvc-framework";
      boolean debug = false;

      project.setFile(new File(baseDir, ""));
      mojo.m_project = project;
      mojo.m_generator = lookup(Generator.class, "dal-xml");

      if (!debug) {
         mojo.manifest = getResource("xml_manifest.xml");
      } else {
         mojo.manifest = debugBaseDir + "/src/main/resources/META-INF/dal/xml/manifest.xml";
      }

      mojo.resouceBase = "/META-INF/dal/xml";

      if (!debug) {
         mojo.sourceDir = baseDir + "/target/generated-sources/dal-xml";
      } else {
         mojo.sourceDir = debugBaseDir + "/target/generated-sources/dal-xml";
      }

      mojo.verbose = false;
      mojo.execute();

      assertTrue("Compile sources is not added", project.getCompileSourceRoots().contains(mojo.sourceDir));
      assertTrue("Files are not generated", new File(mojo.sourceDir).exists());
   }

   @Test
   @Ignore
   public void testGenerateDalXml2() throws Exception {
      DalXmlMojo mojo = new DalXmlMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File(".");

      project.setFile(baseDir);
      mojo.m_project = project;
      mojo.m_generator = lookup(Generator.class, "dal-xml");
      mojo.m_xmlMetaHelper = lookup(XmlMetaHelper.class);
      mojo.m_manifestCreator = lookup(ManifestCreator.class);
      mojo.sourceInput = "http://rss.sina.com.cn/news/marquee/ddt.xml";
      mojo.sourcePackage = "com.site.sina.rss";
      mojo.resouceBase = "/META-INF/dal/xml";
      mojo.sourceDir = baseDir + "/target/generated-sources/dal-xml";
      mojo.verbose = false;
      mojo.execute();

      assertTrue("Compile sources is not added", project.getCompileSourceRoots().contains(mojo.sourceDir));
      assertTrue("Files are not generated", new File(mojo.sourceDir).exists());
   }

   @Test
   public void testGenerateDalXmlMeta() throws Exception {
      DalXmlMetaMojo mojo = new DalXmlMetaMojo();

      mojo.m_meta = new DefaultXmlMeta();
      mojo.baseDir = new File(".");
      mojo.inputFile = getClass().getResource("sanguo.xml").getPath();
      mojo.outputFile = "target/generated-resources/sanguo_xml_meta.xml";
      mojo.execute();

      assertTrue("File is not generated", mojo.baseDir.exists());
   }
}
