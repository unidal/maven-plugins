package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

public class NamedAnnotationHandler extends SourceHandlerAdaptor {
	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		String str = line.trim();

		if (str.startsWith("@")) {
			SimpleAnnotation a = new SimpleAnnotation(str);

			if ("Named".equals(a.getName())) {
				// TODO Spring component does not support explicit type well
				// String type = a.getString("type", null);
				String value = a.getString("value", null);
				String is = a.getString("instantiationStrategy", "");
				int pos = line.indexOf('@');
				String prefix = line.substring(0, pos);

				if (value != null) {
					ctx.fireLine(prefix + "@Component(" + value + ")");
				} else {
					ctx.fireLine(prefix + "@Component");
				}

				if (is.length() > 0) {
					if (is.equals("Named.PER_LOOKUP") || is.equals("per-lookup")) {
						ctx.fireLine("import org.springframework.context.annotation.Scope;");
						ctx.fireLine(prefix + "@org.springframework.context.annotation.Scope(\"prototype\")");
					} else {
						System.err.println("@Named.instantiationStrategy(" + is + ") is NOT supported yet!");
					}
				}
			} else {
				ctx.fireLine(line);
			}
		} else if (str.startsWith("import ")) {
			ctx.fireLine(line.replace("org.unidal.lookup.annotation.Named", "org.springframework.stereotype.Component"));
		} else {
			ctx.fireLine(line);
		}
	}
}
