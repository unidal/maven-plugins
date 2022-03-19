package org.unidal.maven.plugin.source.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileSource implements Source {
   private File m_file;

   private DefaultSourcePipeline m_pipeline;

   public FileSource(File file) {
      m_file = file;
      m_pipeline = new DefaultSourcePipeline(this);
   }

   @Override
   public SourcePipeline pipeline() {
      return m_pipeline;
   }

   @Override
   public void scanFile() throws IOException {
      BufferedReader reader = new BufferedReader(new FileReader(m_file));

      try {
         AbstractSourceHandlerContext ctx = m_pipeline.headContext();

         ctx.handleStart(ctx);

         while (true) {
            final String line = reader.readLine();

            if (line == null) {
               break;
            }

            ctx.handleLine(ctx, line);
         }

         ctx.handleEnd(ctx);
      } finally {
         try {
            reader.close();
         } catch (IOException e) {
            // ignore it
         }
      }
   }

   @Override
   public String toString() {
      return String.format("%s[file=%s]", getClass().getSimpleName(), m_file);
   }
}
