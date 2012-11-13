package org.unidal.codegen.generator.xml;

import java.io.File;

import org.junit.Test;

public class XmlGeneratorTest extends XmlGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-java-xml");
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
   public void testGenerateXml() throws Exception {
      generate("xml_manifest.xml");
   }
}
