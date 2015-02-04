package org.unidal.codegen.generator.model.hermes;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class HermesGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-hermes");
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
   public void testMeta() throws Exception {
      generate("meta-manifest.xml");
   }
}
