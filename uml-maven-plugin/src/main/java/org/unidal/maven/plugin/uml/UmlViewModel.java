package org.unidal.maven.plugin.uml;

import javax.servlet.http.HttpServletRequest;

public class UmlViewModel {
   private String m_webapp;

   private String m_uml;

   private byte[] m_image;

   private String m_svg;

   public UmlViewModel(HttpServletRequest req) {
      m_webapp = req.getContextPath();
   }

   public byte[] getImage() {
      return m_image;
   }

   public String getSvg() {
      return m_svg;
   }

   public String getUml() {
      return m_uml;
   }

   public String getWebapp() {
      return m_webapp;
   }

   public void setImage(byte[] image) {
      m_image = image;
   }

   public void setSvg(String svg) {
      m_svg = svg;
   }

   public void setUml(String uml) {
      m_uml = uml;
   }
}
