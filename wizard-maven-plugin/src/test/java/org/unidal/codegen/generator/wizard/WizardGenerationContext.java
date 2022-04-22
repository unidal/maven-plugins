package org.unidal.codegen.generator.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.unidal.codegen.framework.GenerationContextSupport;

public class WizardGenerationContext extends GenerationContextSupport {
   private boolean m_debug;

   private boolean m_verbose;

   private File m_manifestXml;

   private AtomicInteger m_generatedFiles = new AtomicInteger();

   public WizardGenerationContext(File projectBaseDir, String type, File manifestXml) throws IOException {
      super(projectBaseDir);

      m_manifestXml = manifestXml;

      File templateFile = new File(manifestXml.getParentFile(), "template.xml");

      if (templateFile.isFile()) {
         getProperties().put("template-file", templateFile.getPath());
      }
   }

   public WizardGenerationContext(File projectBaseDir, String type, File manifestXml, boolean verbose, boolean debug)
         throws IOException {
      this(projectBaseDir, type, manifestXml);

      m_verbose = verbose;
      m_debug = debug;
   }

   @Override
   public File getManifestXml() {
      return m_manifestXml;
   }

   @Override
   public void debug(String message) {
      if (m_debug) {
         info(message);
      }
   }

   @Override
   public AtomicInteger getGeneratedFiles() {
      return m_generatedFiles;
   }

   @Override
   public void info(String message) {
      // getLog().info(message);
   }

   @Override
   public void verbose(String message) {
      if (m_debug || m_verbose) {
         info(message);
      }
   }

   @Override
   protected URL getResource(String name) {
      // TODO Auto-generated method stub
      return null;
   }
}
