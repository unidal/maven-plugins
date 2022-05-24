package org.unidal.maven.plugin.source.pipeline;

public class AbstractSourceHandlerContext extends SourceHandlerAdaptor implements SourceHandlerContext {
	private SourcePipeline m_pipeline;

	private Source m_source;

	AbstractSourceHandlerContext m_prev;

	AbstractSourceHandlerContext m_next;

	public AbstractSourceHandlerContext(SourcePipeline pipeline, Source source) {
		m_pipeline = pipeline;
		m_source = source;
	}

	@Override
	public void fireEnd(SourceScope scope) {
		if (m_next != null) {
			m_next.handler().handleEnd(m_next, scope);
		}
	}

	@Override
	public void fireLine(String line) {
		if (m_next != null) {
			m_next.handler().handleLine(m_next, line);
		}
	}

	@Override
	public void fireStart(SourceScope scope) {
		if (m_next != null) {
			m_next.handler().handleStart(m_next, scope);
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
