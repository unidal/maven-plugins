package org.unidal.maven.plugin.wizard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.maven.plugin.wizard.dom.DocumentBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({

DocumentBuilderTest.class,

ProjectTest.class,

WizardModelTest.class,

WebAppMojoTest.class,

JdbcMojoTest.class,

ModelMojoTest.class,

CatConfigMojoTest.class

})
public class AllTests {

}
