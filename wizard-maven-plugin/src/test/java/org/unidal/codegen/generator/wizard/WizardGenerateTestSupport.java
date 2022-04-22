package org.unidal.codegen.generator.wizard;

import java.io.File;

import org.unidal.codegen.framework.GenerationContext;
import org.unidal.codegen.framework.XslGenerator;
import org.unidal.lookup.ComponentTestCase;

public abstract class WizardGenerateTestSupport extends ComponentTestCase {
   protected void generateJdbc(GenerationContext ctx) throws Exception {
      XslGenerator g = lookup(XslGenerator.class, "wizard-jdbc");
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected void generateJdbc(String manifestXml) throws Exception {
      XslGenerator g = lookup(XslGenerator.class, "wizard-jdbc");
      File manifest = new File(getClass().getResource(manifestXml).getFile());
      GenerationContext ctx = new WizardGenerationContext(getProjectBaseDir(), "jdbc", manifest, isVerbose(), isDebug());
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected void generateWebapp(GenerationContext ctx) throws Exception {
      XslGenerator g = lookup(XslGenerator.class, "wizard-webapp");
      long start = System.currentTimeMillis();

      g.generate(ctx);

      if (isVerbose() || isDebug()) {
         long now = System.currentTimeMillis();

         System.out.println(String.format("%s files generated in %s ms.", ctx.getGeneratedFiles(), now - start));
      }
   }

   protected void generateWebapp(String manifestXml) throws Exception {
      XslGenerator g = lookup(XslGenerator.class, "wizard-webapp");
      File manifest = new File(getClass().getResource(manifestXml).getFile());
      GenerationContext ctx = new WizardGenerationContext(getProjectBaseDir(), "webapp", manifest, isVerbose(), isDebug());
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
