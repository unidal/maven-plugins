package org.unidal.codegen.framework;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.code.ObfuscaterTest;

@RunWith(Suite.class)
@SuiteClasses({

      FileStorageTest.class,

      XmlAggregatorTest.class,

      XslGeneratorTest.class,

      XslTransformerTest.class,

      ObfuscaterTest.class,

})
public class AllTests {

}
