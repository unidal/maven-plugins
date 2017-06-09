package org.unidal.maven.plugin.project.group.transform;

import org.xml.sax.Attributes;

import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public GroupMetadata buildMetadata(Attributes attributes) {
      GroupMetadata metadata = new GroupMetadata();

      return metadata;
   }

   @Override
   public Plugin buildPlugin(Attributes attributes) {
      Plugin plugin = new Plugin(null);

      return plugin;
   }

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class || type == Boolean.TYPE) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class || type == Integer.TYPE) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class || type == Long.TYPE) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class || type == Short.TYPE) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class || type == Float.TYPE) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class || type == Double.TYPE) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class || type == Byte.TYPE) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class || type == Character.TYPE) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }
}
