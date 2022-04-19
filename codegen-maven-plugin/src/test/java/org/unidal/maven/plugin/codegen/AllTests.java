package org.unidal.maven.plugin.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.maven.plugin.codegen.scenario.DalJdbcTests;
import org.unidal.maven.plugin.codegen.scenario.DalModelTests;

@RunWith(Suite.class)
@SuiteClasses({

      DalModelTests.class,

      DalJdbcTests.class,

})
public class AllTests {

}
