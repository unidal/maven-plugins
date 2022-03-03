package org.unidal.maven.plugin.codegen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;

public class DalJdbcMojoTest extends AbstractCodegenMojoTest {
   @Test
   public void testMojo() throws Exception {
      AbstractCodegenMojo mojo = CodegenMojoBuilder.builder(this, DalJdbcMojo.class) //
            .pom("jdbc-pom.xml") //
            .component("m_generator", Generator.class, "dal-jdbc") //
            .build();
      File baseDir = mojo.getProject().getBasedir();

      setField(mojo, "sourceDir", new File(baseDir, "generated-sources").toString());
      setField(mojo, "manifest", getClass().getResource("jdbc-manifest.xml").getPath());

      mojo.execute();
System.out.println(baseDir);
      Assert.assertEquals(true, new File(baseDir, "pom.xml").exists());
   }
}
