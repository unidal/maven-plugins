package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.Generator;
import org.unidal.helper.Splitters;

/**
 * Generates model files in Java language.
 * 
 * @goal dal-model
 * @phase generate-sources
 * @author Frankie Wu
 */
public class DalModelMojo extends AbstractCodegenMojo {
   /**
    * XSL code generator implementation
    * 
    * @component role="org.unidal.codegen.generator.Generator" role-hint="dal-model"
    * @required
    * @readonly
    */
   private Generator m_generator;

   /**
    * Current project base directory
    * 
    * @parameter expression="${sourceDir}" default-value="${basedir}/target/generated-sources/dal-model"
    * @required
    */
   private String sourceDir;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}"
    * @required
    */
   private String manifest;

   /**
    * Skip this codegen or not
    * 
    * @parameter expression="${codegen.skip}" default-value="false"
    */
   private boolean skip;

   /**
    * for test or not
    * 
    * @parameter default-value="false"
    */
   private boolean test;

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (skip) {
         getLog().info("DAL model code generation is skipped explicitly.");
         return;
      }

      List<String> files = Splitters.by(',').noEmptyItem().trim().split(manifest);

      try {
         for (String file : files) {
            generateModel(file);
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating code due to " + e, e);
      }
   }

   private void generateModel(String manifest)
         throws MojoExecutionException, IOException, MalformedURLException, Exception {
      File manifestFile = new File(manifest);

      if (!manifestFile.exists()) {
         throw new MojoExecutionException(String.format("Manifest(%s) is not found!", manifestFile.getCanonicalPath()));
      }

      final GenerateContext ctx = super.createContext(manifestFile, sourceDir);

      m_generator.generate(ctx);

      getLog().info(ctx.getGeneratedFiles() + " files generated.");

      if (test) {
         getProject().addTestCompileSourceRoot(sourceDir);
      } else {
         getProject().addCompileSourceRoot(sourceDir);
      }
   }

   @Override
   protected String getCodegenType() {
      return "model";
   }
}
