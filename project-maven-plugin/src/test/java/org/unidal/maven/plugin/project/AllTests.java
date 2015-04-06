package org.unidal.maven.plugin.project;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.maven.plugin.project.rule.RuleEngineTest;

@RunWith(Suite.class)
@SuiteClasses({

MigrateMojoTest.class,

RuleEngineTest.class

})
public class AllTests {

}
