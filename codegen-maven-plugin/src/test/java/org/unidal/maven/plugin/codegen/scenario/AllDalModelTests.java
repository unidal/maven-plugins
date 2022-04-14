package org.unidal.maven.plugin.codegen.scenario;

import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

@RunWith(DalModelRunner.class)
public class AllDalModelTests {
   @Rule
   public ExternalResource resource = new ExternalResource() {
      @Override
      protected void before() throws Throwable {
         System.out.println("before");
      };

      @Override
      protected void after() {
         System.out.println("after");
      };
   };
}
