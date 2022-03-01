package org.unidal.maven.plugin.wizard.pom;

import java.io.File;

import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class JdbcPomBuilderTest extends ComponentTestCase {
   private Wizard getWizard() {
      Wizard wizard = new Wizard().setPackage("org.unidal.jdbc");
      Jdbc jdbc = new Jdbc().setName("jdbc");

      jdbc.addGroup(new Group("first"));
      jdbc.addGroup(new Group("second"));
      jdbc.addGroup(new Group("third"));
      wizard.addJdbc(jdbc);

      return wizard;
   }

   @Test
   public void test() throws Exception {
      JdbcPomBuilder builder = lookup(JdbcPomBuilder.class);
      File pomFile = new File(getClass().getResource("jdbc-pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("jdbc-pom-after.xml").getFile());
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
