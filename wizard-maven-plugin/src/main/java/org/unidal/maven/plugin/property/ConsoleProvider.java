package org.unidal.maven.plugin.property;

import java.io.IOException;

/**
 * Get property from Console.
 */
public class ConsoleProvider extends AbstractPropertyProvider {
   public ConsoleProvider(String... mappings) {
      super(mappings);
   }

   /**
    * Get property value.
    * 
    * @param property
    *           property name
    * @return property value, null if property is not found, or property name is empty
    */
   @Override
   public String getProperty(String property, String defaultValue) {
      String prompt = map(property);
      String value = null;

      if (prompt != null && prompt.length() > 0) {
         value = readLine(prompt, defaultValue);
      } else {
         value = readLine(property, defaultValue);
      }

      return value;
   }

   private String readLine(String prompt, String defaultValue) {
      String value = null;

      try {
         while (value == null) {
            if (defaultValue == null) {
               System.out.print(String.format("%s: ", prompt));
            } else {
               System.out.print(String.format("%s[%s]: ", prompt, defaultValue));
            }

            byte[] buffer = new byte[256];
            int size = 0;

            while (true) {
               int i = System.in.read();
               byte b = (byte) (i & 0xFF);

               if (i == -1) {
                  if (size == 0) {
                     size--;
                  }

                  break;
               } else if (b == '\r') {
                  continue;
               } else if (b == '\n') {
                  break;
               } else {
                  buffer[size++] = b;
               }
            }

            if (size < 0) {
               value = defaultValue;
               break;
            } else if (size == 0) {
               value = defaultValue;
            } else {
               value = new String(buffer, 0, size);
            }
         }
      } catch (IOException e) {
         // ignore it
      }

      return value;
   }
}