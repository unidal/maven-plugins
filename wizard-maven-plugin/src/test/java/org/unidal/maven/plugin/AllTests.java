package org.unidal.maven.plugin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.wizard.AllWizardGeneratorTests;
import org.unidal.maven.plugin.wizard.JdbcMojoTest;
import org.unidal.maven.plugin.wizard.ModelMojoTest;
import org.unidal.maven.plugin.wizard.ProjectMojoTest;
import org.unidal.maven.plugin.wizard.WebAppMojoTest;
import org.unidal.maven.plugin.wizard.WizardModelTest;
import org.unidal.maven.plugin.wizard.dom.DocumentBuilderTest;
import org.unidal.maven.plugin.wizard.meta.CsprojMetaTest;
import org.unidal.maven.plugin.wizard.meta.GameModelMetaTest;
import org.unidal.maven.plugin.wizard.meta.MavenModelMetaTest;
import org.unidal.maven.plugin.wizard.meta.ModelMetaTest;
import org.unidal.maven.plugin.wizard.meta.StarterMetadataMetaTest;
import org.unidal.maven.plugin.wizard.meta.TableMetaTest;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({

DocumentBuilderTest.class,

ModelMetaTest.class,

TableMetaTest.class,

GameModelMetaTest.class,

MavenModelMetaTest.class,

StarterMetadataMetaTest.class,

CsprojMetaTest.class,

ProjectMojoTest.class,

WizardModelTest.class,

WebAppPomBuilderTest.class,

WebAppMojoTest.class,

JdbcMojoTest.class,

ModelMojoTest.class,

AllWizardGeneratorTests.class,


})
public class AllTests {

}
