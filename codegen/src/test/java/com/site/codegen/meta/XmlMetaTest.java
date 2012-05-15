package com.site.codegen.meta;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.codehaus.plexus.util.IOUtil;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class XmlMetaTest extends ComponentTestCase {
	@Test
	public void testSanguo() throws Exception {
		XmlMeta xmlMeta = lookup(XmlMeta.class);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer = new StringWriter(8192);
		Element root = xmlMeta.getMeta(new InputStreamReader(getClass().getResourceAsStream("sanguo.xml"), "utf-8"));

		outputter.output(root, writer);

		String result = writer.toString();
		String expected = IOUtil.toString(getClass().getResourceAsStream("sanguo_meta.xml"));

		assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
	}

	@Test
	public void testSinaNews() throws Exception {
		XmlMeta xmlMeta = lookup(XmlMeta.class);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer = new StringWriter(8192);
		Element root = xmlMeta.getMeta(new InputStreamReader(getClass().getResourceAsStream("sina_news.xml"), "utf-8"));

		outputter.output(root, writer);

		String result = writer.toString();
		String expected = IOUtil.toString(getClass().getResourceAsStream("sina_news_meta.xml"));

		assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
	}

	@Test
	public void testGuessType() {
		assertEquals("boolean", DefaultXmlMeta.Utils.guessValueTypeAndFormat("true")[0]);
		assertEquals("boolean", DefaultXmlMeta.Utils.guessValueTypeAndFormat("false")[0]);
		assertEquals("int", DefaultXmlMeta.Utils.guessValueTypeAndFormat("123")[0]);
		assertEquals("double", DefaultXmlMeta.Utils.guessValueTypeAndFormat("123.45")[0]);
		assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("Tue, 27 Jan 2009 12:40:01 GMT")[0]);
		assertEquals("EEE, d MMM yyyy HH:mm:ss z",
		      DefaultXmlMeta.Utils.guessValueTypeAndFormat("Tue, 27 Jan 2009 12:40:01 GMT")[1]);
		assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31 16:54:01")[0]);
		assertEquals("yyyy-MM-dd HH:mm:ss", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31 16:54:01")[1]);
		assertEquals("Date", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31T16:54:01")[0]);
		assertEquals("yyyy-MM-dd'T'HH:mm:ss", DefaultXmlMeta.Utils.guessValueTypeAndFormat("2009-01-31T16:54:01")[1]);
	}
}
