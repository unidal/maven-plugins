package org.unidal.maven.plugin.project.plugin.transform;

import org.xml.sax.Attributes;

import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public class DefaultSaxMaker implements IMaker<Attributes> {

   @Override
   public PluginMetadata buildMetadata(Attributes attributes) {
      PluginMetadata metadata = new PluginMetadata();

      return metadata;
   }

   @Override
   public String buildVersion(Attributes attributes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Versioning buildVersioning(Attributes attributes) {
      Versioning versioning = new Versioning();

      return versioning;
   }

   @Override
   public Versions buildVersions(Attributes attributes) {
      Versions versions = new Versions();

      return versions;
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
