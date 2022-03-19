package org.unidal.maven.plugin.source.pipeline;

public abstract class AbstractSourceHandlerContext extends SourceHandlerAdaptor implements SourceHandlerContext {
   private SourcePipeline m_pipeline;

   private Source m_source;

   AbstractSourceHandlerContext m_prev;

   AbstractSourceHandlerContext m_next;

   public AbstractSourceHandlerContext(SourcePipeline pipeline, Source source) {
      m_pipeline = pipeline;
      m_source = source;
   }

   @Override
   public void fireComment(String line) {
      if (m_next != null) {
         m_next.handler().handleComment(m_next, line);
      }
   }

   @Override
   public void fireEmptyLine() {
      if (m_next != null) {
         m_next.handler().handleEmptyLine(m_next);
      }
   }

   @Override
   public void fireEnd() {
      if (m_next != null) {
         m_next.handler().handleEnd(m_next);
      }
   }

   @Override
   public void fireLine(String line) {
      if (m_next != null) {
         m_next.handler().handleLine(m_next, line);
      }
   }

   @Override
   public void fireStart() {
      if (m_next != null) {
         m_next.handler().handleStart(m_next);
      }
   }

   @Override
   public void fireStatement(String line) {
      if (m_next != null) {
         m_next.handler().handleStatement(m_next, line);
      }
   }

   @Override
   public SourceHandler handler() {
      throw new UnsupportedOperationException("This should NOT happen!");
   }

   @Override
   public SourcePipeline pipeline() {
      return m_pipeline;
   }

   @Override
   public Source source() {
      return m_source;
   }
}
