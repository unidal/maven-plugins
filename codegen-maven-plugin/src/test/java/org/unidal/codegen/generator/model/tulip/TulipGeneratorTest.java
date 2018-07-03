package org.unidal.codegen.generator.model.tulip;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class TulipGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-tulip");
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
      generate("model-manifest.xml");
   }
   
   @Test
   public void testScenarios() throws Exception {
	   generate("scenarios-manifest.xml");
   }
}
