package com.site.codegen.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.IOUtil;

import com.site.codegen.manifest.FileMode;
import com.site.codegen.manifest.Manifest;

public abstract class GenerateContextSupport implements GenerateContext {
   private File m_projectBaseDir;

   private String m_resourceBasePath;

   private int m_generatedFiles;

   private Map<String, String> m_properties;

   public GenerateContextSupport(String resourceBasePath, File projectBaseDir) {
      m_projectBaseDir = projectBaseDir;
      m_resourceBasePath = resourceBasePath;
      m_properties = new HashMap<String, String>();

      m_properties.put("src-main-java", "src/main/java");
      m_properties.put("src-main-resources", "src/main/resources");
      m_properties.put("src-main-webapp", "src/main/webapp");
      m_properties.put("src-test-java", "src/test/java");
      m_properties.put("src-test-resources", "src/test/resources");
      
      configure(m_properties);
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

   public File getPath(String name) {
      String path = m_properties.get(name);

      if (path == null) {
         return new File(m_projectBaseDir, "target/" + name.replace('.', '/'));
      } else if (path.startsWith("/")) {
         return new File(path);
      } else {
         return new File(m_projectBaseDir, path);
      }
   }

   protected void configure(Map<String, String> properties) {
      // to be override
   }

   public void addFileToStorage(Manifest manifest, String content) throws IOException {
      FileMode mode = manifest.getMode();
      Writer writer = null;
      File file = new File(m_projectBaseDir, manifest.getPath()).getCanonicalFile();

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
      String path = m_resourceBasePath + "/" + name;
      URL url = getClass().getResource(path);

      if (url != null) {
         return url;
      } else {
         throw new RuntimeException("Can't find resource: " + path);
      }
   }
}
