package org.unidal.codegen.framework;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface GenerationContext {
   public void debug(String message);

   public URL getDecorateXsl();

   public File getFile(String file);

   public AtomicInteger getGeneratedFiles();

   public File getManifestXml();

   public URL getManifestXsl();

   public URL getNormalizeXsl();

   public Map<String, String> getProperties();

   public URL getStructureXml();

   public URL getTemplateXsl(String template);

   public void info(String message);

   public void verbose(String message);
}
