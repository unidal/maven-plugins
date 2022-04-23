package org.unidal.codegen.framework;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class GenerationContextSupport implements GenerationContext {
   private File m_projectBaseDir;

   private Map<String, String> m_properties;

   public GenerationContextSupport(File projectBaseDir) throws IOException {
      m_projectBaseDir = projectBaseDir.getCanonicalFile();
      m_properties = new HashMap<String, String>();

      m_properties.put("base-dir", m_projectBaseDir.getPath());
      m_properties.put("src-main-java", "src/main/java");
      m_properties.put("src-main-resources", "src/main/resources");
      m_properties.put("src-main-webapp", "src/main/webapp");
      m_properties.put("src-test-java", "src/test/java");
      m_properties.put("src-test-resources", "src/test/resources");
   }

   public URL getDecorateXsl() {
      return getResource("decorate.xsl");
   }

   @Override
   public File getFile(String file) {
      if (file.startsWith("/")) {
         return new File(file);
      } else {
         return new File(m_projectBaseDir, file);
      }
   }

   public URL getManifestXsl() {
      return getResource("manifest.xsl");
   }

   public URL getNormalizeXsl() {
      return getResource("normalize.xsl");
   }

   public File getPath(String name) {
      if (name.startsWith("/") || name.contains(":")) {
         return new File(name);
      } else {
         return new File(m_projectBaseDir, name);
      }
   }

   public Map<String, String> getProperties() {
      return m_properties;
   }

   public String getProperty(String name, String defaultValue) {
      String value = m_properties.get(name);

      if (value == null) {
         return defaultValue;
      } else {
         return value;
      }
   }

   protected abstract URL getResource(String name);

   @Override
   public URL getStructureXml() {
      return getResource("../structure.xml");
   }

   public URL getTemplateXsl(String relativeFile) {
      return getResource(relativeFile);
   }
}
