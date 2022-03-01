package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.meta.TableMeta;
import org.unidal.maven.plugin.wizard.meta.JdbcWizardBuilder;
import org.unidal.maven.plugin.wizard.pom.JdbcPomBuilder;

public class JdbcMojoTest extends AbstractWizardMojoTest {
   @Test
   public void testMojo() throws Exception {
      AbstractWizardMojo mojo = WizardMojoBuilder.builder(this, JdbcMojo.class) //
            .pom("jdbc-pom.xml") //
            .component("m_wizardBuilder", JdbcWizardBuilder.class) //
            .component("m_pomBuilder", JdbcPomBuilder.class) //
            .component("m_tableMeta", TableMeta.class) //
            .component("m_generator", Generator.class, "wizard-jdbc") //
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
