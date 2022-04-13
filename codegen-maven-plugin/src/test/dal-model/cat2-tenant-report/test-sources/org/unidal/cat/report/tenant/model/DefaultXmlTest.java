package org.unidal.cat.report.tenant.model;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.cat.report.tenant.model.entity.TenantReport;
import org.unidal.cat.report.tenant.model.transform.DefaultXmlBuilder;
import org.unidal.cat.report.tenant.model.transform.DefaultXmlParser;
import org.unidal.helper.Files;
import org.xml.sax.SAXException;

public class DefaultXmlTest {
   @Test
   public void test() throws SAXException, IOException {
      InputStream in = getClass().getResourceAsStream("/tenant-report.xml");
      String xml = Files.forIO().readUtf8String(in);

      TenantReport report = DefaultXmlParser.parse(xml);
      String actual = new DefaultXmlBuilder().buildXml(report);

      Assert.assertEquals(xml.replaceAll("\r\n", "\n"), actual.replaceAll("\r\n", "\n"));

   }
}
