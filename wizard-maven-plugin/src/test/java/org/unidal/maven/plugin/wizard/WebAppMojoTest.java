package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;
import org.unidal.maven.plugin.wizard.meta.WebAppWizardBuilder;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

public class WebAppMojoTest extends AbstractWizardMojoTest {
   @Test
   public void testMojo() throws Exception {
      WizardMojoSupport mojo = WizardMojoBuilder.builder(this, WebAppMojo.class) //
            .pom("webapp-pom.xml") //
            .component("m_wizardBuilder", WebAppWizardBuilder.class) //
            .component("m_pomBuilder", WebAppPomBuilder.class) //
            .component("m_generator", Generator.class, "wizard-webapp") //
            .build();
      File baseDir = mojo.getProject().getBasedir();

      System.setProperty("project.package", "org.unidal.webapp");
      System.setProperty("web.module", "true");
      System.setProperty("web.name", "cat");
      System.setProperty("web.template", "thymeleaf");
      System.setProperty("web.module", "ui");
      System.setProperty("web.page", "home");

      mojo.execute();

      Assert.assertEquals(true, new File(baseDir, "pom.xml").exists());
   }
}
