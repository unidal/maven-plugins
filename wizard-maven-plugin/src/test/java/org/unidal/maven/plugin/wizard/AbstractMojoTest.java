package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.pom.AbstractWizardMojo;
import org.unidal.maven.plugin.pom.MavenContainer;

public abstract class AbstractMojoTest extends ComponentTestCase {
	@Test
	public void test() throws Exception {
		WebAppMojo mojo = new WebAppMojo();
		MavenProject project = new MavenProject();
		File pomFile = new File(getClass().getResource("pom-before.xml").getFile());
		File tmpFile = new File("target/pom-single.xml");

		Files.forDir().copyFile(pomFile, tmpFile);

		project.setFile(tmpFile);

		setField(mojo, AbstractWizardMojo.class, "m_project", project);
		setField(mojo, AbstractWizardMojo.class, "m_container", lookup(MavenContainer.class));
		setField(mojo, AbstractWizardMojo.class, "debug", true);
		setField(mojo, AbstractWizardMojo.class, "verbose", false);

		System.setProperty("war", "true");
		System.setProperty("module", "ui");
		System.setProperty("page", "home");
		System.setProperty("template", "default");

		mojo.execute();
	}

	private void setField(WebAppMojo mojo, Class<?> clazz, String field, Object value) {
		Reflects.forField().setDeclaredFieldValue(clazz, field, mojo, value);
	}
	
	
	protected static class MavenProjectBuilder {
	   private MavenProjectBuilder() {
	   }

      public static MavenProjectBuilder newBuilder() {
         return new MavenProjectBuilder();
      }

      public MavenProject build() {
         return null;
      }
	}
}
