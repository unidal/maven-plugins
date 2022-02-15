package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@RunWith(JUnit4.class)
public class ModelMojoTest extends ComponentTestCase {
   @Test
   public void testPom() throws Exception {
      ModelMojo mojo = new ModelMojo();
      File pomFile = new File(getClass().getResource("model/pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("model/pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile, getWizard());
      mojo.modifyPomFile(tmpFile, getWizard());

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   private Wizard getWizard() {
      Wizard wizard = new Wizard();
      
      wizard.addModel(new Model("first"));
      wizard.addModel(new Model("second"));
      wizard.addModel(new Model("third"));
      return wizard;
   }
}
