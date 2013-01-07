package org.unidal.codegen.generator.wizard.cat;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.wizard.WizardGenerateTestSupport;

public class CatWizardGeneratorTest extends WizardGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-wizard-cat");
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
      generateJdbc("home-jdbc-manifest.xml");
   }

   @Test
   public void testWebapp() throws Exception {
      generateWebapp("home-webapp-manifest.xml");
   }
}
