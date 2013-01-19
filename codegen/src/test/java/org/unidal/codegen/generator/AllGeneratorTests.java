package org.unidal.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.ibatis.IbatisGeneratorTest;
import org.unidal.codegen.generator.jdbc.garden.GardenJdbcGeneratorTest;
import org.unidal.codegen.generator.jdbc.phoenix.PhoenixJdbcGeneratorTest;
import org.unidal.codegen.generator.jdbc.user.UserJdbcGeneratorTest;
import org.unidal.codegen.generator.model.all.AllGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatConsumerGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatCoreGeneratorTest;
import org.unidal.codegen.generator.model.dobby.DobbyGeneratorTest;
import org.unidal.codegen.generator.model.egret.EgretGeneratorTest;
import org.unidal.codegen.generator.model.mvc.MvcGeneratorTest;
import org.unidal.codegen.generator.model.phoenix.PhoenixGeneratorTest;
import org.unidal.codegen.generator.model.pom.PomGeneratorTest;
import org.unidal.codegen.generator.model.rule.RuleGeneratorTest;
import org.unidal.codegen.generator.model.service.ServiceModelGeneratorTest;
import org.unidal.codegen.generator.model.test.MetaGeneratorTest;
import org.unidal.codegen.generator.model.test.TestGeneratorTest;
import org.unidal.codegen.generator.model.webres.WebresGeneratorTest;
import org.unidal.codegen.generator.model.wizard.WizardModelGeneratorTest;
import org.unidal.codegen.generator.wizard.garden.GardenWizardGeneratorTest;
import org.unidal.codegen.generator.wizard.phoenix.PhoenixWizardGeneratorTest;
import org.unidal.codegen.generator.xml.XmlGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

IbatisGeneratorTest.class,

UserJdbcGeneratorTest.class,

GardenJdbcGeneratorTest.class,

PhoenixJdbcGeneratorTest.class,

AllGeneratorTest.class,

CatConsumerGeneratorTest.class,

CatCoreGeneratorTest.class,

DobbyGeneratorTest.class,

EgretGeneratorTest.class,

MvcGeneratorTest.class,

PhoenixGeneratorTest.class,

PomGeneratorTest.class,

RuleGeneratorTest.class,

MetaGeneratorTest.class,

TestGeneratorTest.class,

WebresGeneratorTest.class,

ServiceModelGeneratorTest.class,

WizardModelGeneratorTest.class,

GardenWizardGeneratorTest.class,

PhoenixWizardGeneratorTest.class,

XmlGeneratorTest.class

})
public class AllGeneratorTests {

}
