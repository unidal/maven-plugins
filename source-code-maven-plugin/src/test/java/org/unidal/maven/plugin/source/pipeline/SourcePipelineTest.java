package org.unidal.maven.plugin.source.pipeline;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SourcePipelineTest {
   private static Counter COUNTER = new Counter();

   @Test
   public void test() throws IOException {
      String path = "src/test/java/org/unidal/maven/plugin/source/pipeline/SourcePipelineTest.java";
      Source source = new FileSource(new File(path));
      SourcePipeline pipeline = source.pipeline();

      pipeline.addLast(new EmptyLineCounter());
      pipeline.addLast(new CommentCounter());
      pipeline.addLast(new StatementCounter());
      pipeline.addLast(new IfStatementCounter());

      source.scanFile();

      System.out.println(COUNTER);
   }

   private static class Counter {
      private int m_emptyLines;

      private int m_comments;

      private int m_lines;

      private int m_statements;

      private int m_ifStatements;

      @Override
      public String toString() {
         return String.format("Line: %s\nStatement: %s\nIf: %s, \nComment: %s\nEmtpy Line: %s", m_lines, m_statements,
               m_ifStatements, m_comments, m_emptyLines);
      }
   }

   private static class EmptyLineCounter extends SourceHandlerAdaptor {
      @Override
      public void handleEmptyLine(SourceHandlerContext ctx) {
         COUNTER.m_emptyLines++;
         ctx.fireEmptyLine();
      }
   }

   private static class CommentCounter extends SourceHandlerAdaptor {
      @Override
      public void handleComment(SourceHandlerContext ctx, String line) {
         COUNTER.m_comments++;
         ctx.fireComment(line);
      }
   }

   private static class StatementCounter extends SourceHandlerAdaptor {
      @Override
      public void handleStatement(SourceHandlerContext ctx, String line) {
         COUNTER.m_statements++;
         ctx.fireStatement(line);
      }

      @Override
      public void handleLine(SourceHandlerContext ctx, String line) {
         System.out.println(getClass().getSimpleName() + ": handleLine : " + line);
         COUNTER.m_lines++;

         if (line.trim().isEmpty()) {
            ctx.pipeline().handleEmptyLine(ctx);
         } else {
            ctx.pipeline().handleStatement(ctx, line);
         }
      }
   }

   private static class IfStatementCounter extends SourceHandlerAdaptor {
      @Override
      public void handleStatement(SourceHandlerContext ctx, String line) {
         if (line.trim().startsWith("if ")) {
            COUNTER.m_ifStatements++;
         }

         ctx.fireStatement(line);
      }
   }
}
