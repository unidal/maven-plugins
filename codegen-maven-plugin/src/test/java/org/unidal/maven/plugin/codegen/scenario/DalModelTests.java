package org.unidal.maven.plugin.codegen.scenario;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(DalModelRunner.class)
public class DalModelTests {
   @Test
   public void test() throws Exception {
      new DalModelMojoSupport().runStartAt("model-array");
   }
}
