package org.unidal.maven.plugin.source;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.unidal.helper.Reflects;

public class LinesMojoTest {
	@Test
	public void testMojo() throws Exception {
		LinesMojo mojo = new LinesMojo();
		MavenProject project = new MavenProject();

		project.setName("mock-project");
		project.getCompileSourceRoots().add(new File("src/main/java").getCanonicalPath());
		project.getTestCompileSourceRoots().add(new File("src/test/java").getCanonicalPath());

		Reflects.forField().setDeclaredFieldValue(LinesMojo.class, "m_project", mojo, project);

		mojo.execute();
	}
}
