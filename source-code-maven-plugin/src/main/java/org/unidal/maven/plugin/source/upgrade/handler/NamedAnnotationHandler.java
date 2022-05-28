package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

public class NamedAnnotationHandler extends SourceHandlerAdaptor {
	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		boolean test = ctx.source().getParentScope().isTest();
		String str = line.trim();

		if (str.startsWith("@")) {
			SimpleAnnotation a = new SimpleAnnotation(str);

			if ("Named".equals(a.getName())) {
				String type = a.getString("type", null);
				String value = a.getString("value", null);
				String is = a.getString("instantiationStrategy", "");
				int pos = line.indexOf('@');
				String prefix = line.substring(0, pos);

				if (test) {
					ctx.fireLine(prefix + "@TestComponent");
				} else {
					ctx.fireLine(prefix + "@Component");
				}

				ctx.fireLine("import org.unidal.spring.Named;");

				if (type == null) {
					if (value != null) {
						ctx.fireLine(prefix + "@Named(" + value + ")");
					} else {
						ctx.fireLine(prefix + "@Named");
					}
				} else {
					if (value != null) {
						ctx.fireLine(prefix + "@Named(type = " + type + ", value = " + value + ")");
					} else {
						ctx.fireLine(prefix + "@Named(type = " + type + ")");
					}
				}

				if (is.length() > 0) {
					if (is.equals("Named.PER_LOOKUP") || is.equals("per-lookup")) {
						ctx.fireLine("import org.springframework.context.annotation.Scope;");
						ctx.fireLine("import org.springframework.beans.factory.config.ConfigurableBeanFactory;");
						ctx.fireLine(prefix + "@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)");
					} else {
						System.err.println("@Named.instantiationStrategy(" + is + ") is NOT supported yet!");
					}
				}
			} else {
				ctx.fireLine(line);
			}
		} else if (str.startsWith("import ")) {
			if (test) {
				ctx.fireLine(line.replace("org.unidal.lookup.annotation.Named",
				      "org.springframework.boot.test.context.TestComponent"));
			} else {
				ctx.fireLine(
				      line.replace("org.unidal.lookup.annotation.Named", "org.springframework.stereotype.Component"));
			}
		} else {
			ctx.fireLine(line);
		}
	}
}
