package org.unidal.codegen.generator.model.key;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class KeysGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-keys");
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
   public void generateModel() throws Exception {
      generate("manifest.xml");
   }
}
