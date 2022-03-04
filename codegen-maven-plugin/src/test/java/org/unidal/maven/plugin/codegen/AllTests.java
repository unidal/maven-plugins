package org.unidal.maven.plugin.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

      DalModelMojoTest.class,

      DalJdbcMojoTest.class,

})
public class AllTests {

}
