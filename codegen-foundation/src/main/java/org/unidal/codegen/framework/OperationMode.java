package org.unidal.codegen.framework;

public enum OperationMode {
   APPLY_TEMPLATE("apply_template"),

   COPY_RESOURCES("copy_resources");

   private String m_name;

   private OperationMode(String name) {
      m_name = name;
   }

   public static OperationMode getByName(String name) {
      for (OperationMode mode : OperationMode.values()) {
         if (mode.getName().equalsIgnoreCase(name)) {
            return mode;
         }
      }

      throw new IllegalArgumentException("No OperationMode defined for " + name);
   }

   public String getName() {
      return m_name;
   }
}
