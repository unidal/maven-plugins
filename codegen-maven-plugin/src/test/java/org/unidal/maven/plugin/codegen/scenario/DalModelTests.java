package org.unidal.maven.plugin.codegen.scenario;

import org.junit.Test;
import org.junit.runner.RunWith;

//@RunWith(DalModelRunner.class)
public class DalModelTests {
   @Test
   public void cat2TenantReport() throws Exception {
      new DalModelMojoSupport().run("ctrip-kpi-fx");
   }
}
