package org.unidal.maven.plugin.source.pipeline;

public interface SourceHandlerContext {
   public void fireEnd(SourceScope scope);

   public void fireLine(String line);

   public void fireStart(SourceScope scope);

   public SourceHandler handler();

   public SourcePipeline pipeline();

   public Source source();
}
