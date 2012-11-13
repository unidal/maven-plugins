package org.unidal.codegen.generator.jdbc;

import java.io.File;

import org.junit.Test;

public class JdbcGeneratorTest extends JdbcGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-java-jdbc");
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
   public void testGenerateJdbc() throws Exception {
      generate("jdbc_manifest.xml");
   }

   @Test
   public void testGenerateGarden() throws Exception {
      generate("garden-manifest.xml");
   }
}
