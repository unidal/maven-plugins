package org.unidal.maven.plugin.uml;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.common.Injector;

public class GenerateMojoTest extends ComponentTestCase {
   @Test
   public void testGenerate() throws Exception {
      GenerateMojo mojo = new GenerateMojo();
      URL url = getClass().getResource("deploy.uml");
      String source = new File(url.getPath()).getParent();
      File target = new File("target/uml");

      Files.forDir().delete(target, true);

      Injector.setField(mojo, "source", source);
      Injector.setField(mojo, "output", "target/uml");
      Injector.setField(mojo, "imageType", "png,svg");

      mojo.execute();

      List<File> files = Scanners.forDir().scan(target, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.endsWith(".png") || path.endsWith(".svg")) {
               return Direction.MATCHED;
            } else {
               return Direction.DOWN;
            }
         }
      });

      Assert.assertEquals(6, files.size());
   }
}
