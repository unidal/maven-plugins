package org.unidal.maven.plugin.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.AllGeneratorTests;

@RunWith(Suite.class)
@SuiteClasses({

	MojoTest.class,
	
	AllGeneratorTests.class,
	
})
public class AllTests {
	
}
