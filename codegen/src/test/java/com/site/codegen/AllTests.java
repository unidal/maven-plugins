package com.site.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.site.codegen.aggregator.XmlAggregatorTest;
import com.site.codegen.generator.AllGeneratorTests;
import com.site.codegen.manifest.ManifestCreatorTest;
import com.site.codegen.manifest.ManifestParserTest;
import com.site.codegen.meta.ModelMetaTest;
import com.site.codegen.meta.TableMetaTest;
import com.site.codegen.meta.XmlMetaHelperTest;
import com.site.codegen.meta.XmlMetaTest;
import com.site.codegen.template.XslTemplateManagerTest;
import com.site.codegen.transformer.XslDecorateTest;
import com.site.codegen.transformer.XslNormalizeTest;

@RunWith(Suite.class)
@SuiteClasses({

XmlAggregatorTest.class,

AllGeneratorTests.class,

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
