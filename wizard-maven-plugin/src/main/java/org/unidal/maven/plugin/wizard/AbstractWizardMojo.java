package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.GenerateContextSupport;

/**
 * Abstract mojo with container, for inheritance purpose
 */
public abstract class AbstractWizardMojo extends AbstractMojo {
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

   protected GenerateContext createContext() throws IOException {
      String resourceBase = String.format("/META-INF/wizard/%s/xsl", getWizardType());

      return new WizardGenerateContext(resourceBase, m_project.getBasedir(), getManifestFile());
   }

   protected File getManifestFile() {
      String manifestXml = String.format("src/main/resources/META-INF/wizard/manifest.xml");

      return new File(m_project.getBasedir(), manifestXml);
   }

   protected MavenProject getProject() {
      return m_project;
   }

   protected abstract String getWizardType();

   protected class WizardGenerateContext extends GenerateContextSupport {
      private final File m_manifestXml;

      private WizardGenerateContext(String resourceBasePath, File projectBaseDir, File manifestXml) throws IOException {
         super(resourceBasePath, projectBaseDir);
         m_manifestXml = manifestXml;
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
