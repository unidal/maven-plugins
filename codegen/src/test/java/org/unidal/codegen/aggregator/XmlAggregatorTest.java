package org.unidal.codegen.aggregator;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.util.StringUtils;

public class XmlAggregatorTest extends ComponentTestCase {
	@Test
	public void testAggregateJdbc() throws Exception {
		XmlAggregator aggregator = lookup(XmlAggregator.class, "dal-jdbc");

		Assert.assertNotNull(aggregator);

		File manifest = new File(getClass().getResource("jdbc_manifest.xml").getFile());
		String result = aggregator.aggregate(manifest);
		String expected = Files.forIO().readUtf8String(getClass().getResourceAsStream("jdbc_aggregated.xml"));

		Assert.assertEquals(StringUtils.normalizeSpace(expected), StringUtils.normalizeSpace(result));
	}

	@Test
	public void testAggregateXml() throws Exception {
		XmlAggregator aggregator = lookup(XmlAggregator.class, "dal-xml");

		Assert.assertNotNull(aggregator);

		File manifest = new File(getClass().getResource("xml_manifest.xml").getFile());
		String result = aggregator.aggregate(manifest);
		String expected = Files.forIO().readUtf8String(getClass().getResourceAsStream("xml_aggregated.xml"));

		Assert.assertEquals(expected, result);
	}
}
