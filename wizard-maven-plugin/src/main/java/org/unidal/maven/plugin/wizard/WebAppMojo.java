package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.Generator;
import org.unidal.maven.plugin.pom.AbstractWizardMojo;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.webapp.WebAppPomBuilder;
import org.unidal.maven.plugin.wizard.webapp.WebAppWizardBuilder;

/**
 * Create web application project, modules and pages.
 * 
 * @goal webapp
 */
public class WebAppMojo extends AbstractWizardMojo {

   public void execute() throws MojoExecutionException, MojoFailureException {
      MavenProject project = getProject();

      if (project.getPackaging().equals("pom")) {
         throw new MojoFailureException("This wizard can not be run against POM project!");
      }

      try {
         WebAppWizardBuilder wizardBuilder = lookup(WebAppWizardBuilder.class);
         WebAppPomBuilder pomBuilder = lookup(WebAppPomBuilder.class);

         File manifestFile = getManifestFile();
         Wizard wizard = wizardBuilder.build(project, manifestFile);

         Generator generator = lookup(Generator.class, "wizard-webapp");
         GenerateContext ctx = createGenerateContext();

         generator.generate(createGenerateContext());

         getLog().info(ctx.getGeneratedFiles() + " files generated.");

         pomBuilder.build(project.getFile(), wizard);
         pomBuilder.save();
      } catch (Exception e) {
         e.printStackTrace();
         throw new MojoExecutionException("Error when generating code for webapp.", e);
      }
   }

   @Override
   protected String getWizardType() {
      return "webapp";
   }
}
