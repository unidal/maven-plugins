package org.unidal.codegen.generator.wizard.phoenix;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.wizard.WizardGenerateTestSupport;

public class PhoenixWizardGeneratorTest extends WizardGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-wizard-phoenix");
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
   public void testConsole() throws Exception {
      generateWebapp("console-manifest.xml");
   }
   
   @Test
   public void testEnvironment() throws Exception {
      generateWebapp("environment-manifest.xml");
   }
   
   @Test
   public void testService() throws Exception {
      generateWebapp("service-manifest.xml");
   }
}
