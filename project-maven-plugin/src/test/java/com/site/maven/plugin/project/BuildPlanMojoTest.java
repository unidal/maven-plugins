package com.site.maven.plugin.project;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.site.maven.plugin.common.Injector;

public class BuildPlanMojoTest extends TestCase {
   public void testExecute() throws MojoExecutionException, MojoFailureException {
      BuildPlanMojo mojo = new BuildPlanMojo();
      File pom = new File("../../pom.xml");
      File buildPlan = new File("plan.xml");

      Injector.setField(mojo, "pomFile", pom);
      Injector.setField(mojo, "planFile", buildPlan);

      mojo.execute();
      assertTrue(buildPlan.exists());
      assertTrue(buildPlan.length() > 500);
   }
}
