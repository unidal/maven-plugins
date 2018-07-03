package org.unidal.codegen.generator.model.all;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class AllGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-all");
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
   
   @Test
   public void generateSqlMap() throws Exception {
      generate("sqlMap-manifest.xml");
   }
}
