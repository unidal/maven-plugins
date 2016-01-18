package org.unidal.maven.plugin.property;

import java.util.ArrayList;
import java.util.List;

public class PropertyProviders {
   public static StringPropertyAccessor forString() {
      return new StringPropertyAccessor();
   }

   public static abstract class PropertyAccessor<T extends PropertyAccessor<T>> {
      private List<PropertyProvider> m_providers = new ArrayList<PropertyProvider>();

      @SuppressWarnings("unchecked")
      public T fromConsole(String... mappings) {
         m_providers.add(new ConsoleProvider(mappings));
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T fromEnv(String... mappings) {
         m_providers.add(new EnvironmentVariableProvider(mappings));
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T fromProperties(String propertiesFile, String... mappings) {
         m_providers.add(new PropertiesFileProvider(propertiesFile, mappings));
         return (T) this;
      }

      @SuppressWarnings("unchecked")
      public T fromSystem(String... mappings) {
         m_providers.add(new SystemPropertyProvider(mappings));
         return (T) this;
      }

      protected String getStringProperty(String property, String defaultValue) {
         String value = null;

         for (PropertyProvider provider : m_providers) {
            value = provider.getProperty(property, defaultValue);

            if (value != null) {
               break;
            }
         }

         return value;
      }
   }

   public static class StringPropertyAccessor extends PropertyAccessor<StringPropertyAccessor> {
      public String getProperty(String property, String defaultValue) {
         String value = getStringProperty(property, defaultValue);

         return value;
      }
   }
}
