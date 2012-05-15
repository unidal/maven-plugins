package com.site.maven.plugin.common;

import java.lang.reflect.Field;

public class Injector {
   public static void setField(Object obj, String name, Object value) {
      try {
         Field field = obj.getClass().getDeclaredField(name);

         field.setAccessible(true);
         field.set(obj, value);
      } catch (Exception e) {
         throw new RuntimeException("Error when setting field value. name: " + name + ", value: " + value
               + ", object: " + obj, e);
      }
   }

   @SuppressWarnings("unchecked")
   public static <T> T getField(Object obj, String name) {
      try {
         Field field = obj.getClass().getDeclaredField(name);

         field.setAccessible(true);
         return (T) field.get(obj);
      } catch (Exception e) {
         throw new RuntimeException("Error when setting field value. name: " + name + ", object: " + obj, e);
      }
   }
}
