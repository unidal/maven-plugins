package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.meta.ModelMeta;
import org.unidal.maven.plugin.wizard.meta.ModelWizardBuilder;
import org.unidal.maven.plugin.wizard.pom.ModelPomBuilder;

public class ModelMojoTest extends AbstractWizardMojoTest {
   @Test
   public void testMojo() throws Exception {
      AbstractWizardMojo mojo = WizardMojoBuilder.builder(this, ModelMojo.class) //
            .pom("model-pom.xml") //
            .component("m_wizardBuilder", ModelWizardBuilder.class) //
            .component("m_pomBuilder", ModelPomBuilder.class) //
            .component("m_modelMeta", ModelMeta.class) //
            .build();
      File baseDir = mojo.getProject().getBasedir();

      System.setProperty("project.package", "org.unidal.model");
      System.setProperty("model.sample", getClass().getResource("model-sample.xml").getPath());
      System.setProperty("model.package", "org.unidal.demo");
      System.setProperty("model.name", "demo");
      setField(mojo, "outputDir", new File(baseDir, "src/main/resources/META-INF/dal/model").toString());
      
      mojo.execute();

      Assert.assertEquals(true, new File(baseDir, "pom.xml").exists());
   }
}
