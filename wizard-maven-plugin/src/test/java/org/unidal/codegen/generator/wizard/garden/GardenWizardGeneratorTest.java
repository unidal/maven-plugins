package org.unidal.codegen.generator.wizard.garden;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.wizard.WizardGenerateTestSupport;

public class GardenWizardGeneratorTest extends WizardGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-wizard-garden");
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
   public void testJdbc() throws Exception {
      generateJdbc("manifest.xml");
   }

   @Test
   public void testWebapp() throws Exception {
      generateWebapp("manifest.xml");
   }
}
