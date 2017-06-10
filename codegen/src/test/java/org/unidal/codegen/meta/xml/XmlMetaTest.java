package org.unidal.codegen.meta.xml;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.meta.DefaultXmlMeta;
import org.unidal.codegen.meta.XmlMeta;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class XmlMetaTest extends ComponentTestCase {
   @Test
   public void testSanguo() throws Exception {
      XmlMeta xmlMeta = lookup(XmlMeta.class);
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      StringWriter writer = new StringWriter(8192);
      Element root = xmlMeta.getMeta(new InputStreamReader(getClass().getResourceAsStream("sanguo.xml"), "utf-8"));

      outputter.output(root, writer);

      String result = writer.toString();
      String expected = Files.forIO().readUtf8String(getClass().getResourceAsStream("sanguo_meta.xml"));

      Assert.assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
   }

   @Test
   public void testSinaNews() throws Exception {
      XmlMeta xmlMeta = lookup(XmlMeta.class);
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      StringWriter writer = new StringWriter(8192);
      Element root = xmlMeta.getMeta(new InputStreamReader(getClass().getResourceAsStream("sina_news.xml"), "utf-8"));

      outputter.output(root, writer);

      String result = writer.toString();
      String expected = Files.forIO().readUtf8String(getClass().getResourceAsStream("sina_news_meta.xml"));

      Assert.assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
   }

   @Test
   public void testGuessType() {
      Assert.assertEquals("boolean", DefaultXmlMeta.Utils.guessValueTypeAndFormat("true")[0]);
      Assert.assertEquals("boolean", DefaultXmlMeta.Utils.guessValueTypeAndFormat("false")[0]);
      Assert.assertEquals("int", DefaultXmlMeta.Utils.guessValueTypeAndFormat("123")[0]);
      Assert.assertEquals("double", DefaultXmlMeta.Utils.guessValueTypeAndFormat("123.45")[0]);
      Assert.assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("Tue, 27 Jan 2009 12:40:01 GMT")[0]);
      Assert.assertEquals("EEE, d MMM yyyy HH:mm:ss z",
            DefaultXmlMeta.Utils.guessValueTypeAndFormat("Tue, 27 Jan 2009 12:40:01 GMT")[1]);
      Assert.assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31 16:54:01")[0]);
      Assert.assertEquals("yyyy-MM-dd HH:mm:ss", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31 16:54:01")[1]);
      Assert.assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31T16:54:01")[0]);
      Assert.assertEquals("yyyy-MM-dd'T'HH:mm:ss", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31T16:54:01")[1]);
   }
}
