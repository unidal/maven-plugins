package org.unidal.codegen.generator.wizard.pixie;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.wizard.WizardGenerateTestSupport;

public class PixieWizardGeneratorTest extends WizardGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-wizard-pixie");
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
   public void testWebapp() throws Exception {
      generateWebapp("manifest.xml");
   }
}
