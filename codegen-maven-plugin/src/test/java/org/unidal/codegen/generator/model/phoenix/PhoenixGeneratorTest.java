package org.unidal.codegen.generator.model.phoenix;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class PhoenixGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-phoenix");
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
   public void testRoutingConfigure() throws Exception {
      generate("configure-manifest.xml");
   }

   @Test
   public void testConfig() throws Exception {
      generate("config-manifest.xml");
   }

   @Test
   public void testDeploy() throws Exception {
      generate("deploy-manifest.xml");
   }

   @Test
   public void testProject() throws Exception {
      generate("project-manifest.xml");
   }

   @Test
   public void testResponse() throws Exception {
      generate("response-manifest.xml");
   }
}
