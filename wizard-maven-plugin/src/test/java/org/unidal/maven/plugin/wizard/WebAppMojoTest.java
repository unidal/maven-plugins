package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.DefaultDomParser;

import com.site.helper.Files;

public class WebAppMojoTest {
   @Test
   public void testModel() throws Exception {
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("wizard.xml"), "utf-8");
      Wizard wizard = new DefaultDomParser().parse(expected);

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), wizard.toString().replace("\r", ""));
   }

   @Test
   public void testPom() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      File pomFile = new File(getClass().getResource("webapp/pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("webapp/pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile, new Webapp().setPackage("com.dianping.test"));
      mojo.modifyPomFile(tmpFile, new Webapp().setPackage("com.dianping.test")); // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
