package org.unidal.codegen.generator.xmodel;

import java.io.File;

import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.Generator;
import org.unidal.lookup.ComponentTestCase;

public abstract class XmodelGenerateTestSupport extends ComponentTestCase {
   protected void generate(GenerateContext ctx) throws Exception {
      Generator g = lookup(Generator.class, "dal-xmodel");
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected void generate(String manifestXml) throws Exception {
      Generator g = lookup(Generator.class, "dal-xmodel");
      File manifest = new File(getClass().getResource(manifestXml).getFile());
      GenerateContext ctx = new XmodelGenerateContext(getProjectBaseDir(), manifest, isVerbose(), isDebug());
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
