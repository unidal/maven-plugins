package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class WebAppMojoTest {
   @Test
   public void testPom() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      File pomFile = new File(getClass().getResource("webapp/pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("webapp/pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");
      Wizard wizard = new Wizard().setPackage("com.dianping.test");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile, wizard, new Webapp().setPackage("com.dianping.test"));
      mojo.modifyPomFile(tmpFile, wizard, new Webapp().setPackage("com.dianping.test")); // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
