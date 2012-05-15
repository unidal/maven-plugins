package com.site.codegen.meta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class XmlMetaHelperTest extends ComponentTestCase {
	@Test
	public void testResource() throws Exception {
		XmlMetaHelper helper = lookup(XmlMetaHelper.class);
		Reader reader = new InputStreamReader(new FileInputStream(getResource("sanguo.xml")), "utf-8");
		String content = helper.getXmlMetaContent(reader);

		assertTrue(content.contains("game"));
	}

	@Test
	@Ignore
	public void testSinaRss() throws Exception {
		XmlMetaHelper helper = lookup(XmlMetaHelper.class);
		URL url = new URL("http://rss.sina.com.cn/news/marquee/ddt.xml");

		try {
			String content = helper.getXmlMetaContent(new InputStreamReader(url.openStream(), "utf-8"));

			assertTrue(content.contains("rss"));
		} catch (FileNotFoundException e) {
			// ignore it
		}
	}
}
