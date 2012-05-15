package com.site.maven.plugin.project;

import java.io.File;

import junit.framework.Assert;

import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.lookup.ComponentTestCase;
import com.site.maven.plugin.common.Injector;

@RunWith(JUnit4.class)
public class MigrateMojoTest extends ComponentTestCase {
   private MavenProject createMavenProject(String finalName, String path) throws Exception {
      File baseDir = new File(path).getCanonicalFile();
      MavenProject project = new MavenProject();

      project.setFile(new File(baseDir, finalName + ".jar"));
      project.getCompileSourceRoots().add(new File(baseDir, "src/main/java").toString());
      project.getTestCompileSourceRoots().add(new File(baseDir, "src/test/java").toString());
      project.getResources().add(createResource(new File(baseDir, "src/main/resources")));
      project.getTestResources().add(createResource(new File(baseDir, "src/test/resources")));

      return project;
   }

   protected Resource createResource(File directory) {
      Resource resource = new Resource();

      resource.setDirectory(directory.toString());
      return resource;
   }

   @Test
   @Ignore
   public void testMigrate() throws Exception {
      MigrateMojo mojo = new MigrateMojo();

      Injector.setField(mojo, "project", createMavenProject("eunit-testfwk", "../../common/eunit-testfwk"));
      Injector.setField(mojo, "sourcePackage", "com.ebay.eunit");
      Injector.setField(mojo, "targetPackage", "com.site.eunit");
      Injector.setField(mojo, "targetDir", "target/eunit-testfwk");
      Injector.setField(mojo, "verbose", false);

      mojo.execute();
   }

   @Test
   public void testReversePackage() {
      checkReverse("a.b", "b.a");
      checkReverse("a.b.c", "c.b.a");
      
      checkReverse("a.b.", ".b.a");
      checkReverse("a.b.c.", ".c.b.a");
   }

   private void checkReverse(String packageName, String reversedPackageName) {
      String actual = new MigrateMojo().reversePackage(packageName);

      Assert.assertEquals("Reversed package name incorrecct!", reversedPackageName, actual);
   }
}
