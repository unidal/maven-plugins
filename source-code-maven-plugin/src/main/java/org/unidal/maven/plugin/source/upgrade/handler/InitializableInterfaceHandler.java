package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

public class InitializableInterfaceHandler extends SourceHandlerAdaptor {
	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		String str = line.trim();

		if (str.startsWith("import ")) {
			if (str.equals("import org.unidal.lookup.extension.InitializationException;")) {
				// ignore it
			} else {
				ctx.fireLine(line.replace("org.unidal.lookup.extension.Initializable",
				      "org.springframework.beans.factory.InitializingBean"));
			}
		} else if (str.contains(" implements ") && str.contains(" Initializable")) {
			ctx.fireLine(line.replace("Initializable", "InitializingBean"));
		} else if (str.startsWith("public void initialize() throws InitializationException")) {
			String prefix = line.substring(0, line.indexOf("public"));

			ctx.fireLine(prefix + "public void afterPropertiesSet() throws Exception {");
		} else if (str.startsWith("throw new InitializationException(")) {
			ctx.fireLine("import org.springframework.beans.factory.BeanInitializationException;");
			ctx.fireLine(line.replace("throw new InitializationException(", "throw new BeanInitializationException("));
		} else {
			ctx.fireLine(line);
		}
	}
}
