package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.common.Injector;
import org.unidal.maven.plugin.pom.MavenContainer;

public class WebAppMojoTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      WebAppMojo mojo = new WebAppMojo();
      MavenProject project = new MavenProject();
      File baseDir = new File("target/generated-sources/wizard-webapp").getCanonicalFile();
      File pomFile = new File(getClass().getResource("webapp/pom-before.xml").getFile());
      File tmpFile = new File("target/pom-single.xml");

      Files.forDir().copyFile(pomFile, tmpFile);

      project.setFile(tmpFile);

      Injector.setField(mojo, "m_project", project);
      Injector.setField(mojo, "m_container", lookup(MavenContainer.class));
      Injector.setField(mojo, "m_wizardMeta", lookup(WizardMeta.class));
      Injector.setField(mojo, "baseDir", baseDir);
      Injector.setField(mojo, "debug", true);
      Injector.setField(mojo, "verbose", false);

      System.setProperty("war", "true");
      System.setProperty("module", "ui");
      System.setProperty("page", "home");
      System.setProperty("template", "default");

      mojo.execute();
   }
}
