package org.unidal.codegen.generator.model.array;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class ArrayGeneratorTest extends ModelGenerateTestSupport {
	@Override
	protected File getProjectBaseDir() {
		return new File("target/generated-model-array");
	}

	@Override
	protected boolean isDebug() {
		return false;
	}

	@Override
	protected boolean isVerbose() {
		return false;
	}
//
//	@Test
//	public void test() throws SAXException, IOException {
//		InputStream in = getClass().getResourceAsStream("sample.xml");
//		Model m1 = DefaultSaxParser.parse(in);
//		System.out.println("m1 len: " + m1.toString().length());
//		System.out.println(m1);
//
//		byte[] data = DefaultNativeBuilder.build(m1);
//		System.out.println("native len: " + data.length);
//
//		Model m2 = DefaultNativeParser.parse(data);
//		System.out.println("m2 len: " + m2.toString().length());
//		System.out.println(m2);
//		
//		String json = new DefaultJsonBuilder().build(m2);
//		System.out.println("json len: " + json.length());
//		System.out.println(json);
//	}

	@Test
	public void generateModel() throws Exception {
		generate("manifest.xml");
	}
}
