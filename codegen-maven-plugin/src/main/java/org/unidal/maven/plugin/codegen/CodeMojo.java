package org.unidal.maven.plugin.codegen;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.codegen.code.Obfuscater;
import org.unidal.maven.plugin.common.PropertyProviders;

/**
 * Encode a text.
 * 
 * @goal code
 * @requiresProject false
 * @author Frankie Wu
 */
public class CodeMojo extends AbstractMojo {
   /**
    * Code obfuscater.
    * 
    * @component
    * @required
    * @readonly
    */
   protected Obfuscater m_obfuscater;

   /**
    * Source text to encode
    * 
    * @parameter expression="${source}"
    */
   private String source;

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

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (source == null) {
         source = PropertyProviders.fromConsole().forString("source", "[INFO] Please enter text: ", null, null);
      }

      try {
         if (verbose || debug) {
            getLog().info("Source is " + source);
         }

         String target = m_obfuscater.encode(source);

         getLog().info("The result is:");
         getLog().info("~{" + target + "}");
      } catch (Exception e) {
         throw new MojoFailureException("Error when encoding text: " + source, e);
      }
   }
}
