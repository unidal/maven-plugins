package org.unidal.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.ibatis.IbatisGeneratorTest;
import org.unidal.codegen.generator.jdbc.AllJdbcGeneratorTests;
import org.unidal.codegen.generator.model.AllModelGeneratorTests;
import org.unidal.codegen.generator.wizard.AllWizardGeneratorTests;
import org.unidal.codegen.generator.xml.XmlGeneratorTest;
import org.unidal.codegen.generator.xmodel.expense.ExpenseGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

IbatisGeneratorTest.class,

AllJdbcGeneratorTests.class,

AllModelGeneratorTests.class,

AllWizardGeneratorTests.class,

XmlGeneratorTest.class,

ExpenseGeneratorTest.class

})
public class AllGeneratorTests {

}
