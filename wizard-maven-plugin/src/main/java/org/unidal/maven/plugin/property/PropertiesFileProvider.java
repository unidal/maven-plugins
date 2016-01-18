package org.unidal.maven.plugin.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Get property from Java Properties file.
 */
public class PropertiesFileProvider extends AbstractPropertyProvider {
   private Properties m_properties = new Properties();

   public PropertiesFileProvider(String propertiesFile, String... mappings) {
      super(mappings);

      // try to find it from resource file
      InputStream in = getClass().getResourceAsStream(propertiesFile);

      try {
         if (in == null) {
            // try to find it from file system
            File file = new File(propertiesFile);

            if (file.canRead()) {
               in = new FileInputStream(file);
            }
         }

         if (in == null) {
            throw new IOException(String.format("Can't find properties file(%s).", propertiesFile));
         } else {
            m_properties.load(in);
         }
      } catch (IOException e) {
         throw new RuntimeException(String.format("Error when loading properties file(%s).", propertiesFile), e);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }
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
            value = m_properties.getProperty(name);
         }
      } else {
         value = m_properties.getProperty(property);
      }

      if (value == null) {
         return defaultValue;
      } else {
         return value;
      }
   }
}