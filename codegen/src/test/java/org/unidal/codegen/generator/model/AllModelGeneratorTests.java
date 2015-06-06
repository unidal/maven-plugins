package org.unidal.codegen.generator.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.model.all.AllGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatConsumerGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatCoreGeneratorTest;
import org.unidal.codegen.generator.model.cat.CatHomeGeneratorTest;
import org.unidal.codegen.generator.model.dal.DalGeneratorTest;
import org.unidal.codegen.generator.model.dobby.DobbyGeneratorTest;
import org.unidal.codegen.generator.model.ebay.EbayGeneratorTest;
import org.unidal.codegen.generator.model.egret.EgretGeneratorTest;
import org.unidal.codegen.generator.model.expense.ExpenseGeneratorTest;
import org.unidal.codegen.generator.model.key.KeysGeneratorTest;
import org.unidal.codegen.generator.model.liger.LigerGeneratorTest;
import org.unidal.codegen.generator.model.mvc.MvcGeneratorTest;
import org.unidal.codegen.generator.model.phoenix.PhoenixGeneratorTest;
import org.unidal.codegen.generator.model.pixie.PixieGeneratorTest;
import org.unidal.codegen.generator.model.pom.PomGeneratorTest;
import org.unidal.codegen.generator.model.rule.RuleGeneratorTest;
import org.unidal.codegen.generator.model.service.ServiceModelGeneratorTest;
import org.unidal.codegen.generator.model.test.EunitMetaGeneratorTest;
import org.unidal.codegen.generator.model.test.EunitTestGeneratorTest;
import org.unidal.codegen.generator.model.webres.WebresGeneratorTest;
import org.unidal.codegen.generator.model.wizard.WizardModelGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

AllGeneratorTest.class,

CatConsumerGeneratorTest.class,

CatCoreGeneratorTest.class,

CatHomeGeneratorTest.class,

DalGeneratorTest.class,

DobbyGeneratorTest.class,

EbayGeneratorTest.class,

EgretGeneratorTest.class,

ExpenseGeneratorTest.class,

LigerGeneratorTest.class,

MvcGeneratorTest.class,

PhoenixGeneratorTest.class,

PixieGeneratorTest.class,

PomGeneratorTest.class,

RuleGeneratorTest.class,

EunitMetaGeneratorTest.class,

EunitTestGeneratorTest.class,

WebresGeneratorTest.class,

ServiceModelGeneratorTest.class,

WizardModelGeneratorTest.class,

KeysGeneratorTest.class

})
public class AllModelGeneratorTests {

}
