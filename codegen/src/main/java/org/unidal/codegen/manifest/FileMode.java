package org.unidal.codegen.manifest;

public enum FileMode {
   CREATE_OR_OVERWRITE("create_or_overwrite"),

   CREATE_IF_NOT_EXISTS("create_if_not_exists");

   private String m_name;

   private FileMode(String name) {
      m_name = name;
   }

   public String getName() {
      return m_name;
   }

   public static FileMode getByName(String name) {
      for (FileMode mode : FileMode.values()) {
         if (mode.getName().equalsIgnoreCase(name)) {
            return mode;
         }
      }

      throw new IllegalArgumentException("No FileMode defined for " + name);
   }
}
