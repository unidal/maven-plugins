package org.unidal.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.unidal.codegen.aggregator.XmlAggregatorTest;
import org.unidal.codegen.manifest.ManifestCreatorTest;
import org.unidal.codegen.manifest.ManifestParserTest;
import org.unidal.codegen.meta.ModelMetaTest;
import org.unidal.codegen.meta.TableMetaTest;
import org.unidal.codegen.meta.XmlMetaHelperTest;
import org.unidal.codegen.meta.XmlMetaTest;
import org.unidal.codegen.template.XslTemplateManagerTest;
import org.unidal.codegen.transformer.XslDecorateTest;
import org.unidal.codegen.transformer.XslNormalizeTest;

@RunWith(Suite.class)
@SuiteClasses({

XmlAggregatorTest.class,

ManifestCreatorTest.class,

ManifestParserTest.class,

ModelMetaTest.class,

TableMetaTest.class,

XmlMetaHelperTest.class,

XmlMetaTest.class,

XslTemplateManagerTest.class,

XslDecorateTest.class,

XslNormalizeTest.class

})
public class AllTests {

}
