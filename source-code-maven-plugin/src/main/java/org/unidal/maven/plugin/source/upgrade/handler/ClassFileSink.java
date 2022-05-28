package org.unidal.maven.plugin.source.upgrade.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.unidal.helper.Files;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;
import org.unidal.maven.plugin.source.pipeline.SourceScope;

public class ClassFileSink extends SourceHandlerAdaptor {
	private ClassSource m_source;

	private boolean m_dirty;

	private int m_files;

	@Override
	public void handleEnd(SourceHandlerContext ctx, SourceScope scope) {
		if (scope.isFile()) {
			if (m_dirty) {
				m_source.writeToFile(ctx.source().getFile());
				m_files++;
			}
		} else if (scope.isProject()) {
			System.out.println(m_files + " files updated.");
		}

		ctx.fireEnd(scope);
	}

	@Override
	public void handleLine(SourceHandlerContext ctx, String line) {
		m_source.addLine(line);

		if (!m_dirty && !line.equals(ctx.source().getLine())) {
			m_dirty = true;
		}

		ctx.fireLine(line);
	}

	@Override
	public void handleStart(SourceHandlerContext ctx, SourceScope scope) {
		if (scope.isFile()) {
			m_source = new ClassSource();
			m_dirty = false;
		}

		ctx.fireStart(scope);
	}

	private class ClassSource {
		private String m_package;

		private Set<String> m_imports = new TreeSet<>();

		private List<String> m_statements = new ArrayList<>();

		public void addLine(String line) {
			if (line.startsWith("package ")) {
				m_package = line;
			} else if (line.startsWith("import ")) {
				m_imports.add(line);
			} else {
				m_statements.add(line);
			}
		}

		public void writeToFile(File file) {
			StringBuilder sb = new StringBuilder(8192);

			sb.append(m_package).append("\r\n");
			sb.append("\r\n");

			for (String line : m_imports) {
				sb.append(line).append("\r\n");
			}

			sb.append("\r\n");

			boolean first = true;

			for (String line : m_statements) {
				if (first && line.trim().length() == 0) {
					// skip empty lines
				} else {
					first = false;
					sb.append(line).append("\r\n");
				}
			}

			System.out.println("Updating " + file);

			try {
				Files.forIO().writeTo(file, sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
