package org.unidal.codegen.manifest;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class ManifestCreatorTest extends ComponentTestCase {
   @Test
   public void testCreate() throws Exception {
      ManifestCreator creator = lookup(ManifestCreator.class);
      
      Assert.assertNotNull(creator.create("123", "456"));
   }
}
