package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@RunWith(JUnit4.class)
public class JdbcMojoTest extends ComponentTestCase {
   @Test
   public void testPom() throws Exception {
      JdbcMojo mojo = new JdbcMojo();
      File pomFile = new File(getClass().getResource("jdbc/pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("jdbc/pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile, getWizard(), getJdbc());
      mojo.modifyPomFile(tmpFile, getWizard(), getJdbc());

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   private Jdbc getJdbc() {
      Jdbc jdbc = new Jdbc().setName("jdbc");

      jdbc.addGroup(new Group("first"));
      jdbc.addGroup(new Group("second"));
      jdbc.addGroup(new Group("third"));
      return jdbc;
   }

   private Wizard getWizard() {
      return new Wizard().setPackage("org.unidal.test");
   }
}
