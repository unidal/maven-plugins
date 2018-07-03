package org.unidal.codegen.generator.model.ebay;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class EbayGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-ebay");
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
   public void generateAddItem() throws Exception {
      generate("AddItem-manifest.xml");
   }

   @Test
   public void generateAddItemRequest() throws Exception {
      generate("AddItemRequest-manifest.xml");
   }

   @Test
   public void generateAddItemResponse() throws Exception {
      generate("AddItemResponse-manifest.xml");
   }
}
