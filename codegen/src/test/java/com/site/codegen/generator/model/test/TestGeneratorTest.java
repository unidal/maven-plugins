package com.site.codegen.generator.model.test;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.generator.model.ModelGenerateTestSupport;

@RunWith(JUnit4.class)
public class TestGeneratorTest extends ModelGenerateTestSupport {
   @Override
   protected File getProjectBaseDir() {
      return new File("target/generated-model-test");
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
   public void testGenerateBenchmarkTestFwkModel() throws Exception {
      generate("eunit_benchmark_testfwk_manifest.xml");
   }

   @Test
   public void testGenerateCmdTestFwkModel() throws Exception {
      generate("eunit_cmd_testfwk_manifest.xml");
   }

   @Test
   public void testGenerateCompatibilityTestModel() throws Exception {
      generate("ctest_report_model_manifest.xml");
   }

   @Test
   public void testGenerateEunitResourceModel() throws Exception {
      generate("eunit_resource_manifest.xml");
   }

   @Test
   public void testGenerateEunitTestFwkModel() throws Exception {
      generate("eunit_testfwk_manifest.xml");
   }

   @Test
   public void testGenerateReportExcelModel() throws Exception {
      generate("eunit_excel_manifest.xml");
   }
}
