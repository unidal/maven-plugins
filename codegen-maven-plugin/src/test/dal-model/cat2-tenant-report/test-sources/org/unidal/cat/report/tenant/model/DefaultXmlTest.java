package org.unidal.cat.report.tenant.model;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.cat.report.tenant.model.entity.TenantReport;
import org.unidal.helper.Files;
import org.xml.sax.SAXException;

public class DefaultXmlTest {
   @Test
   public void test() throws SAXException, IOException {
      InputStream in = getClass().getResourceAsStream("/tenant-report.xml");
      String xml = Files.forIO().readUtf8String(in);
      TenantReport report = TenantReportHelper.fromXml(xml);
      String xml2 = TenantReportHelper.asXml(report);

      Assert.assertEquals(xml.replaceAll("\r\n", "\n"), xml2.replaceAll("\r\n", "\n"));
   }

   @Test
   public void test2() throws SAXException, IOException {
      InputStream in = getClass().getResourceAsStream("/tenant-report.xml");
      TenantReport report = TenantReportHelper.fromXml(in);

      String xml = TenantReportHelper.asXml(report);

      System.out.println(xml);
   }

   @Test(expected = RuntimeException.class)
   public void test3() throws Exception {
      throw new RuntimeException("Aha");
   }

   @Test(expected = RuntimeException.class)
   public void test4() throws Exception {
      throw new RuntimeException("Aha");
   }

   @Test(expected = AssertionError.class)
   public void test5() throws Exception {
      throw new AssertionError("Aha");
   }
}
