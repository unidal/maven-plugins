package org.unidal.maven.plugin.uml;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class UmlViewModel {
   private String m_webapp;

   private String m_uml;

   private String m_svg;

   private List<File> m_umlFiles;

   private String m_umlFile;

   private boolean m_error;

   private String m_message;

   public UmlViewModel(HttpServletRequest req) {
      m_webapp = req.getContextPath();
   }

   public String getMessage() {
      return m_message;
   }

   public String getSvg() {
      return m_svg;
   }

   public String getUml() {
      return m_uml;
   }

   public String getUmlFile() {
      return m_umlFile;
   }

   public List<File> getUmlFiles() {
      return m_umlFiles;
   }

   public String getWebapp() {
      return m_webapp;
   }

   public boolean isError() {
      return m_error;
   }

   public void setError(boolean error) {
      m_error = error;
   }

   public void setMessage(String message) {
      m_message = message;
   }

   public void setSvg(String svg) {
      m_svg = svg;
   }

   public void setUml(String uml) {
      m_uml = uml;
   }

   public void setUmlFile(String umlFile) {
      m_umlFile = umlFile;
   }

   public void setUmlFiles(List<File> umlFiles) {
      m_umlFiles = umlFiles;
   }
}
