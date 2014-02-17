package org.unidal.codegen.generator.model.liger;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class LigerGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-liger");
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
   public void testDatasource() throws Exception {
      generate("datasource-manifest.xml");
   }
}
