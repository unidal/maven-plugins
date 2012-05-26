package org.unidal.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.unidal.codegen.generator.jdbc.JdbcGeneratorTest;
import org.unidal.codegen.generator.model.all.AllGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatConsumerGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatCoreGeneratorTest;
import org.unidal.codegen.generator.model.pom.PomGeneratorTest;
import org.unidal.codegen.generator.model.test.TestGeneratorTest;
import org.unidal.codegen.generator.model.webres.WebresGeneratorTest;
import org.unidal.codegen.generator.model.wizard.WizardModelGeneratorTest;
import org.unidal.codegen.generator.wizard.WizardGeneratorTest;
import org.unidal.codegen.generator.xml.XmlGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

AllGeneratorTest.class,

CatCoreGeneratorTest.class,

CatConsumerGeneratorTest.class,

PomGeneratorTest.class,

TestGeneratorTest.class,

WebresGeneratorTest.class,

WizardModelGeneratorTest.class,

WizardGeneratorTest.class,

JdbcGeneratorTest.class,

XmlGeneratorTest.class

})
public class AllGeneratorTests {

}
