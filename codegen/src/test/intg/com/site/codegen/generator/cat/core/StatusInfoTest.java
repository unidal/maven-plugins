package com.site.codegen.generator.cat.core;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.cat.status.model.entity.StatusInfo;
import com.dianping.cat.status.model.transform.DefaultSaxParser;
import com.dianping.cat.status.model.transform.DefaultXmlBuilder;
import com.dianping.cat.status.model.transform.DefaultDomParser;
import com.site.helper.Files;

public class StatusInfoTest {
	@Test
	public void testDomParser() throws Exception {
		String source = Files.forIO().readFrom(getClass().getResourceAsStream("status.xml"), "utf-8");
		StatusInfo root = new DefaultDomParser().parse(source);
		String xml = new DefaultXmlBuilder().buildXml(root);
		String expected = source;

		Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
	}
	@Test
	public void testSaxParser() throws Exception {
		String source = Files.forIO().readFrom(getClass().getResourceAsStream("status.xml"), "utf-8");
		StatusInfo root = DefaultSaxParser.parse(source);
		String xml = new DefaultXmlBuilder().buildXml(root);
		String expected = source;
		
		Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
	}
}
