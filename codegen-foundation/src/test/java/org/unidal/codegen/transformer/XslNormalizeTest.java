package org.unidal.codegen.transformer;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class XslNormalizeTest extends ComponentTestCase {
	private static final String JDBC_NORMALIZE_XSL = "/META-INF/dal/jdbc/normalize.xsl";

	private void assertEquals2(String message, String expected, String actual) {
		String e = expected.replace("\r", "");
		String a = actual.replace("\r", "");

      a = a.substring("<?xml version=\"1.0\" encoding=\"utf-8\"?>".length());

		Assert.assertEquals(message, e, a);
	}

	private void checkJdbcNormalize(String caseName) throws Exception {
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
		String result = transformer.transform(getClass().getResource(JDBC_NORMALIZE_XSL), source);

		assertEquals2("[" + caseName + "] Content does not match.", expected, result);
	}

	@Test
	public void testJdbcBasic() throws Exception {
		checkJdbcNormalize("basic");
	}

	@Test
	public void testJdbcEntity() throws Exception {
		checkJdbcNormalize("entity");
	}

	@Test
	public void testJdbcFlag() throws Exception {
		checkJdbcNormalize("flag");
	}

	@Test
	public void testJdbcMember() throws Exception {
		checkJdbcNormalize("member");
	}

	@Test
	public void testJdbcRelation() throws Exception {
		checkJdbcNormalize("relation");
	}

	@Test
	public void testJdbcValueType() throws Exception {
		checkJdbcNormalize("valuetype");
	}
}
