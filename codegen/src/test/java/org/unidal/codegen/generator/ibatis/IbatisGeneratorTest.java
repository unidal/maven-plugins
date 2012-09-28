package org.unidal.codegen.generator.ibatis;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
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
      return true;
   }

   @Test
   public void testGenerateIbatis() throws Exception {
      generate("garden-manifest.xml");
   }
}
