package com.site.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.site.codegen.generator.jdbc.JdbcGeneratorTest;
import com.site.codegen.generator.model.all.AllGeneratorTest;
import com.site.codegen.generator.model.cat.CatConsumerGeneratorTest;
import com.site.codegen.generator.model.cat.CatCoreGeneratorTest;
import com.site.codegen.generator.model.pom.PomGeneratorTest;
import com.site.codegen.generator.model.test.TestGeneratorTest;
import com.site.codegen.generator.model.webres.WebresGeneratorTest;
import com.site.codegen.generator.model.wizard.WizardModelGeneratorTest;
import com.site.codegen.generator.wizard.WizardGeneratorTest;
import com.site.codegen.generator.xml.XmlGeneratorTest;

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
