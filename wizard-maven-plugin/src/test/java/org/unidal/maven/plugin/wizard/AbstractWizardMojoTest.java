package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.lang.reflect.Field;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.PlexusContainer;

public abstract class AbstractWizardMojoTest extends ComponentTestCase {
   protected static void setField(Object instance, String fieldName, Object value) throws Exception {
      Field field = Reflects.forField().getDeclaredField(instance, fieldName);

      field.setAccessible(true);
      field.set(instance, value);
   }

   protected static class WizardMojoBuilder {
      private PlexusContainer m_container;

      private WizardMojoSupport m_mojo;

      private WizardMojoBuilder() {
      }

      public static WizardMojoBuilder builder() {
         return new WizardMojoBuilder();
      }

      public static WizardMojoBuilder builder(AbstractWizardMojoTest test,
            Class<? extends WizardMojoSupport> mojoClass) throws Exception {
         WizardMojoBuilder builder = new WizardMojoBuilder();

         builder.initialize(test.getContainer(), mojoClass);
         return builder;
      }

      public WizardMojoSupport build() throws Exception {
         return m_mojo;
      }

      public WizardMojoBuilder component(String fieldName, Class<?> role) throws Exception {
         return component(fieldName, role, null);
      }

      public WizardMojoBuilder component(String fieldName, Class<?> role, String roleHint) throws Exception {
         Object component = m_container.lookup(role, roleHint);

         setField(m_mojo, fieldName, component);

         if (component instanceof LogEnabled) {
            ((LogEnabled) component).enableLogging(new ConsoleLogger());
         }

         return this;
      }

      private void initialize(PlexusContainer container, Class<? extends WizardMojoSupport> mojoClass)
            throws Exception {
         m_container = container;
         m_mojo = mojoClass.getConstructor().newInstance();
         m_mojo.setLog(new SystemStreamLog());

         MavenProject project = new MavenProject();

         project.setGroupId("group.id");
         project.setArtifactId("artifact-id");
         project.setVersion("1.0.0");
         project.setPackaging("jar");
         setField(m_mojo, "m_project", project);
      }

      public <T extends WizardMojoSupport> WizardMojoBuilder mojo(Class<T> mojoClass) throws Exception {
         m_mojo = mojoClass.getConstructor().newInstance();
         return this;
      }

      public WizardMojoBuilder pom(String pom) throws Exception {
         MavenProject project = m_mojo.getProject();
         String tmpDir = System.getProperty("java.io.tmpdir");
         File baseDir = new File(tmpDir, "org.unidal.maven.plugin.wizard/" + m_mojo.getWizardType());

         if (baseDir.isDirectory()) {
            Files.forDir().delete(baseDir, true);
         }

         File pomFile = new File(baseDir, "pom.xml");
         byte[] data = Files.forIO().readFrom(getClass().getResourceAsStream(pom));

         Files.forIO().writeTo(pomFile, data);
         project.setFile(pomFile);

         return this;
      }
   }
}
