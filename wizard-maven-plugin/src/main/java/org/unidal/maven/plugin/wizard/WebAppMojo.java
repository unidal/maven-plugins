package org.unidal.maven.plugin.wizard;

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.framework.GenerationContext;
import org.unidal.codegen.framework.XslGenerator;
import org.unidal.maven.plugin.wizard.meta.WebAppWizardBuilder;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

/**
 * Create web application project, modules and pages.
 * 
 * @goal webapp
 */
public class WebAppMojo extends WizardMojoSupport {
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
    * XSL code generator implementation
    * 
    * @component role="org.unidal.codegen.framework.XslGenerator"
    * @required
    * @readonly
    */
   private XslGenerator m_generator;

   /**
    * Current project base directory
    * 
    * @parameter expression="${sourceDir}" default-value="${basedir}/src/main/java"
    * @required
    */
   private String sourceDir;

   /**
    * Current project base directory
    * 
    * @parameter expression="${manifest}" default-value="${basedir}/src/main/resources/META-INF/wizard/webapp/wizard.xml"
    * @required
    */
   private String manifest;

   public void execute() throws MojoExecutionException, MojoFailureException {
      MavenProject project = getProject();

      if (project.getPackaging().equals("pom")) {
         throw new MojoFailureException("This wizard can not be run against POM project!");
      }

      try {
         // prepare the wizard.xml and the manifest.xml
         Wizard wizard = m_wizardBuilder.build(project);

         // generate web scaffold files
         generate(new File(manifest), sourceDir);

         // modify the pom.xml
         m_pomBuilder.build(project.getFile(), wizard);
      } catch (Exception e) {
         e.printStackTrace();
         throw new MojoExecutionException("Error when generating code for webapp.", e);
      }
   }

   public void generate(File manifest, String sourceDir) throws Exception {
      GenerationContext ctx = super.createContext(manifest, sourceDir);

      m_generator.generate(ctx);
   }

   @Override
   protected String getCodegenType() {
      return "webapp";
   }
}
