package org.unidal.maven.plugin.source.pipeline;

public interface SourceHandlerContext {
   // public void fireClassFile(String packageName, String className);

   public void fireComment(String line);

   public void fireEmptyLine();

   public void fireEnd();

   public void fireLine(String line);

   public void fireStart();

   public void fireStatement(String line);

   public SourceHandler handler();

   public SourcePipeline pipeline();

   public Source source();
}
