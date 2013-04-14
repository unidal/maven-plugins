package org.unidal.maven.plugin.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is a shared context to hold data for plug-in to communicate and
 * interact, across plug-ins and projects within single build command.
 */
public class SharedContext {
   private Map<Object, Map<String, Object>> m_map = new HashMap<Object, Map<String, Object>>();

   @SuppressWarnings("unchecked")
   public <T> T getAttribute(Object category, String key) {
      Map<String, Object> map = getCategoryMap(category, false);

      return (T) map.get(key);
   }

   @SuppressWarnings("unchecked")
   public <T> Map<String, T> getAttributes(Object category) {
      Map<String, Object> map = getCategoryMap(category, false);
      Map<String, T> result = new LinkedHashMap<String, T>();

      for (Map.Entry<String, Object> e : map.entrySet()) {
         result.put(e.getKey(), (T) e.getValue());
      }

      return result;
   }

   private synchronized Map<String, Object> getCategoryMap(Object category, boolean createIfNotExist) {
      Map<String, Object> map = m_map.get(category);

      if (map == null && createIfNotExist) {
         map = new LinkedHashMap<String, Object>();
         m_map.put(category, map);
      }

      if (map == null) {
         return Collections.emptyMap();
      } else {
         return map;
      }
   }

   public boolean hasAttribute(Object category, String key) {
      Map<String, Object> map = getCategoryMap(category, false);

      return map.containsKey(key);
   }

   public void setAttribute(Object category, String key, Object value) {
      Map<String, Object> map = getCategoryMap(category, true);

      map.put(key, value);
   }
}
