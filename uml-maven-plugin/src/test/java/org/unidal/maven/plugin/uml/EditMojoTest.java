package org.unidal.maven.plugin.uml;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class EditMojoTest extends ComponentTestCase {
   @Test
   public void testEdit() throws Exception {
      EditMojo mojo = new EditMojo();

      mojo.execute();
   }
}
