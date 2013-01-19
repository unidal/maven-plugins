package org.unidal.codegen.generator.jdbc.garden;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.jdbc.JdbcGenerateTestSupport;

public class GardenJdbcGeneratorTest extends JdbcGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-jdbc-garden");
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
   public void testGenerateDeploy() throws Exception {
      generate("garden-manifest.xml");
   }
}
