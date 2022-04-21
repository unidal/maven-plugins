package org.unidal.codegen.generator.wizard;

import java.io.File;
import java.io.IOException;

import org.unidal.codegen.framework.GenerationContextSupport;

public class WizardGenerationContext extends GenerationContextSupport {
   private boolean m_debug;

   private boolean m_verbose;

   private File m_manifestXml;

   public WizardGenerationContext(File projectBaseDir, String type, File manifestXml) throws IOException {
      super("/META-INF/wizard/" + type, projectBaseDir);

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
}
