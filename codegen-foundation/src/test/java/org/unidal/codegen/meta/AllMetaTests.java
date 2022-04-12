package org.unidal.codegen.meta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.codegen.meta.model.CsprojMetaTest;
import org.unidal.codegen.meta.model.GameModelMetaTest;
import org.unidal.codegen.meta.model.MavenModelMetaTest;
import org.unidal.codegen.meta.model.StarterMetadataMetaTest;
import org.unidal.codegen.meta.table.TableMetaTest;

@RunWith(Suite.class)
@SuiteClasses({

ModelMetaTest.class,

TableMetaTest.class,

GameModelMetaTest.class,

MavenModelMetaTest.class,

StarterMetadataMetaTest.class,

CsprojMetaTest.class,

})
public class AllMetaTests {

}
