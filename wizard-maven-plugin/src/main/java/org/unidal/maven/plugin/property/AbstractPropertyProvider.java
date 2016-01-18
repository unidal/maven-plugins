package org.unidal.maven.plugin.property;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPropertyProvider implements PropertyProvider {
   private Map<String, String> m_property2Prompts = new HashMap<String, String>();

   public AbstractPropertyProvider(String[] mappings) {
      m_property2Prompts = toMap(mappings);
   }

   protected String map(String property) {
      return m_property2Prompts.get(property);
   }

   private Map<String, String> toMap(String[] mappings) {
      Map<String, String> map = new HashMap<String, String>(mappings.length * 2);

      for (String mapping : mappings) {
         int pos = mapping.indexOf(':');

         if (pos > 0) {
            String key = mapping.substring(0, pos);
            String value = mapping.substring(pos + 1);

            map.put(key, value);
         } else {
            throw new IllegalArgumentException(String.format("Invalid mappings: %s.", Arrays.asList(mappings)));
         }
      }

      return map;
   }
}
