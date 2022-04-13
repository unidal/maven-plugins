package org.unidal.maven.plugin.codegen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;

public class DalModelMojoTest extends AbstractCodegenMojoTest {
   @Test
   public void testMojo() throws Exception {
      AbstractCodegenMojo mojo = CodegenMojoBuilder.builder(this, DalModelMojo.class) //
            .pom("pom.xml") //
            .component("m_generator", Generator.class, "dal-model") //
            .build();
      File baseDir = mojo.getProject().getBasedir();

      setField(mojo, "sourceDir", new File(baseDir, "generated-sources").toString());
      setField(mojo, "manifest", getClass().getResource("model-manifest.xml").getPath());

      mojo.execute();

      Assert.assertEquals(true, new File(baseDir, "pom.xml").exists());
   }
}
