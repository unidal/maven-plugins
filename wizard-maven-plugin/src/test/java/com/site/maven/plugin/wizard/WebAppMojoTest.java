package com.site.maven.plugin.wizard;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.site.helper.Files;
import com.site.maven.plugin.wizard.model.entity.Wizard;
import com.site.maven.plugin.wizard.model.transform.DefaultXmlParser;

public class WebAppMojoTest {
   @Test
   public void testModel() throws Exception {
      DefaultXmlParser parser = new DefaultXmlParser();
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("wizard.xml"), "utf-8");
      Wizard wizard = parser.parse(expected);

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), wizard.toString().replace("\r", ""));
   }

   @Test
   @Ignore
   public void test() throws IOException, SAXException {
      WebAppMojo mojo = new WebAppMojo();
      File wizardFile = new File("target/generate-resources/wizard.xml");
      Wizard wizard = mojo.buildWizard(wizardFile);

      Files.forIO().writeTo(wizardFile.getCanonicalFile(), wizard.toString());
      System.out.println(wizard);
   }
}
