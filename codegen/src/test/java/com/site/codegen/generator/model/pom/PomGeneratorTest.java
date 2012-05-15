package com.site.codegen.generator.model.pom;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.generator.model.ModelGenerateTestSupport;

@RunWith(JUnit4.class)
public class PomGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-pom");
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
      generate("manifest.xml");
   }
}
