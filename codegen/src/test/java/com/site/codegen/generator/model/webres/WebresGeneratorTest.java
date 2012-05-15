package com.site.codegen.generator.model.webres;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.generator.model.ModelGenerateTestSupport;

@RunWith(JUnit4.class)
public class WebresGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-webres");
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
   public void testGenerateResourceModel() throws Exception {
      generate("resource_model_manifest.xml");
   }

   @Test
   public void testGenerateResourceProfile() throws Exception {
      generate("resource_profile_manifest.xml");
   }

   @Test
   public void testGenerateResourceTestFwkModel() throws Exception {
      generate("resource_testfwk_manifest.xml");
   }

   @Test
   public void testGenerateResourceVariation() throws Exception {
      generate("resource_variation_manifest.xml");
   }
}
