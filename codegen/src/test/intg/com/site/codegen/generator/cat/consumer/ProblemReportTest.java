package com.site.codegen.generator.cat.consumer;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.cat.consumer.problem.model.IEntity;
import com.dianping.cat.consumer.problem.model.entity.ProblemReport;
import com.dianping.cat.consumer.problem.model.transform.DefaultJsonBuilder;
import com.dianping.cat.consumer.problem.model.transform.DefaultSaxParser;
import com.dianping.cat.consumer.problem.model.transform.DefaultXmlBuilder;
import com.dianping.cat.consumer.problem.model.transform.DefaultDomParser;
import com.site.helper.Files;

public class ProblemReportTest {
	@Test
	public void testDomParser() throws Exception {
		String source = Files.forIO().readFrom(getClass().getResourceAsStream("ProblemReport.xml"), "utf-8");
		ProblemReport report = new DefaultDomParser().parse(source);
		String xml = new DefaultXmlBuilder().buildXml(report);
		String expected = source;

		Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
	}

	@Test
	public void testJson() throws Exception {
		String source = Files.forIO().readFrom(getClass().getResourceAsStream("ProblemReport.xml"), "utf-8");
		ProblemReport report = new DefaultDomParser().parse(source);
		String json = new DefaultJsonBuilder().buildJson(report);
		String expected = Files.forIO().readFrom(getClass().getResourceAsStream("ProblemReport.json"), "utf-8");

		Assert.assertEquals("XML is not well parsed or JSON is not well built!", expected.replace("\r", ""),
		      json.replace("\r", ""));
	}

	@Test
	public void testSaxParser() throws Exception {
		String source = Files.forIO().readFrom(getClass().getResourceAsStream("ProblemReport.xml"), "utf-8");
		ProblemReport report = DefaultSaxParser.parse(source);
		String xml = new DefaultXmlBuilder().buildXml(report);
		String expected = source;

		Assert.assertEquals("XML is not well parsed!", expected.replace("\r", ""), xml.replace("\r", ""));
	}

	@Test
	public void testSchema() throws Exception {
		// define the type of schema - we use W3C:
		String schemaLang = "http://www.w3.org/2001/XMLSchema";

		// get validation driver:
		SchemaFactory factory = SchemaFactory.newInstance(schemaLang);

		// create schema by reading it from an XSD file:
		String path = "/" + IEntity.class.getPackage().getName().replace('.', '/');
		Schema schema = factory.newSchema(new StreamSource(getClass().getResourceAsStream(path + "/problem-report.xsd")));
		Validator validator = schema.newValidator();

		// at last perform validation:
		validator.validate(new StreamSource(getClass().getResourceAsStream("ProblemReport.xml")));
	}
}
