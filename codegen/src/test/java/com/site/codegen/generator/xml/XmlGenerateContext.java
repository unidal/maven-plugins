package com.site.codegen.generator.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.site.codegen.generator.GenerateContextSupport;

public class XmlGenerateContext extends GenerateContextSupport {
   private boolean m_debug;

   private boolean m_verbose;

   private URL m_manifestXml;

   public XmlGenerateContext(File projectBaseDir, File manifestXml) throws IOException {
      super("/META-INF/dal/xml", projectBaseDir);

      m_manifestXml = manifestXml.toURI().toURL();
   }

   public XmlGenerateContext(File projectBaseDir, File manifestXml, boolean verbose, boolean debug)
         throws IOException {
      this(projectBaseDir, manifestXml);

      m_verbose = verbose;
      m_debug = debug;
   }

   @Override
   public URL getManifestXml() {
      return m_manifestXml;
   }

   @Override
   public void log(LogLevel logLevel, String message) {
      switch (logLevel) {
      case DEBUG:
         if (m_debug) {
            System.out.println(message);
         }
         break;
      case INFO:
         if (m_debug || m_verbose) {
            System.out.println(message);
         }
         break;
      case ERROR:
         System.out.println(message);
         break;
      }
   }
}
