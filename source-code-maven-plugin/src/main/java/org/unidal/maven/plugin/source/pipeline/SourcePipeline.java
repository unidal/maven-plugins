package org.unidal.maven.plugin.source.pipeline;

public interface SourcePipeline extends SourceHandler {
   public void addLast(SourceHandler handler);
}
