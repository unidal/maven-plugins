package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

public class ContainerHolderClassHandler extends SourceHandlerAdaptor {
	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		String str = line.trim();

		if (str.startsWith("import ")) {
			line = line.replace("org.unidal.lookup.ContainerHolder", "org.unidal.spring.ApplicationContextHolder");
			line = line.replace("org.unidal.lookup.ComponentLookupException", "org.springframework.beans.BeansException");

			ctx.fireLine(line);
		} else if (str.contains(" extends ") && str.contains("ContainerHolder")) {
			ctx.fireLine(line.replace("ContainerHolder", "ApplicationContextHolder"));
		} else if (str.contains("throws ComponentLookupException")) {
			ctx.fireLine(line.replace("throws ComponentLookupException", "throws BeansException"));
		} else {
			ctx.fireLine(line);
		}
	}
}
