package org.unidal.maven.plugin.source.pipeline;

public interface SourceHandler {
   // public void handleClassFile(SourceHandlerContext ctx, String packageName, String className);

   public void handleComment(SourceHandlerContext ctx, String line);

   public void handleEmptyLine(SourceHandlerContext ctx);

    public void handleEnd(SourceHandlerContext ctx);

   public void handleLine(SourceHandlerContext ctx, String line);

    public void handleStart(SourceHandlerContext ctx);

   public void handleStatement(SourceHandlerContext ctx, String line);
}
