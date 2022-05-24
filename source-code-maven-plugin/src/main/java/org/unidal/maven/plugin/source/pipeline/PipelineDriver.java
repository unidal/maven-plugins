package org.unidal.maven.plugin.source.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

public class PipelineDriver {
	private void handleEnd(DefaultSource source, SourceHandlerContext ctx, SourceScope scope) {
		source.pop();
		source.pipeline().handleEnd(ctx, scope);
	}

	void handleFile(DefaultSource source, SourceHandlerContext ctx, SourceScope scope, Path path) {
		File file = path.toFile();
		String name = file.getName();

		source.setFolder(file.getParentFile());
		source.setFile(file);

		if (scope.isSource() && name.endsWith(".java")) {
			String relativePath = file.getParent().substring(source.getSourceRoot().length() + 1);
			String className = name.substring(0, name.length() - ".java".length());

			source.setPackage(relativePath.replace('/', '.'));
			source.setClassName(className);
		}

		handleStart(source, ctx, SourceScope.FILE);

		try {
			List<String> lines = Files.readAllLines(path);

			for (String line : lines) {
				source.pipeline().handleLine(ctx, line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		handleEnd(source, ctx, SourceScope.FILE);
	}

	private void handleModule(DefaultSource source, SourceHandlerContext ctx, MavenProject module) throws IOException {
		source.setModule(module);
		handleStart(source, ctx, SourceScope.MODULE);

		for (String sourceRoot : module.getCompileSourceRoots()) {
			handleSourceRoot(source, ctx, sourceRoot, SourceScope.SOURCE);
		}

		for (Resource resource : module.getResources()) {
			handleSourceRoot(source, ctx, resource.getDirectory(), SourceScope.RESOURCE);
		}

		source.setTest(true);

		for (String sourceRoot : module.getTestCompileSourceRoots()) {
			handleSourceRoot(source, ctx, sourceRoot, SourceScope.TEST_SOURCE);
		}

		for (Resource resource : module.getTestResources()) {
			handleSourceRoot(source, ctx, resource.getDirectory(), SourceScope.TEST_RESOURCE);
		}

		handleEnd(source, ctx, SourceScope.MODULE);
	}

	public void handleProject(DefaultSource source, SourceHandlerContext ctx, MavenProject project) throws IOException {
		source.setProject(project);
		handleStart(source, ctx, SourceScope.PROJECT);

		if ("pom".equals(project.getPackaging())) {
			for (MavenProject module : project.getCollectedProjects()) {
				handleModule(source, ctx, module);
			}
		} else {
			handleModule(source, ctx, project);
		}

		handleEnd(source, ctx, SourceScope.PROJECT);
	}

	private void handleSourceRoot(DefaultSource source, SourceHandlerContext ctx, String sourceRoot, SourceScope scope)
	      throws IOException {
		Path basePath = new File(sourceRoot).toPath();

		source.setSourceRoot(sourceRoot);
		handleStart(source, ctx, scope);

		Files.walk(basePath).forEach(path -> {
			if (path.toFile().isFile()) {
				handleFile(source, ctx, scope, path);
			}
		});

		handleEnd(source, ctx, scope);
	}

	private void handleStart(DefaultSource source, SourceHandlerContext ctx, SourceScope scope) {
		source.push(scope);
		source.pipeline().handleStart(ctx, scope);
	}
}
