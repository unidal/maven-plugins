package com.site.maven.plugin.codegen;

import java.io.File;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.site.codegen.generator.AbstractGenerateContext;
import com.site.codegen.generator.GenerateContext;
import com.site.codegen.generator.Generator;

/**
 * DAL code generator for JDBC
 * 
 * @goal dal-jdbc
 * @author Frankie Wu
 */
public class DalJdbcMojo extends AbstractMojo {

   /**
    * XSL code generator implementation
    * 
    * @component role="com.site.codegen.generator.Generator"
    *            role-hint="dal-jdbc"
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
    * @parameter expression="${sourceOutput}"
    *            default-value="${basedir}/target/generated-sources/dal-jdbc"
    * @required
    */
   protected String sourceDir;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}" default-value=
    *            "${basedir}/src/main/resources/META-INF/dal/jdbc/manifest.xml"
    * @required
    */
   protected String manifest;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}"
    *            default-value="/META-INF/dal/jdbc"
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
         final GenerateContext ctx = new AbstractGenerateContext(m_project.getBasedir(), resouceBase, sourceDir) {
            public URL getManifestXml() {
               return manifestXml;
            }

            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().debug(message);
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
