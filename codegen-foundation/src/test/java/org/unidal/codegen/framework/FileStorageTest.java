package org.unidal.codegen.framework;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class FileStorageTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      FileStorage storage = lookup(FileStorage.class);

      System.out.println(storage);
   }
}
