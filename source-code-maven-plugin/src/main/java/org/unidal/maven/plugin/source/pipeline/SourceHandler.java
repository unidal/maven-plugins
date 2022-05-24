package org.unidal.maven.plugin.source.pipeline;

public interface SourceHandler {
	public void handleStart(SourceHandlerContext ctx, SourceScope scope);
	
	public void handleEnd(SourceHandlerContext ctx, SourceScope scope);

	public void handleLine(SourceHandlerContext ctx, String line);
}
