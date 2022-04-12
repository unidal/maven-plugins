package org.unidal.maven.plugin.source.pipeline;

import java.io.IOException;

public interface Source {
   public SourcePipeline pipeline();

   public void scanFile() throws IOException;
}
