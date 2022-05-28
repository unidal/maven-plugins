package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;
import org.unidal.maven.plugin.source.pipeline.SourceScope;

public class ComponentTestCaseClassHandler extends SourceHandlerAdaptor {
	private boolean m_withinClass;

	@Override
	public void handleEnd(SourceHandlerContext ctx, SourceScope scope) {
		m_withinClass = false;

		super.handleEnd(ctx, scope);
	}

	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		String str = line.trim();

		if (str.startsWith("import ")) {
			line = line.replace("org.unidal.lookup.ComponentTestCase", "org.unidal.spring.ComponentTestCase");

			ctx.fireLine(line);
		} else if (str.contains(" extends ") && str.contains("ComponentTestCase")) {
			m_withinClass = true;
			ctx.fireLine(line.replace("ComponentTestCase", "ComponentTestCase"));
		} else if (m_withinClass) {
			if (str.contains("getContainer()")) {
				ctx.fireLine(line.replace("getContainer()", "this"));
			} else if (str.contains("define(")) {
				// delete the line
			} else {
				ctx.fireLine(line);
			}
		} else {
			ctx.fireLine(line);
		}
	}
}
