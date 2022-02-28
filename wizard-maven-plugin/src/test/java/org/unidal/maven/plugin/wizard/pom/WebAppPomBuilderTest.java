package org.unidal.maven.plugin.wizard.pom;

import java.io.File;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class WebAppPomBuilderTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      File pomFile = new File(getClass().getResource("webapp-pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("webapp-pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      Wizard wizard = new Wizard().setPackage("org.unidal.test");
      WebAppPomBuilder builder = lookup(WebAppPomBuilder.class);
      String actual = Files.forIO().readFrom(pomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");

      wizard.setWebapp(new Webapp().setPackage("org.unidal.test"));
      builder.enableLogging(new ConsoleLogger());
      Files.forIO().writeTo(tmpFile, actual.getBytes());

      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
