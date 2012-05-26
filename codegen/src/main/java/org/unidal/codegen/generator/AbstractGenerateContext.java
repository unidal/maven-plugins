package org.unidal.codegen.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.codehaus.plexus.util.IOUtil;

import org.unidal.codegen.manifest.FileMode;
import org.unidal.codegen.manifest.Manifest;

public abstract class AbstractGenerateContext implements GenerateContext {
   private File m_projectBase;

   private File m_sourceOutput;

   private String m_resourceBase;

   private int m_generatedFiles;

   public AbstractGenerateContext(File projectBase, String resourceBase, String sourceOutput) {
      m_projectBase = projectBase;
      m_resourceBase = resourceBase;

      if (sourceOutput != null) {
         if (new File(sourceOutput).isAbsolute()) {
            m_sourceOutput = new File(sourceOutput);
         } else {
            m_sourceOutput = new File(m_projectBase, sourceOutput);
         }
      }
   }

   @Override
   public Map<String, String> getProperties() {
      return Collections.emptyMap();
   }

   public void addFileToStorage(Manifest manifest, String content) throws IOException {
      FileMode mode = manifest.getMode();
      Writer writer = null;
      File file = new File(m_sourceOutput, manifest.getPath());

      file = file.getCanonicalFile();
      file.getParentFile().mkdirs();

      switch (mode) {
      case CREATE_OR_OVERWRITE:
         writer = new FileWriter(file);
         break;
      case CREATE_IF_NOT_EXISTS:
         if (!file.exists()) {
            writer = new FileWriter(file);
         }

         break;
      case CREATE_OR_APPEND:
         writer = new FileWriter(file, true);
         break;
      }

      if (writer != null) {
         IOUtil.copy(content, writer);
         log(LogLevel.INFO, file + " generated");
         writer.close();
         m_generatedFiles++;
      }
   }

   public void closeStorage() throws IOException {
      // do nothing here
   }

   public URL getDecorateXsl() {
      return getResource("decorate.xsl");
   }

   public int getGeneratedFiles() {
      return m_generatedFiles;
   }

   public URL getManifestXsl() {
      return getResource("manifest.xsl");
   }

   public URL getNormalizeXsl() {
      return getResource("normalize.xsl");
   }

   public URL getTemplateXsl(String relativeFile) {
      return getResource(relativeFile);
   }

   public void openStorage() throws IOException {
      // do nothing here
   }

   protected URL getResource(String name) {
      String path = m_resourceBase + "/" + name;
      URL url = getClass().getResource(path);

      if (url != null) {
         return url;
      } else {
         throw new RuntimeException("Can't find resource: " + path);
      }
   }
}
