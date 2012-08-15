package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import com.site.helper.Files;

public class CatConfigMojoTest {
   @Test
   public void testPom() throws Exception {
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
}
