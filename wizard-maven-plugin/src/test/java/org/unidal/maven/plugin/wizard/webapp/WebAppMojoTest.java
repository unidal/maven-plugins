package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class WebAppMojoTest extends ComponentTestCase {
   @Test
   public void testPom() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      File pomFile = new File(getClass().getResource("pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");
      Wizard wizard = new Wizard().setPackage("com.dianping.test");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile, wizard, new Webapp().setPackage("com.dianping.test"));
      mojo.modifyPomFile(tmpFile, wizard, new Webapp().setPackage("com.dianping.test"));
      // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   @Test
   public void testSingle() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File(".").getCanonicalFile();

      File pomFile = new File(getClass().getResource("pom-before.xml").getFile());
      File tmpFile = new File("target/pom-single.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      project.setFile(tmpFile);
      mojo.m_project = project;
      mojo.m_generator = lookup(Generator.class, "wizard-webapp");
      mojo.m_wizardMeta = lookup(WizardMeta.class);
      mojo.m_aggregator = lookup(XmlAggregator.class, "dal-jdbc");
      mojo.m_resourceFolder = "src/test/resources";
      mojo.baseDir = baseDir;
      mojo.manifest = "src/test/resources/META-INF/wizard/webapp/manifest.xml";
      mojo.resourceBase = "/META-INF/wizard/webapp";
      mojo.sourceDir = baseDir + "/target/generated-sources/wizard-webapp";
      mojo.debug = false;
      mojo.verbose = false;

      System.setProperty("module", "ui");
      System.setProperty("page", "home");
      System.setProperty("template", "single");
      System.setProperty("table", "dailyreport");
      mojo.execute();
   }
}
