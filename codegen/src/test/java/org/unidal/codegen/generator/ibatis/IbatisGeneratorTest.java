package org.unidal.codegen.generator.ibatis;

import java.io.File;

import org.junit.Test;

public class IbatisGeneratorTest extends IbatisGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-java-ibatis");
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
   public void testGenerateIbatis() throws Exception {
      generate("garden-manifest.xml");
   }
}
