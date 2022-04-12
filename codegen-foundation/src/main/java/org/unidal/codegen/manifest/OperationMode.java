package org.unidal.codegen.manifest;

public enum OperationMode {
   APPLY_TEMPLATE("apply_template"),

   COPY_RESOURCES("copy_resources");

   private String m_name;

   private OperationMode(String name) {
      m_name = name;
   }

   public String getName() {
      return m_name;
   }

   public static OperationMode getByName(String name, OperationMode defaultValue) {
      for (OperationMode mode : OperationMode.values()) {
         if (mode.getName().equalsIgnoreCase(name)) {
            return mode;
         }
      }

      return defaultValue;
   }
}
