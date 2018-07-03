package org.unidal.codegen.generator.jdbc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.generator.jdbc.garden.GardenJdbcGeneratorTest;
import org.unidal.codegen.generator.jdbc.phoenix.PhoenixJdbcGeneratorTest;
import org.unidal.codegen.generator.jdbc.user.UserJdbcGeneratorTest;

@RunWith(Suite.class)
@SuiteClasses({

UserJdbcGeneratorTest.class,

GardenJdbcGeneratorTest.class,

PhoenixJdbcGeneratorTest.class

})
public class AllJdbcGeneratorTests {

}
