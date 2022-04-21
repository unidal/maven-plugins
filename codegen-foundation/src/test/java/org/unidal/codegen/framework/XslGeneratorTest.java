package org.unidal.codegen.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.model.entity.OutputModel;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.annotation.Named;

public class XslGeneratorTest extends ComponentTestCase {
   private static StringBuilder SB = new StringBuilder(1024);

   @Test
   public void test() throws Exception {
      define(MockFileStorage.class);

      XslGenerator generator = lookup(XslGenerator.class);
      URL url = getClass().getResource("meta-manifest.xml");
      File baseDir = new File(url.getPath()).getParentFile();

      GenerationContextSupport ctx = new GenerationContextSupport(baseDir) {
         @Override
         public void debug(String message) {
         }

         @Override
         public AtomicInteger getGeneratedFiles() {
            return new AtomicInteger();
         }

         @Override
         public File getManifestXml() {
            return new File(getClass().getResource("meta-manifest.xml").getPath());
         }

         @Override
         protected URL getResource(String name) {
            return getClass().getResource("xsl/" + name);
         }

         @Override
         public URL getStructureXml() {
            return getClass().getResource("structure.xml");
         }

         @Override
         public void info(String message) {
         }

         @Override
         public void verbose(String message) {
         }
      };

      generator.generate(ctx);

      String expected = Files.forIO().readFrom(getClass().getResourceAsStream("abstract.txt"), "utf-8");

      Assert.assertEquals(expected, SB.toString());
   }

   @Named(type = FileStorage.class)
   private static class MockFileStorage implements FileStorage {
      @Override
      public void copyResources(GenerationContext ctx, OutputModel output) throws IOException {
         SB.append("resource: ").append(output.getTemplate()).append(" => ").append(output.getPath()).append("\r\n");
      }

      @Override
      public void writeFile(GenerationContext ctx, OutputModel output, String content) throws IOException {
         SB.append("code: ").append(output.getPath()).append(" => ").append(content).append("\r\n");
      }
   }
}
