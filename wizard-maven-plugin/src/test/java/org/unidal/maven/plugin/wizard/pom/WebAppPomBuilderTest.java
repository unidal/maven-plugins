package org.unidal.maven.plugin.wizard.pom;

import java.io.File;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class WebAppPomBuilderTest extends ComponentTestCase {
   private Wizard getWizard() {
      Wizard wizard = new Wizard().setPackage("org.unidal.model");
      Webapp webapp = new Webapp().setPackage("org.unidal.webapp").setModule(false);

      wizard.setWebapp(webapp);
      webapp.addModule(new Module("first"));
      webapp.addModule(new Module("second"));
      webapp.addModule(new Module("third"));

      return wizard;
   }

   @Test
   public void test() throws Exception {
      WebAppPomBuilder builder = lookup(WebAppPomBuilder.class);
      File pomFile = new File(getClass().getResource("webapp-pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("webapp-pom-after.xml").getFile());
      File tmpFile = new File("target/pom.xml");

      Files.forDir().copyFile(pomFile, tmpFile);
      builder.enableLogging(new ConsoleLogger());

      Wizard wizard = getWizard();

      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);
      builder.build(tmpFile, wizard);

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
