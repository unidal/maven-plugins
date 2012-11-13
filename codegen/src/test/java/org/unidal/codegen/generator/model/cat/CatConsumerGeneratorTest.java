package org.unidal.codegen.generator.model.cat;

import java.io.File;
import java.util.Map;

import org.junit.Test;
import org.unidal.codegen.generator.model.ModelGenerateContext;
import org.unidal.codegen.generator.model.ModelGenerateTestSupport;

public class CatConsumerGeneratorTest extends ModelGenerateTestSupport {
   void generateWithXsd(String resource) throws Exception {
      File file = new File(getClass().getResource(resource).getFile());
      generate(new ModelGenerateContext(getProjectBaseDir(), file, isVerbose(), isDebug()) {
         @Override
         protected void configure(Map<String, String> properties) {
            // generate XSD file with java code
            // for integration purpose in AllIntgTests
            properties.put("src-main-resources", "src/main/java");
         }
      });
   }

   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-cat");
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
   public void testGenerateEventReport() throws Exception {
      generateWithXsd("consumer/event-report-manifest.xml");
   }

   @Test
   public void testGenerateHeartbeatReport() throws Exception {
      generateWithXsd("consumer/heartbeat-report-manifest.xml");
   }

   @Test
   public void testGenerateIpReport() throws Exception {
      generateWithXsd("consumer/ip-report-manifest.xml");
   }

   @Test
   public void testGenerateProblemReport() throws Exception {
      generateWithXsd("consumer/problem-report-manifest.xml");
   }

   @Test
   public void testGenerateProblem2Report() throws Exception {
      generateWithXsd("consumer/problem2-report-manifest.xml");
   }

   @Test
   public void testGenerateTransactionReport() throws Exception {
      generateWithXsd("consumer/transaction-report-manifest.xml");
   }

}
