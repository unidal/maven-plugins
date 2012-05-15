package com.site.maven.plugin.codegen;

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.site.codegen.generator.GenerateContext;
import com.site.codegen.generator.GenerateContextSupport;
import com.site.codegen.generator.Generator;

/**
 * DAL code generator for modeling
 * 
 * @goal dal-model
 * @phase generate-sources
 * @author Frankie Wu
 */
public class DalModelMojo extends AbstractMojo {
   /**
    * XSL code generator implementation
    * 
    * @component role="com.site.codegen.generator.Generator"
    *            role-hint="dal-model"
    * @required
    * @readonly
    */
   protected Generator m_generator;

   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Current project base directory
    * 
    * @parameter expression="${sourceDir}"
    *            default-value="target/generated-sources/dal-model"
    * @required
    */
   protected String sourceDir;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}" default-value=
    *            "${basedir}/src/main/resources/META-INF/dal/model/manifest.xml"
    * @required
    */
   protected String manifest;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}"
    *            default-value="/META-INF/dal/model"
    * @required
    */
   protected String resouceBase;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   protected boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   protected boolean debug;

   /**
    * Skip this codegen or not
    * 
    * @parameter expression="${codegen.skip}" default-value="false"
    */
   protected boolean skip;

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (skip) {
         getLog().info("Model codegen was skipped explicitly.");
         return;
      }

      try {
         File manifestFile = new File(manifest);

         if (!manifestFile.exists()) {
            throw new MojoExecutionException(String.format("Manifest(%s) not found!", manifestFile.getCanonicalPath()));
         }

         final URL manifestXml = manifestFile.toURI().toURL();
         final GenerateContext ctx = new GenerateContextSupport(resouceBase, m_project.getBasedir()) {
            @Override
            public URL getManifestXml() {
               return manifestXml;
            }

            @Override
            protected void configure(Map<String, String> properties) {
               properties.put("src-main-java", sourceDir);
            }

            @Override
            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().info(message);
                  }
                  break;
               case INFO:
                  if (debug || verbose) {
                     getLog().info(message);
                  }
                  break;
               case ERROR:
                  getLog().error(message);
                  break;
               }
            }
         };

         m_generator.generate(ctx);
         m_project.addCompileSourceRoot(sourceDir);
         getLog().info(ctx.getGeneratedFiles() + " files generated.");
      } catch (Exception e) {
         throw new MojoExecutionException("Code generating failed.", e);
      }
   }
}
