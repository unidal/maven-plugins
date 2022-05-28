package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;
import org.unidal.maven.plugin.source.pipeline.SourceScope;

public class ContainerHolderClassHandler extends SourceHandlerAdaptor {
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
			line = line.replace("org.unidal.lookup.ContainerHolder", "org.unidal.spring.BeanFactoryHolder");
			line = line.replace("org.unidal.lookup.PlexusContainer", "org.unidal.spring.BeanFactoryHolder");
			line = line.replace("org.unidal.lookup.ComponentLookupException", "org.springframework.beans.BeansException");

			ctx.fireLine(line);
		} else if (str.contains(" extends ") && str.contains("ContainerHolder")) {
			m_withinClass = true;
			ctx.fireLine(line.replace("ContainerHolder", "BeanFactoryHolder"));
		} else if (str.contains("throws ComponentLookupException")) {
			ctx.fireLine(line.replace("throws ComponentLookupException", "throws BeansException"));
		} else if (m_withinClass && str.contains("getContainer()")) {
			ctx.fireLine(line.replace("getContainer()", "this"));
		} else if (str.contains("PlexusContainer")) {
			ctx.fireLine(line.replace("PlexusContainer", "BeanFactoryHolder"));
		} else {
			ctx.fireLine(line);
		}
	}
}
