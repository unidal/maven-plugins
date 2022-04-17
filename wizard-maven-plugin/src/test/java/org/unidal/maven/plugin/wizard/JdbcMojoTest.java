package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;
import org.unidal.maven.plugin.wizard.meta.JdbcWizardBuilder;
import org.unidal.maven.plugin.wizard.meta.TableMeta;
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

      System.setProperty("project.package", "org.unidal.jdbc");
      System.setProperty("datasource", "test");
      System.setProperty("jdbc.package", "org.unidal.jdbc.dal");
      System.setProperty("driver", "com.mysql.cj.jdbc.Driver");
      System.setProperty("url", "jdbc:mysql://localhost:3306/test");
      System.setProperty("user", "user");
      System.setProperty("password", "password");
      System.setProperty("properties", "useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=true");
      System.setProperty("group", "group1");
      System.setProperty("table", "table1");
      setField(mojo, "outputDir", new File(baseDir, "src/main/resources/META-INF/dal/jdbc").toString());
      
      mojo.execute();

      Assert.assertEquals(true, new File(baseDir, "pom.xml").exists());
   }
}
