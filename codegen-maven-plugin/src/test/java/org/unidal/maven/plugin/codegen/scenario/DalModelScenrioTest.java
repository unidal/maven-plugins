package org.unidal.maven.plugin.codegen.scenario;

import org.junit.Test;

public class DalModelScenrioTest extends DalModelMojoSupport {
   @Test
   public void all() throws Exception {
      checkAll();
   }

   @Test
   public void cat2TenantReport() throws Exception {
      checkOne("cat2-tenant-report");
   }
}
