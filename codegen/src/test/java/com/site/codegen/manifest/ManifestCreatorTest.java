package com.site.codegen.manifest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class ManifestCreatorTest extends ComponentTestCase {
   @Test
   public void testCreate() throws Exception {
      ManifestCreator creator = lookup(ManifestCreator.class);
      
      assertNotNull(creator.create("123", "456"));
   }
}
