package org.unidal.maven.plugin;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.maven.plugin.property.PropertyProviderTest;
import org.unidal.maven.plugin.wizard.meta.ModelMetaTest;
import org.unidal.maven.plugin.wizard.meta.TableMetaTest;
import org.unidal.maven.plugin.wizard.pom.JdbcPomBuilderTest;
import org.unidal.maven.plugin.wizard.pom.ModelPomBuilderTest;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilderTest;

@RunWith(Suite.class)
@SuiteClasses({

      PropertyProviderTest.class,

      ModelMetaTest.class,

      TableMetaTest.class,

      JdbcPomBuilderTest.class,

      ModelPomBuilderTest.class,

      WebAppPomBuilderTest.class,

})
public class AllTests {

}
