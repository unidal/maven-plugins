package com.site.codegen.generator.cat.core;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.cat.configuration.client.IEntity;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.transform.DefaultDomParser;
import com.dianping.cat.configuration.client.transform.DefaultJsonBuilder;
import com.dianping.cat.configuration.client.transform.DefaultJsonParser;
import com.dianping.cat.configuration.client.transform.DefaultSaxParser;
import com.dianping.cat.configuration.client.transform.DefaultXmlBuilder;
import com.site.helper.Files;

public class ConfigTest {
   @Test
   public void testDom() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("client-config.xml"), "utf-8");
      ClientConfig config = new DefaultDomParser().parse(source);
      String xml = new DefaultXmlBuilder().buildXml(config);
      String expected = source;

      Assert.assertEquals("XML is not well parsed or built!", expected.replace("\r", ""), xml.replace("\r", ""));
   }

   @Test
   public void testJson() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("global-config.xml"), "utf-8");
      ClientConfig config = new DefaultDomParser().parse(source);
      String json = new DefaultJsonBuilder().buildJson(config);
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("global-config.json"), "utf-8");

      Assert.assertEquals("XML is not well parsed or JSON is not well built!", expected.replace("\r", ""),
            json.replace("\r", ""));

      ClientConfig config2 = DefaultJsonParser.parse(json);

      Assert.assertEquals(json, new DefaultJsonBuilder().buildJson(config2));
   }

   @Test
   public void testSax() throws Exception {
      String source = Files.forIO().readFrom(getClass().getResourceAsStream("client-config.xml"), "utf-8");
      ClientConfig config = DefaultSaxParser.parse(source);
      String xml = new DefaultXmlBuilder().buildXml(config);
      String expected = source;

      Assert.assertEquals("XML is not well parsed or built!", expected.replace("\r", ""), xml.replace("\r", ""));
   }

   @Test
   public void testXmlSchema() throws Exception {
      // define the type of schema - we use W3C:
      String schemaLang = "http://www.w3.org/2001/XMLSchema";

      // get validation driver:
      SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

      // create schema by reading it from an XSD file:
      String path = "/" + IEntity.class.getPackage().getName().replace('.', '/');
      Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream(path + "/config.xsd")));
      Validator validator = schema.newValidator();

      // at last perform validation:
      validator.validate(new StreamSource(getClass().getResourceAsStream("client-config.xml")));
   }

}
