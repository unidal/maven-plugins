package com.site.codegen.manifest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DefaultManifestCreator implements ManifestCreator {
   public String create(String generatedContent, String userContent) throws IOException {
      StringBuilder sb = new StringBuilder(256);
      File generated = createTempFile(generatedContent);
      File user = createTempFile(userContent);

      sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");
      sb.append("<manifest>\r\n");
      sb.append("   <file path=\"" + generated.getAbsolutePath() + "\"/>\r\n");
      sb.append("   <file path=\"" + user.getAbsolutePath() + "\"/>\r\n");
      sb.append("</manifest>\r\n");

      File manifest = createTempFile(sb.toString());
      return manifest.getAbsolutePath();
   }

   private File createTempFile(String content) throws IOException {
      File file = File.createTempFile("tmp", ".xml");
      FileWriter writer = new FileWriter(file);

      writer.write(content);
      writer.close();
//      file.deleteOnExit();
      return file.getCanonicalFile();
   }
}
