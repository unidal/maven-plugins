package org.unidal.codegen.generator.model.lotus;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class LotusGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-lotus");
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
   public void testModel() throws Exception {
      generate("lotus-manifest.xml");
   }
}
