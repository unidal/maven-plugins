package org.unidal.codegen.generator.jdbc.phoenix;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.jdbc.JdbcGenerateTestSupport;

public class PhoenixJdbcGeneratorTest extends JdbcGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-jdbc-phoenix");
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
      generate("deploy-manifest.xml");
   }
}
