package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.maven.plugin.wizard.CatConfigMojo;

public class CatConfigMojoTest {
   @Test
   public void testLog4jConfig() throws Exception {
      CatConfigMojo mojo = new CatConfigMojo();
      File log4jConfig = new File(getClass().getResource("catconfig/log4j-before.xml").getFile());
      File expectedWebFile = new File(getClass().getResource("catconfig/log4j-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedWebFile, "utf-8");
      File tmpFile = new File("target/log4j.xml");
      
      Files.forDir().copyFile(log4jConfig, tmpFile);
      
      mojo.modifyLog4jFile(tmpFile);
      mojo.modifyLog4jFile(tmpFile); // next time, change nothing
      
      String actual = Files.forIO().readFrom(tmpFile, "utf-8");
      
      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   @Test
   public void testPomFile() throws Exception {
      CatConfigMojo mojo = new CatConfigMojo();
      File pomFile = new File(getClass().getResource("catconfig/pom-before.xml").getFile());
      File expectedPomFile = new File(getClass().getResource("catconfig/pom-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedPomFile, "utf-8");
      File tmpFile = new File("target/pom.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      mojo.modifyPomFile(tmpFile);
      mojo.modifyPomFile(tmpFile); // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
   
   @Test
   public void testWebFileForWeb23() throws Exception {
      CatConfigMojo mojo = new CatConfigMojo();
      File webFile = new File(getClass().getResource("catconfig/web-2.3-before.xml").getFile());
      File expectedWebFile = new File(getClass().getResource("catconfig/web-2.3-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedWebFile, "utf-8");
      File tmpFile = new File("target/web-2.3.xml");

      Files.forDir().copyFile(webFile, tmpFile);

      mojo.modifyWebFile(tmpFile, true);
      mojo.modifyWebFile(tmpFile, true); // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }

   @Test
   public void testWebFileForWeb25() throws Exception {
      CatConfigMojo mojo = new CatConfigMojo();
      File webFile = new File(getClass().getResource("catconfig/web-2.5-before.xml").getFile());
      File expectedWebFile = new File(getClass().getResource("catconfig/web-2.5-after.xml").getFile());
      String expected = Files.forIO().readFrom(expectedWebFile, "utf-8");
      File tmpFile = new File("target/web-2.5.xml");

      Files.forDir().copyFile(webFile, tmpFile);

      mojo.modifyWebFile(tmpFile, true);
      mojo.modifyWebFile(tmpFile, true); // next time, change nothing

      String actual = Files.forIO().readFrom(tmpFile, "utf-8");

      Assert.assertEquals(expected.replaceAll("\r", ""), actual.replaceAll("\r", ""));
   }
}
