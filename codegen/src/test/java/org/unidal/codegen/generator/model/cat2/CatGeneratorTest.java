package org.unidal.codegen.generator.model.cat2;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class CatGeneratorTest extends ModelGenerateTestSupport {
	@Override
	protected File getProjectBaseDir() {
		return new File("target/generated-model-cat2");
	}

	@Override
	protected boolean isDebug() {
		return true;
	}

	@Override
	protected boolean isVerbose() {
		return false;
	}

	@Test
	public void testGenerateClientConfig() throws Exception {
		generate("client-config-manifest.xml");
	}
}
