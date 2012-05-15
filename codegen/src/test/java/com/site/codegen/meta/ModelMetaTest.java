package com.site.codegen.meta;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.codehaus.plexus.util.IOUtil;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class ModelMetaTest extends ComponentTestCase {
	@Test
	public void test() throws Exception {
		ModelMeta xmlMeta = lookup(ModelMeta.class);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		StringWriter writer = new StringWriter(8192);
		Document root = xmlMeta.getCodegen(new InputStreamReader(getClass().getResourceAsStream("model.xml"), "utf-8"));

		outputter.output(root, writer);

		String result = writer.toString();
		String expected = IOUtil.toString(getClass().getResourceAsStream("model_meta.xml"));

		assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
	}
}
