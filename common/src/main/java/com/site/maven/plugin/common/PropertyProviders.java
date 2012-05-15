package com.site.maven.plugin.common;

import java.util.List;

public class PropertyProviders {
   public static ConsoleProvider fromConsole() {
      return ConsoleProvider.INSTANCE;
   }

   public static enum ConsoleProvider {
      INSTANCE;

      public boolean forBoolean(String name, String prompt, boolean defaultValue) {
         String value = getString(name, prompt, String.valueOf(defaultValue));

         if ("true".equals(value) || "false".equals(value)) {
            return Boolean.valueOf(value);
         }

         return false;
      }

      public String forString(String name, String prompt, String defaultValue, IValidator<String> validator) {
         String value = getString(name, prompt, defaultValue);

         if (validator != null) {
            while (!validator.validate(value)) {
               value = getString(name, prompt, defaultValue);
            }
         }

         return value;
      }

      public String forString(String name, String prompt, List<String> availableValues, String defaultValue,
            IValidator<String> validator) {
         String value = getString(name, prompt, availableValues, defaultValue);

         if (validator != null) {
            while (!validator.validate(value)) {
               value = getString(name, prompt, availableValues, defaultValue);
            }
         }

         return value;
      }

      private String getString(String name, String prompt, String defaultValue) {
         return getString(name, prompt, null, defaultValue);
      }

      private String getString(String name, String prompt, List<String> availableValues, String defaultValue) {
         String value = System.getProperty(name);

         if (value != null) {
            return value;
         }

         StringBuilder sb = new StringBuilder(64);
         byte[] buffer = new byte[256];

         while (value == null) {
            sb.setLength(0);
            sb.append(prompt);

            if (defaultValue != null) {
               sb.append('[').append(defaultValue).append(']');
            }

            boolean withOptions = availableValues != null && !availableValues.isEmpty();
            int index = 0;

            if (withOptions) {
               System.out.println(sb.toString());

               for (String availableValue : availableValues) {
                  System.out.println((index++) + ": " + availableValue);
               }

               System.out.print("Please select:");
            } else {
               System.out.print(sb.toString());
            }

            try {
               int size = System.in.read(buffer);

               while (size > 0 && (buffer[size - 1] == '\n' || buffer[size - 1] == '\r')) {
                  size--;
               }

               if (size <= 0) {
                  value = defaultValue;
               } else {
                  value = new String(buffer, 0, size);

                  if (withOptions && value.length() == 1) {
                     int pos = value.charAt(0) - '0';

                     if (pos >= 0 && pos < index) {
                        value = availableValues.get(pos);
                     } else if (pos <= 9) {
                        value = null;
                     }
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         return value;
      }
   }

   public static interface IValidator<T> {
      public boolean validate(T value);
   }

   public static interface IConsole {
      public String readln();

      public void writeln(String str);
   }
}
