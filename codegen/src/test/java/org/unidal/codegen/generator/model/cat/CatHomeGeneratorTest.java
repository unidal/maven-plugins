package org.unidal.codegen.generator.model.cat;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class CatHomeGeneratorTest extends ModelGenerateTestSupport {
	@Override
	protected File getProjectBaseDir() {
		return new File("target/generated-model-cat");
	}

	@Override
	protected boolean isDebug() {
		return false;
	}

	@Override
	protected boolean isVerbose() {
		return false;
	}

	@Test
	public void testGenerateThresholdTemplate() throws Exception {
		generate("home/threshold-template-manifest.xml");
	}
	
	@Test
	public void testBug() throws Exception {
	   generate("home/bug-manifest.xml");
	}
}
