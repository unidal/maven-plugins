package org.unidal.maven.plugin.wizard.pom;

import java.util.HashMap;
import java.util.Map;

import org.unidal.maven.plugin.wizard.pom.PomDelegate.VersionMapping;

public class VersionManager implements VersionMapping {
   private Map<Entry, Entry> m_entries = new HashMap<Entry, Entry>();

   public VersionManager() {
      configure("org.unidal.foundation", "foundation", "6.0.0", null);
      configure("org.unidal.framework", "web-framework", "6.0.0", null);
      configure("org.unidal.test", "test-framework", "6.0.0", "test");

      configure("javax.servlet", "javax.servlet-api", "3.1.0", "provided");
      configure("mysql", "mysql-connector-java", "8.0.28", "runtime");

      configure("org.unidal.maven.plugins", "codegen-maven-plugin", "6.0.0", null);
      configure("org.unidal.maven.plugins", "plexus-maven-plugin", "6.0.0", null);
   }

   public void configure(String groupId, String artifactId, String version, String scope) {
      Entry e = new Entry(groupId, artifactId).version(version).scope(scope);

      m_entries.put(e, e);
   }

   @Override
   public String getVersion(String groupId, String artifactId) {
      Entry e = m_entries.get(new Entry(groupId, artifactId));

      if (e != null) {
         return e.m_version;
      } else {
         return null;
      }
   }

   private static class Entry {
      private String m_groupId;

      private String m_artifactId;

      private String m_version;

      private String m_scope;

      public Entry(String groupId, String artifactId) {
         m_groupId = groupId;
         m_artifactId = artifactId;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof Entry) {
            Entry d = (Entry) obj;

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

      public Entry scope(String scope) {
         m_scope = scope;
         return this;
      }

      @Override
      public String toString() {
         return "Dependency[groupId=" + m_groupId + ", artifactId=" + m_artifactId + ", version=" + m_version
               + ", scope=" + m_scope + "]";
      }

      public Entry version(String version) {
         m_version = version;
         return this;
      }
   }
}