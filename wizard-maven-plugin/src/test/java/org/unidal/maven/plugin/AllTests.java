package org.unidal.maven.plugin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.wizard.AllWizardGeneratorTests;
import org.unidal.maven.plugin.wizard.JdbcMojoTest;
import org.unidal.maven.plugin.wizard.ModelMojoTest;
import org.unidal.maven.plugin.wizard.ProjectTest;
import org.unidal.maven.plugin.wizard.WebAppMojoTest;
import org.unidal.maven.plugin.wizard.WizardModelTest;
import org.unidal.maven.plugin.wizard.dom.DocumentBuilderTest;
import org.unidal.maven.plugin.wizard.webapp.WebAppPomBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({

DocumentBuilderTest.class,

ProjectTest.class,

WizardModelTest.class,

WebAppPomBuilderTest.class,

WebAppMojoTest.class,

JdbcMojoTest.class,

ModelMojoTest.class,

AllWizardGeneratorTests.class,


})
public class AllTests {

}
