package org.unidal.maven.plugin.source;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.unidal.helper.Reflects;

public class UpgradeMojoTest {
	@Test
	public void testMojo() throws Exception {
		UpgradeMojo mojo = new UpgradeMojo();
		MavenProject project = new MavenProject();
		File baseDir = new File("/Users/qmwu2000/project/lab/cat-next/cat-meta-service");

		project.setName(baseDir.getName());
		project.getCompileSourceRoots().add(new File(baseDir, "src/main/java").getCanonicalPath());
		project.getTestCompileSourceRoots().add(new File(baseDir, "src/test/java").getCanonicalPath());

		Reflects.forField().setDeclaredFieldValue(UpgradeMojo.class, "m_project", mojo, project);

		mojo.execute();
	}
}
