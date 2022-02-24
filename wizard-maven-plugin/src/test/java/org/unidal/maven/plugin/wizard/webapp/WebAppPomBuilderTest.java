package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

public class WebAppPomBuilderTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      File pomFile = new File(getClass().getResource("pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");
      Wizard wizard = new Wizard().setPackage("org.unidal.test");
      WebAppPomBuilder builder = lookup(WebAppPomBuilder.class);

      builder.enableLogging(new ConsoleLogger());
      Files.forDir().copyFile(pomFile, tmpFile);

      wizard.setWebapp(new Webapp().setPackage("org.unidal.test"));
      
      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
