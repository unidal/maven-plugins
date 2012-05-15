package com.site.codegen.aggregator;

import java.io.File;

import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.aggregator.XmlAggregator;
import com.site.lookup.ComponentTestCase;
import com.site.lookup.util.StringUtils;

@RunWith(JUnit4.class)
public class XmlAggregatorTest extends ComponentTestCase {
   @Test
   public void testAggregateJdbc() throws Exception {
      XmlAggregator aggregator = lookup(XmlAggregator.class, "dal-jdbc");

      assertNotNull(aggregator);

      File manifest = getResourceFile("jdbc_manifest.xml");
      String result = aggregator.aggregate(manifest);
      String expected = IOUtil.toString(getClass().getResourceAsStream("jdbc_aggregated.xml"));

      assertEquals(StringUtils.normalizeSpace(expected), StringUtils.normalizeSpace(result));
   }

   @Test
   public void testAggregateXml() throws Exception {
      XmlAggregator aggregator = lookup(XmlAggregator.class, "dal-xml");

      assertNotNull(aggregator);

      File manifest = getResourceFile("xml_manifest.xml");
      String result = aggregator.aggregate(manifest);
      String expected = IOUtil.toString(getClass().getResourceAsStream("xml_aggregated.xml"));

      assertEquals(expected, result);
   }
}
