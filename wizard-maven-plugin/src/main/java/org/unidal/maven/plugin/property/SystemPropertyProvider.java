package org.unidal.maven.plugin.property;

/**
 * Get property from Java System Properties.
 */
public class SystemPropertyProvider extends AbstractPropertyProvider {
   public SystemPropertyProvider(String... mappings) {
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
      String name = map(property);
      String value = null;

      if (name != null) {
         if (name.length() > 0) {
            value = System.getProperty(name);
         }
      } else {
         value = System.getProperty(property);
      }

      if (value == null) {
         return defaultValue;
      } else {
         return value;
      }
   }
}