package org.unidal.maven.plugin.property;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.maven.plugin.property.PropertyProviders.StringPropertyAccessor;

public class PropertyProviderTest {
   @Test
   public void testEnvironmentVariable() {
      StringPropertyAccessor provider = PropertyProviders.forString() //
            .fromEnv("key.1:PATH");

      // use key
      String p1 = provider.getProperty("key.1", null);

      Assert.assertNotNull("PATH is not set", p1);

      // use name directly
      String p2 = provider.getProperty("PATH", null);

      Assert.assertNotNull("PATH is not set", p2);
      Assert.assertEquals(p1, p2);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testInvalidMapping() {
      PropertyProviders.forString() //
            .fromSystem("key.1", "key.2:name2", "key.3:name3");
   }

   @Test
   public void testSystemProperty() {
      StringPropertyAccessor provider = PropertyProviders.forString() //
            .fromSystem("key.1:name1", "key.2:name2", "key.3:name3");

      System.setProperty("name1", "value1");
      System.setProperty("name2", "value2");

      // use key
      Assert.assertEquals("value1", provider.getProperty("key.1", null));
      Assert.assertEquals("value2", provider.getProperty("key.2", null));
      Assert.assertEquals("default value", provider.getProperty("key.3", "default value"));

      // use name directly
      Assert.assertEquals("value2", provider.getProperty("name2", null));
   }

   @Test
   public void testPropertiesFile() {
      StringPropertyAccessor provider = PropertyProviders.forString() //
            .fromProperties("example.properties", "key.1:name1", "key.2:name2", "key.3:name3");

      // use key
      Assert.assertEquals("value1", provider.getProperty("key.1", null));
      Assert.assertEquals("value2", provider.getProperty("key.2", null));
      Assert.assertEquals("default value", provider.getProperty("key.3", "default value"));

      // use name directly
      Assert.assertEquals("value2", provider.getProperty("name2", null));
   }

   @Test
   public void testConsole() {
      InputStream oldIn = System.in;
      PrintStream oldOut = System.out;

      try {
         StringPropertyAccessor provider = PropertyProviders
               .forString()
               //
               .fromConsole("key.1:Please input name", "key.2:Please input other name", "key.3:Please input third name");

         ByteArrayOutputStream out = new ByteArrayOutputStream(256);

         System.setIn(new ByteArrayInputStream("value1\r\n\r\nvalue2\r\n\r\nvalue2\r\n".getBytes()));
         System.setOut(new PrintStream(out));

         // use key
         out.reset();
         Assert.assertEquals("value1", provider.getProperty("key.1", null));
         Assert.assertEquals("Please input name: ", out.toString());

         out.reset();
         Assert.assertEquals("value2", provider.getProperty("key.2", null));
         Assert.assertEquals("Please input other name: Please input other name: ", out.toString());

         out.reset();
         Assert.assertEquals("default value", provider.getProperty("key.3", "default value"));
         Assert.assertEquals("Please input third name[default value]: ", out.toString());

         // use name directly
         out.reset();
         Assert.assertEquals("value2", provider.getProperty("name2", null));
         Assert.assertEquals("name2: ", out.toString());
      } finally {
         System.setIn(oldIn);
         System.setOut(oldOut);
      }
   }
}
