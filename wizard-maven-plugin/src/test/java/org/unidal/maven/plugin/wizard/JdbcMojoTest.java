package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.unidal.codegen.meta.TableMeta;

import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class JdbcMojoTest extends ComponentTestCase {
   @Test
   public void testExecute() throws Exception {
      JdbcMojo mojo = new JdbcMojo();

      mojo.baseDir = new File("target");
      mojo.wizard = "target/wizard.xml";
      mojo.m_meta = lookup(TableMeta.class);
      mojo.outputDir = "target/wizard/";
      mojo.m_project = new MavenProject();
      
      mojo.m_project.setGroupId("com.dianping.cat");
      mojo.m_project.setArtifactId("cat-home");
      
      mojo.execute();
   }
}
