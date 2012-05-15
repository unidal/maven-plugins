package com.site.codegen.generator.pom;

import org.junit.Assert;
import org.junit.Test;

import com.site.codegen.pom.model.entity.Project;
import com.site.codegen.pom.model.transform.DefaultJsonBuilder;
import com.site.codegen.pom.model.transform.DefaultXmlBuilder;
import com.site.codegen.pom.model.transform.DefaultDomParser;
import com.site.helper.Files;

public class PomTest {
   @Test
   public void testXml() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("pom.xml"), "utf-8");
      Project root = new DefaultDomParser().parse(source);
      String xml = new DefaultXmlBuilder().buildXml(root);
      String expected = source;

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
   }
   
   @Test
   public void testJson() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("pom.xml"), "utf-8");
      Project root = new DefaultDomParser().parse(source);
      String xml = new DefaultJsonBuilder().buildJson(root);
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("pom.json"), "utf-8");
      
      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
   }
}
