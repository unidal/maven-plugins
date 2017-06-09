package org.unidal.maven.plugin.project.plugin.entity;

import org.unidal.maven.plugin.project.plugin.BaseEntity;
import org.unidal.maven.plugin.project.plugin.IVisitor;

public class Versioning extends BaseEntity<Versioning> {
   private String m_latest;

   private String m_release;

   private String m_lastUpdated;

   private Versions m_versions;

   public Versioning() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitVersioning(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Versioning) {
         Versioning _o = (Versioning) obj;

         if (!equals(getLatest(), _o.getLatest())) {
            return false;
         }

         if (!equals(getRelease(), _o.getRelease())) {
            return false;
         }

         if (!equals(getLastUpdated(), _o.getLastUpdated())) {
            return false;
         }

         if (!equals(getVersions(), _o.getVersions())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getLastUpdated() {
      return m_lastUpdated;
   }

   public String getLatest() {
      return m_latest;
   }

   public String getRelease() {
      return m_release;
   }

   public Versions getVersions() {
      return m_versions;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_latest == null ? 0 : m_latest.hashCode());
      hash = hash * 31 + (m_release == null ? 0 : m_release.hashCode());
      hash = hash * 31 + (m_lastUpdated == null ? 0 : m_lastUpdated.hashCode());
      hash = hash * 31 + (m_versions == null ? 0 : m_versions.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Versioning other) {
   }

   public Versioning setLastUpdated(String lastUpdated) {
      m_lastUpdated = lastUpdated;
      return this;
   }

   public Versioning setLatest(String latest) {
      m_latest = latest;
      return this;
   }

   public Versioning setRelease(String release) {
      m_release = release;
      return this;
   }

   public Versioning setVersions(Versions versions) {
      m_versions = versions;
      return this;
   }

}
