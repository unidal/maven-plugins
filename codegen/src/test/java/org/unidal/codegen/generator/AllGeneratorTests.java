package org.unidal.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.jdbc.AllJdbcGeneratorTests;
import org.unidal.codegen.generator.model.AllModelGeneratorTests;
import org.unidal.codegen.generator.wizard.AllWizardGeneratorTests;

@RunWith(Suite.class)
@SuiteClasses({

AllJdbcGeneratorTests.class,

AllModelGeneratorTests.class,

AllWizardGeneratorTests.class,

})
public class AllGeneratorTests {

}
