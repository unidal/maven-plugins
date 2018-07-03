package org.unidal.codegen.generator.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.unidal.codegen.generator.GenerateContextSupport;

public class WizardGenerateContext extends GenerateContextSupport {
   private boolean m_debug;

   private boolean m_verbose;

   private URL m_manifestXml;

   public WizardGenerateContext(File projectBaseDir, String type, File manifestXml) throws IOException {
      super("/META-INF/wizard/" + type, projectBaseDir);

      m_manifestXml = manifestXml.toURI().toURL();

      File templateFile = new File(manifestXml.getParentFile(), "template.xml");

      if (templateFile.isFile()) {
         getProperties().put("template-file", templateFile.getPath());
      }
   }

   public WizardGenerateContext(File projectBaseDir, String type, File manifestXml, boolean verbose, boolean debug)
         throws IOException {
      this(projectBaseDir, type, manifestXml);

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
