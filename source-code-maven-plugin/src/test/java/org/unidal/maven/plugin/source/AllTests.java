package org.unidal.maven.plugin.source;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.unidal.maven.plugin.source.pipeline.SourcePipelineTest;

@RunWith(Suite.class)
@SuiteClasses({

      SourcePipelineTest.class,

})
public class AllTests {

}
