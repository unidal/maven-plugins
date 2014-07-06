package org.unidal.codegen.code;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.helper.Codes;
import org.unidal.lookup.ComponentTestCase;

public class ObfuscaterTest extends ComponentTestCase {
   @Test
   public void testEncode() throws Exception {
      Obfuscater o = lookup(Obfuscater.class);
      String str = o.encode("Hello, world!");

      Assert.assertEquals("Hello, world!", Codes.forDecode().decode(str));
   }
}
