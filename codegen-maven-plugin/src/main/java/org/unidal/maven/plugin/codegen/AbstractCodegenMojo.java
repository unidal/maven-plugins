package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.GenerateContextSupport;

public abstract class AbstractCodegenMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   private boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   private boolean debug;

   protected GenerateContext createContext(File manifestFile, String sourceDir) throws IOException {
      String resourceBase = String.format("/META-INF/dal/%s/xsl", getCodegenType());

      return new CodegenGenerateContext(resourceBase, m_project.getBasedir(), manifestFile, sourceDir);
   }

   protected abstract String getCodegenType();

   protected MavenProject getProject() {
      return m_project;
   }

   protected class CodegenGenerateContext extends GenerateContextSupport {
      private final File m_manifestXml;

      private CodegenGenerateContext(String resourceBasePath, File projectBaseDir, File manifestXml, String sourceDir)
            throws IOException {
         super(resourceBasePath, projectBaseDir);
         m_manifestXml = manifestXml;

         getProperties().put("src-main-java", sourceDir);
      }

      public URL getManifestXml() {
         try {
            return m_manifestXml.toURI().toURL();
         } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Invalid manifest file(%s)!", m_manifestXml), e);
         }
      }

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
   }
}
