package org.unidal.maven.plugin.wizard;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.generator.Generator;
import org.unidal.maven.plugin.pom.AbstractWizardMojo;
import org.unidal.maven.plugin.wizard.meta.WebAppWizardBuilder;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

/**
 * Create web application project, modules and pages.
 * 
 * @goal webapp
 */
public class WebAppMojo extends AbstractWizardMojo {
   /**
    * Wizard builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private WebAppWizardBuilder m_wizardBuilder;

   /**
    * POM builder component
    * 
    * @component
    * @required
    * @readonly
    */
   private WebAppPomBuilder m_pomBuilder;

   /**
    * POM builder component
    * 
    * @component role="org.unidal.codegen.generator.Generator" role-hint="wizard-webapp"
    * @required
    * @readonly
    */
   private Generator m_generator;

   public void execute() throws MojoExecutionException, MojoFailureException {
      MavenProject project = getProject();

      if (project.getPackaging().equals("pom")) {
         throw new MojoFailureException("This wizard can not be run against POM project!");
      }

      try {
         // prepare the wizard.xml and the manifest.xml
         Wizard wizard = m_wizardBuilder.build(project);

         // generate web scaffold files
         m_generator.generate(createGenerateContext());

         // modify the pom.xml
         m_pomBuilder.build(project.getFile(), wizard);
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
