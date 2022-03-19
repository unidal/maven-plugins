package org.unidal.maven.plugin.source.pipeline;

public class SourceHandlerAdaptor implements SourceHandler {
   protected SourceHandlerContext getContext(SourceHandlerContext ctx) {
      return ctx;
   }

   @Override
   public void handleComment(SourceHandlerContext ctx, String line) {
      getContext(ctx).fireComment(line);
   }

   @Override
   public void handleEmptyLine(SourceHandlerContext ctx) {
      getContext(ctx).fireEmptyLine();
   }

   @Override
   public void handleLine(SourceHandlerContext ctx, String line) {
      getContext(ctx).fireLine(line);
   }

   @Override
   public void handleStatement(SourceHandlerContext ctx, String line) {
      getContext(ctx).fireStatement(line);
   }

   @Override
   public void handleEnd(SourceHandlerContext ctx) {
      getContext(ctx).fireEnd();
   }

   @Override
   public void handleStart(SourceHandlerContext ctx) {
      getContext(ctx).fireStart();
   }
}
