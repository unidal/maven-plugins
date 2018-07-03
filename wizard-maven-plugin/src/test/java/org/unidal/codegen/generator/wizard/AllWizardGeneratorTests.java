package org.unidal.codegen.generator.wizard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.wizard.garden.GardenWizardGeneratorTest;
import org.unidal.codegen.generator.wizard.phoenix.PhoenixWizardGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

GardenWizardGeneratorTest.class,

PhoenixWizardGeneratorTest.class

})
public class AllWizardGeneratorTests {

}
