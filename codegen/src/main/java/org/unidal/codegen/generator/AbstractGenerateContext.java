package org.unidal.codegen.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.IOUtil;
import org.unidal.codegen.manifest.FileMode;
import org.unidal.codegen.manifest.Manifest;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.tuple.Pair;

public abstract class AbstractGenerateContext implements GenerateContext {
   private File m_projectBase;

   private File m_sourceOutput;

   private String m_resourceBase;

   private int m_generatedFiles;

   private Map<String, String> m_properties;

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

      m_properties = new HashMap<String, String>();
      m_properties.put("generated-java", m_projectBase.getPath());
      m_properties.put("src-main-java", "src/main/java");
      m_properties.put("src-main-resources", "src/main/resources");
      m_properties.put("src-main-webapp", "src/main/webapp");
      m_properties.put("src-test-java", "src/test/java");
      m_properties.put("src-test-resources", "src/test/resources");
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

   private void copyFile(final FileMode mode, final String from, final File to) throws IOException {
      int pos = from.indexOf(".jar!");

      if (pos > 0 && from.startsWith("file:")) {
         final File jarFile = new File(from.substring(5, pos + 4));
         final String prefix = from.substring(pos + 6);
         final List<Pair<File, String>> list = new ArrayList<Pair<File, String>>();

         Scanners.forJar().scan(jarFile, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (path.startsWith(prefix)) {
                  String relativePath = path.substring(prefix.length() + 1);
                  File target = new File(to, relativePath);

                  switch (mode) {
                  case CREATE_IF_NOT_EXISTS:
                     if (target.exists()) {
                        break;
                     }
                  case CREATE_OR_OVERWRITE:
                     list.add(new Pair<File, String>(target, path));
                     break;
                  }
               }

               return Direction.NEXT;
            }
         });

         for (Pair<File, String> item : list) {
            File target = item.getKey();
            String jarUrl = jarFile.toURI().toURL().toExternalForm();
            byte[] data = Files.forIO().readFrom(new URL("jar:" + jarUrl + "!/" + item.getValue()).openStream());

            Files.forIO().writeTo(target, data);
            log(LogLevel.INFO, target + " generated");
            m_generatedFiles++;
         }
      } else {
         final List<String> list = new ArrayList<String>();

         Scanners.forDir().scan(new File(from), new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (new File(path).isFile()) {
                  list.add(path);
               }

               return Direction.DOWN;
            }
         });

         for (String item : list) {
            File target = new File(to, item);
            File source = new File(from, item);

            switch (mode) {
            case CREATE_IF_NOT_EXISTS:
               if (target.exists()) {
                  break;
               }
            case CREATE_OR_OVERWRITE:
               byte[] data = Files.forIO().readFrom(source);
               Files.forIO().writeTo(target, data);
               log(LogLevel.INFO, target + " generated");
               m_generatedFiles++;
               break;
            }
         }
      }
   }

   @Override
   public void copyFileToStorage(Manifest manifest) throws IOException {
      FileMode mode = manifest.getMode();
      String from = manifest.getTemplate();
      String to = manifest.getPath();

      if (from == null || to == null) {
         throw new IllegalArgumentException(String.format("Property from(%s) and to(%s) must be both specified!", from, to));
      }

      File toFile = new File(m_sourceOutput, to);
      URL url = getResource(from);
      String fromFile = url.getFile();

      copyFile(mode, fromFile, toFile);
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

   @Override
   public Map<String, String> getProperties() {
      return m_properties;
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

   public URL getTemplateXsl(String relativeFile) {
      return getResource(relativeFile);
   }

   public void openStorage() throws IOException {
      // do nothing here
   }
}
