package org.unidal.codegen.transformer;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;

import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class XslDecorateTest extends ComponentTestCase {
	private static final String JDBC_NORMALIZE_XSL = "/META-INF/dal/jdbc/decorate.xsl";

	private void assertEquals2(String message, String expected, String actual) {
		String e = expected.replace("\r\n", "\n");
		String a = actual.replace("\r\n", "\n");

		a = a.substring("<?xml version=\"1.0\" encoding=\"utf-8\"?>".length());
		
		Assert.assertEquals(message, e, a);
	}

	private void checkDecorate(String caseName) throws Exception {
		XslTransformer transformer = lookup(XslTransformer.class);
		InputStream in = getClass().getResourceAsStream(caseName + "_in.xml");
		InputStream out = getClass().getResourceAsStream(caseName + "_out.xml");

		if (in == null) {
			throw new FileNotFoundException("Resource not found: " + caseName + "_in.xml");
		} else if (out == null) {
			throw new FileNotFoundException("Resource not found: " + caseName + "_out.xml");
		}

		String source = Files.forIO().readFrom(in, "utf-8");
		String expected = Files.forIO().readFrom(out, "utf-8");
		String jdbcResult = transformer.transform(getClass().getResource(JDBC_NORMALIZE_XSL), source);

		assertEquals2("[" + caseName + "] [JDBC] Content does not match.", expected, jdbcResult);
	}

	@Test
	public void testJavaKeywords() throws Exception {
		checkDecorate("keywords");
	}
}
