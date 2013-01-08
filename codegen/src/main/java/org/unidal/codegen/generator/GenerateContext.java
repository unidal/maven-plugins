package org.unidal.codegen.generator;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.unidal.codegen.manifest.Manifest;

public interface GenerateContext {
   public void addFileToStorage(Manifest manifest, String content) throws IOException;

   public void closeStorage() throws IOException;

   public void copyFileToStorage(Manifest manifest) throws IOException;

   public URL getDecorateXsl();

   public int getGeneratedFiles();

   public URL getManifestXml();

   public URL getManifestXsl();

   public URL getNormalizeXsl();

   public Map<String, String> getProperties();

   public URL getTemplateXsl(String relativeFile);

   public void log(LogLevel logLevel, String message);

   public void openStorage() throws IOException;

   public static enum LogLevel {
      DEBUG, INFO, ERROR;
   }
}
