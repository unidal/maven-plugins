package org.unidal.codegen.generator.model.query;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class QueryGeneratorTest extends ModelGenerateTestSupport {
	@Override
	protected File getProjectBaseDir() {
		return new File("target/generated-query");
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
	public void testGenerateScenario() throws Exception {
		generate("scenario-manifest.xml");
	}

	@Test
	public void testGenerateGraphQL() throws Exception {
		generate("graphql-manifest.xml");
	}
}
