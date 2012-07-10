package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.DefaultDomParser;

import com.site.helper.Files;

public class WebAppMojoTest {
   @Test
   public void testModel() throws Exception {
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("wizard.xml"), "utf-8");
      Wizard wizard = new DefaultDomParser().parse(expected);

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), wizard.toString().replace("\r", ""));
   }

   @Test
   public void testPom() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      File pomFile = new File(getClass().getResource("pom.xml").getFile());

      mojo.addDependenciesToPom(pomFile);
   }
}
