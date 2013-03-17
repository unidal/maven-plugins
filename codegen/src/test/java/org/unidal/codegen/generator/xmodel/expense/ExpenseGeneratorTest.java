package org.unidal.codegen.generator.xmodel.expense;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.generator.xmodel.XmodelGenerateTestSupport;

public class ExpenseGeneratorTest extends XmodelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-xmodel-expense");
   }

   @Override
   protected boolean isDebug() {
      return false;
   }

   @Override
   protected boolean isVerbose() {
      return true;
   }

   @Test
   public void testGenerate() throws Exception {
      generate("book-manifest.xml");
   }
}
