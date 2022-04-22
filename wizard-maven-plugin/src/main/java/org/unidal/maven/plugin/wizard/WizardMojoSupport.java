package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.unidal.codegen.framework.GenerationContext;
import org.unidal.codegen.framework.GenerationContextSupport;

public abstract class WizardMojoSupport extends AbstractMojo {
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

   public GenerationContext createContext(File manifestFile, String sourceDir) throws IOException {
      String resourceBase = String.format("/META-INF/wizard/%s/xsl", getCodegenType());

      return new WizardMojoGenerationContext(resourceBase, manifestFile, sourceDir);
   }

   protected abstract String getCodegenType();

   public MavenProject getProject() {
      return m_project;
   }

   protected class WizardMojoGenerationContext extends GenerationContextSupport {
      private final File m_manifestXml;

      private String m_resourceBasePath;

      private AtomicInteger m_generatedFiles = new AtomicInteger();

      public WizardMojoGenerationContext(String resourceBasePath, File manifestXml, String sourceDir) throws IOException {
         super(m_project.getBasedir());

         m_resourceBasePath = resourceBasePath;
         m_manifestXml = manifestXml;

         getProperties().put("src-main-java", sourceDir);
      }

      @Override
      public void debug(String message) {
         if (debug) {
            info(message);
         }
      }

      @Override
      public AtomicInteger getGeneratedFiles() {
         return m_generatedFiles;
      }

      public File getManifestXml() {
         return m_manifestXml;
      }

      @Override
      protected URL getResource(String name) {
         String path = m_resourceBasePath + "/" + name;
         URL url = getClass().getResource(path);

         if (url != null) {
            return url;
         } else {
            throw new RuntimeException("Can't find resource: " + path);
         }
      }

      @Override
      public void info(String message) {
         getLog().info(message);
      }

      @Override
      public void verbose(String message) {
         if (debug || verbose) {
            info(message);
         }
      }
   }
}
