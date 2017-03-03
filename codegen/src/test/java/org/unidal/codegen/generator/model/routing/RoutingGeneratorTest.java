package org.unidal.codegen.generator.model.routing;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class RoutingGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-routing");
   }

   @Override
   protected boolean isDebug() {
      return true;
   }

   @Override
   protected boolean isVerbose() {
      return false;
   }

   @Test
   public void generateModel() throws Exception {
      generate("routing-table-manifest.xml");
   }
}
