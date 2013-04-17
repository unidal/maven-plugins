package org.unidal.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.aggregator.XmlAggregatorTest;
import org.unidal.codegen.manifest.ManifestCreatorTest;
import org.unidal.codegen.manifest.ManifestParserTest;
import org.unidal.codegen.meta.AllMetaTests;
import org.unidal.codegen.template.XslTemplateManagerTest;
import org.unidal.codegen.transformer.XslDecorateTest;
import org.unidal.codegen.transformer.XslNormalizeTest;

@RunWith(Suite.class)
@SuiteClasses({

XmlAggregatorTest.class,

ManifestCreatorTest.class,

ManifestParserTest.class,

AllMetaTests.class,

XslTemplateManagerTest.class,

XslDecorateTest.class,

XslNormalizeTest.class

})
public class AllTests {

}
