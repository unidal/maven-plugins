package org.unidal.codegen.generator.jdbc.user;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.jdbc.JdbcGenerateTestSupport;

public class UserJdbcGeneratorTest extends JdbcGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-jdbc-user");
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
      generate("user-manifest.xml");
   }
}
