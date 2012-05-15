package com.site.codegen.generator.model;

import java.io.File;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.generator.GenerateContext;
import com.site.codegen.generator.Generator;
import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public abstract class ModelGenerateTestSupport extends ComponentTestCase {
   protected void generate(GenerateContext ctx) throws Exception {
      Generator g = lookup(Generator.class, "dal-model");
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected void generate(String manifestXml) throws Exception {
      Generator g = lookup(Generator.class, "dal-model");
      File manifest = getResourceFile(manifestXml);
      GenerateContext ctx = new ModelGenerateContext(getProjectBaseDir(), manifest, isVerbose(), isDebug());
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected abstract File getProjectBaseDir();

   protected abstract boolean isDebug();

   protected abstract boolean isVerbose();
}
