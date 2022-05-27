package org.unidal.maven.plugin.source.upgrade.handler;

import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

public class InjectAnnotationHandler extends SourceHandlerAdaptor {
	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		String str = line.trim();

		if (str.startsWith("@")) {
			SimpleAnnotation a = new SimpleAnnotation(str);

			if ("Inject".equals(a.getName())) {
				boolean optional = a.getBoolean("optional", false);
				String value = a.getString("value", null);
				int pos = line.indexOf('@');
				String prefix = line.substring(0, pos);

				if (optional) {
					ctx.fireLine(prefix + "@Autowired(required = false)");
				} else {
					ctx.fireLine(prefix + "@Autowired");
				}

				if (value != null) {
					ctx.fireLine("import org.springframework.beans.factory.annotation.Qualifier;");
					ctx.fireLine(prefix + "@Qualifier(" + value + ")");
				}
			} else {
				ctx.fireLine(line);
			}
		} else if (str.startsWith("import ")) {
			ctx.fireLine(line.replace("org.unidal.lookup.annotation.Inject",
			      "org.springframework.beans.factory.annotation.Autowired"));
		} else {
			ctx.fireLine(line);
		}
	}
}
