package com.site.maven.plugin.project;

import java.io.File;

import org.apache.maven.model.Build;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

import com.site.lookup.ComponentTestCase;
import com.site.maven.plugin.common.Injector;

public class CreateTemplateMojoTest extends ComponentTestCase {
	public void testExecute() throws Exception {
		CreateTemplateMojo mojo = new CreateTemplateMojo();

		Injector.setField(mojo, "project", createMavenProject());

		// mojo.execute();
	}

	private MavenProject createMavenProject() throws Exception {
		File baseDir = new File("C:/dev/workshop/wdbc/wdbc-apps-others/wdbc-ebay-arch");
		MavenProject project = new MavenProject();
		Build build = new Build();
		Resource resource = new Resource();
		Resource testResource = new Resource();

		project.setFile(new File(baseDir, "wdbc-ebay-arch.jar"));
		project.setBuild(build);
		build.setFinalName("wdbc-ebay-arch-1.0.0");
		build.setSourceDirectory(baseDir + "/src/main/java");
		project.getBuild().addResource(resource);
		project.getBuild().addTestResource(testResource);
		resource.setDirectory(baseDir + "/src/main/resources");
		testResource.setDirectory(baseDir + "/src/test/resources");

		return project;
	}
}
