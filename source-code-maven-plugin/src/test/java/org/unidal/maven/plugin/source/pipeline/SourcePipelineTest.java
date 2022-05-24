package org.unidal.maven.plugin.source.pipeline;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class SourcePipelineTest {
	private static Counter COUNTER = new Counter();

	@Test
	public void test() throws IOException {
		DefaultSource source = new DefaultSource();
		DefaultSourcePipeline pipeline = source.pipeline();
		SourceHandlerContext ctx = pipeline.headContext();

		pipeline.addLast(new EmptyLineCounter());
		pipeline.addLast(new CommentCounter());
		pipeline.addLast(new StatementCounter());
		pipeline.addLast(new IfStatementCounter());

		PipelineDriver driver = new PipelineDriver();
		File sourceRoot = new File("src/test/java").getCanonicalFile();
		File file = new File(sourceRoot, getClass().getName().replace('.', '/') + ".java");

		source.setSourceRoot(sourceRoot.getAbsolutePath());
		source.setFile(file);
		driver.handleFile(source, ctx, SourceScope.SOURCE, file.toPath());

		System.out.println(COUNTER);

		Assert.assertEquals(103, COUNTER.m_lines);
		Assert.assertEquals(79, COUNTER.m_statements);
		Assert.assertEquals(4, COUNTER.m_ifStatements);
		Assert.assertEquals(24, COUNTER.m_emptyLines);
	}

	private static class Counter {
		private int m_emptyLines;

		private int m_comments;

		private int m_lines;

		private int m_statements;

		private int m_ifStatements;

		@Override
		public String toString() {
			return String.format("Line: %s\nStatement: %s\nIf: %s, \nComment: %s\nEmtpy Line: %s", m_lines, m_statements,
			      m_ifStatements, m_comments, m_emptyLines);
		}
	}

	private static class EmptyLineCounter extends SourceHandlerAdaptor {
		@Override
		public void handleLine(SourceHandlerContext ctx, String line) {
			if (line.trim().isEmpty()) {
				COUNTER.m_emptyLines++;
			}

			ctx.fireLine(line);
		}
	}

	private static class CommentCounter extends SourceHandlerAdaptor {
		@Override
		public void handleLine(SourceHandlerContext ctx, String line) {
			if (line.trim().startsWith("//")) {
				COUNTER.m_comments++;
			}

			ctx.fireLine(line);
		}
	}

	private static class StatementCounter extends SourceHandlerAdaptor {
		@Override
		public void handleLine(SourceHandlerContext ctx, String line) {
			String str = line.trim();

			if (!str.isEmpty() && !str.startsWith("//")) {
				COUNTER.m_statements++;
			}

			COUNTER.m_lines++;
			ctx.fireLine(line);
		}
	}

	private static class IfStatementCounter extends SourceHandlerAdaptor {
		@Override
		public void handleLine(SourceHandlerContext ctx, String line) {
			if (line.trim().startsWith("if ")) {
				COUNTER.m_ifStatements++;
			}

			ctx.fireLine(line);
		}
	}
}
