package org.unidal.maven.plugin.source.pipeline;

public class DefaultSourcePipeline extends SourceHandlerAdaptor implements SourcePipeline {
   private Source m_source;

   private AbstractSourceHandlerContext m_head;

   private AbstractSourceHandlerContext m_tail;

   public DefaultSourcePipeline(Source source) {
      m_source = source;
      m_head = new HeadContext(this, m_source);
      m_tail = new TailContext(this, m_source);

      m_head.m_next = m_tail;
      m_tail.m_prev = m_head;
   }

   @Override
   public void addLast(SourceHandler handler) {
      HandlerContext ctx = new HandlerContext(this, m_source, handler);
      AbstractSourceHandlerContext prev = m_tail.m_prev;

      ctx.m_prev = prev;
      ctx.m_next = m_tail;
      prev.m_next = ctx;
      m_tail.m_prev = ctx;
   }

   @Override
   protected SourceHandlerContext getContext(SourceHandlerContext ctx) {
      return m_head; // always starts from beginning
   }

   public AbstractSourceHandlerContext headContext() {
      return m_head;
   }

   private static class HandlerContext extends AbstractSourceHandlerContext {
      private SourceHandler m_handler;

      public HandlerContext(SourcePipeline pipeline, Source source, SourceHandler handler) {
         super(pipeline, source);

         m_handler = handler;
      }

      @Override
      public SourceHandler handler() {
         return m_handler;
      }

      @Override
      public String toString() {
         return String.format("%s[handler=%s]", getClass().getSimpleName(), m_handler);
      }
   }

   private static class HeadContext extends AbstractSourceHandlerContext {
      public HeadContext(SourcePipeline pipeline, Source source) {
         super(pipeline, source);
      }
   }

   private static class TailContext extends AbstractSourceHandlerContext {
      private SourceHandler m_handler = new SourceHandlerAdaptor();

      public TailContext(SourcePipeline pipeline, Source source) {
         super(pipeline, source);
      }

      @Override
      public SourceHandler handler() {
         // do nothing at tail
         return m_handler;
      }
   }
}
