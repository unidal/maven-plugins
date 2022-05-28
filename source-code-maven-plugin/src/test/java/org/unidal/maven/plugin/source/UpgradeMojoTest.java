package org.unidal.maven.plugin.source;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.unidal.helper.Reflects;

public class UpgradeMojoTest {
	private File m_rootDir = new File("/Users/qmwu2000/project/unidal/cat-next");

	@Test
	public void testOne() throws Exception {
		checkProject("cat-meta-service");
	}

	@Test
	public void testAll() throws Exception {
		for (String name : m_rootDir.list()) {
			if (name.startsWith("cat-")) {
				checkProject(name);
			}
		}
	}

	private void checkProject(String name) throws Exception {
		UpgradeMojo mojo = new UpgradeMojo();
		MavenProject project = new MavenProject();
		File baseDir = new File(m_rootDir, name);

		project.setName(baseDir.getName());
		project.getCompileSourceRoots().add(new File(baseDir, "src/main/java").getCanonicalPath());
		project.getTestCompileSourceRoots().add(new File(baseDir, "src/test/java").getCanonicalPath());

		Reflects.forField().setDeclaredFieldValue(UpgradeMojo.class, "m_project", mojo, project);

		mojo.execute();
	}
}
