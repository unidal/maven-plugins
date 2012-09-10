package org.unidal.codegen.generator.model.egret;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

@RunWith(JUnit4.class)
public class EgretGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-egret");
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
   public void testGeneratePom() throws Exception {
      generate("projects-manifest.xml");
   }
}
