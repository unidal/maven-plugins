package org.unidal.codegen.generator.model.kpi;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class KpiGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-kpi");
   }

   @Override
   protected boolean isDebug() {
      return false;
   }

   @Override
   protected boolean isVerbose() {
      return false;
   }

   @Test
   public void testGenerate() throws Exception {
      generate("model-manifest.xml");
   }
}
