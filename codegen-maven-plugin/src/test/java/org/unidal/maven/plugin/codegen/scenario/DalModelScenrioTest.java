package org.unidal.maven.plugin.codegen.scenario;

import java.util.List;

import org.junit.Test;

public class DalModelScenrioTest extends DalModelMojoSupport {
   @Test
   public void all() throws Exception {
      List<MyMessage> messages = super.checkAll();

      for (MyMessage message : messages) {
         System.out.println(message);
      }
   }

   @Test
   public void cat2TenantReport() throws Exception {
      List<MyMessage> messages = checkOne("cat2-tenant-report");

      for (MyMessage message : messages) {
         System.out.println(message);
      }
   }
}
