package org.unidal.maven.plugin.source.pipeline;

public class SourceHandlerAdaptor implements SourceHandler {
	protected SourceHandlerContext getContext(SourceHandlerContext ctx) {
		return ctx;
	}

	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		getContext(ctx).fireLine(line);
	}

	@Override
	public void handleEnd(SourceHandlerContext ctx, SourceScope scope) {
		getContext(ctx).fireEnd(scope);
	}

	@Override
	public void handleStart(SourceHandlerContext ctx, SourceScope scope) {
		getContext(ctx).fireStart(scope);
	}
}
