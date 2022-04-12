package org.unidal.codegen.manifest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Manifest {
   private OperationMode m_op;

   private String m_template;

   private String m_path;

   private FileMode m_mode;

   private Map<String, String> m_properties;

   public void addProperty(String name, String value) {
      if (m_properties == null) {
         m_properties = new HashMap<String, String>();
      }

      m_properties.put(name, value);
   }

   public FileMode getMode() {
      return m_mode;
   }

   public OperationMode getOp() {
      return m_op;
   }

   public String getPath() {
      return m_path;
   }

   public Map<String, String> getProperties() {
      if (m_properties == null) {
         return Collections.emptyMap();
      } else {
         return m_properties;
      }
   }

   public String getTemplate() {
      return m_template;
   }

   public void setMode(FileMode mode) {
      m_mode = mode;
   }

   public void setOp(OperationMode op) {
      m_op = op;
   }

   public void setPath(String path) {
      m_path = path;
   }

   public void setTemplate(String template) {
      m_template = template;
   }
}
