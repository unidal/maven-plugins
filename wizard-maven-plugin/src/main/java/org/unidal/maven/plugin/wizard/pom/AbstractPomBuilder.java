package org.unidal.maven.plugin.wizard.pom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

abstract class AbstractPomBuilder implements LogEnabled {
   private DependencyManager m_manager = new DependencyManager();

   private Logger m_logger;

   public AbstractPomBuilder() {
      m_manager.configure("org.unidal.foundation", "foundation", "6.0.0", null);
      m_manager.configure("org.unidal.framework", "web-framework", "6.0.0", null);
      m_manager.configure("org.unidal.test", "test-framework", "6.0.0", "test");

      m_manager.configure("javax.servlet", "javax.servlet-api", "3.1.0", "provided");

      m_manager.configure("org.unidal.maven.plugins", "codegen-maven-plugin", "5.0.0", null);
      m_manager.configure("org.unidal.maven.plugins", "plexus-maven-plugin", "5.0.0", null);
   }

   protected Document loadPom(File pomFile) throws JDOMException, IOException {
      Document doc = new SAXBuilder().build(pomFile);

      return doc;
   }

   protected void savePom(File pomFile, Document doc) throws IOException {
      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(pomFile);

      try {
         outputter.output(doc, writer);

         m_logger.info(String.format("File(%s) updated.", pomFile.getCanonicalPath()));
      } finally {
         writer.close();
      }
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   private static class Dependency {
      private String m_groupId;

      private String m_artifactId;

      private String m_version;

      private String m_scope;

      public Dependency(String groupId, String artifactId) {
         m_groupId = groupId;
         m_artifactId = artifactId;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Dependency) {
            Dependency d = (Dependency) obj;

            return m_artifactId.equals(d.m_artifactId) && m_groupId.equals(d.m_groupId);
         }

         return false;
      }

      @Override
      public int hashCode() {
         int hash = 1;

         hash = 31 * hash + m_groupId.hashCode();
         hash = 31 * hash + m_artifactId.hashCode();
         return hash;
      }

      public Dependency scope(String scope) {
         m_scope = scope;
         return this;
      }

      @Override
      public String toString() {
         return "Dependency[groupId=" + m_groupId + ", artifactId=" + m_artifactId + ", version=" + m_version
               + ", scope=" + m_scope + "]";
      }

      public Dependency version(String version) {
         m_version = version;
         return this;
      }
   }

   private static class DependencyManager {
      private Map<Dependency, Dependency> m_dependencies = new HashMap<Dependency, Dependency>();

      public void configure(String groupId, String artifactId, String version, String scope) {
         Dependency d = new Dependency(groupId, artifactId).version(version).scope(scope);

         m_dependencies.put(d, d);
      }

   }
}
