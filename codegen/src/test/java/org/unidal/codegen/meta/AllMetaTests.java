package org.unidal.codegen.meta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.meta.model.GameModelMetaTest;
import org.unidal.codegen.meta.model.MavenModelMetaTest;
import org.unidal.codegen.meta.table.TableMetaTest;
import org.unidal.codegen.meta.xml.XmlMetaHelperTest;
import org.unidal.codegen.meta.xml.XmlMetaTest;

@RunWith(Suite.class)
@SuiteClasses({

ModelMetaTest.class,

TableMetaTest.class,

XmlMetaHelperTest.class,

XmlMetaTest.class,

GameModelMetaTest.class,

MavenModelMetaTest.class

})
public class AllMetaTests {

}
