package org.unidal.codegen.generator.model.cat;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class CatCoreGeneratorTest extends ModelGenerateTestSupport {
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
	public void testGenerateStatus() throws Exception {
		generate("core/status-manifest.xml");
	}

	@Test
	public void testGenerateClient() throws Exception {
		generate("core/client-manifest.xml");
	}

	@Test
	public void testGenerateServer() throws Exception {
		generate("core/server-manifest.xml");
	}
}
