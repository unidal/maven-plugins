package com.site.codegen.generator.all;

import org.junit.Assert;
import org.junit.Test;

import com.site.codegen.all.model.entity.RootElement;
import com.site.codegen.all.model.transform.DefaultDomParser;
import com.site.codegen.all.model.transform.DefaultJsonBuilder;
import com.site.codegen.all.model.transform.DefaultSaxParser;
import com.site.codegen.all.model.transform.DefaultXmlBuilder;
import com.site.helper.Files;

public class RootElementTest {
   @Test
   public void testDomParser() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("root-element.xml"), "utf-8");
      RootElement root = new DefaultDomParser().parse(source);
      String xml = new DefaultXmlBuilder().buildXml(root);
      String expected = source;

      Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
   }
   @Test
   public void testSaxParser() throws Exception {
   	String source = Files.forIO().readFrom(getClass().getResourceAsStream("root-element.xml"), "utf-8");
   	RootElement root = DefaultSaxParser.parse(source);
   	String xml = new DefaultXmlBuilder().buildXml(root);
   	String expected = source;
   	
   	Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
   }

   @Test
   public void testJson() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("root-element.xml"), "utf-8");
      RootElement root = new DefaultDomParser().parse(source);
      String json = new DefaultJsonBuilder().buildJson(root);
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("root-element.json"), "utf-8");

      Assert.assertEquals("XML is not well parsed or JSON is not well built!", expected.replace("\r", ""), json.replace("\r", ""));
   }

}
