package org.unidal.maven.plugin.wizard.pom;

import java.io.File;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class ModelPomBuilderTest extends ComponentTestCase {
   private Wizard getWizard() {
      Wizard wizard = new Wizard().setPackage("org.unidal.model");

      wizard.addModel(new Model("first"));
      wizard.addModel(new Model("second"));
      wizard.addModel(new Model("third"));
      return wizard;
   }

   @Test
   public void test() throws Exception {
      ModelPomBuilder builder = lookup(ModelPomBuilder.class);
      File pomFile = new File(getClass().getResource("model-pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("model-pom-after.xml").getFile());
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
