package org.unidal.maven.plugin.project.plugin.entity;

import org.unidal.maven.plugin.project.plugin.BaseEntity;
import org.unidal.maven.plugin.project.plugin.IVisitor;

public class PluginMetadata extends BaseEntity<PluginMetadata> {
   private String m_groupId;

   private String m_artifactId;

   private Versioning m_versioning;

   public PluginMetadata() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitMetadata(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof PluginMetadata) {
         PluginMetadata _o = (PluginMetadata) obj;

         if (!equals(getGroupId(), _o.getGroupId())) {
            return false;
         }

         if (!equals(getArtifactId(), _o.getArtifactId())) {
            return false;
         }

         if (!equals(getVersioning(), _o.getVersioning())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getArtifactId() {
      return m_artifactId;
   }

   public String getGroupId() {
      return m_groupId;
   }

   public Versioning getVersioning() {
      return m_versioning;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_groupId == null ? 0 : m_groupId.hashCode());
      hash = hash * 31 + (m_artifactId == null ? 0 : m_artifactId.hashCode());
      hash = hash * 31 + (m_versioning == null ? 0 : m_versioning.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(PluginMetadata other) {
   }

   public PluginMetadata setArtifactId(String artifactId) {
      m_artifactId = artifactId;
      return this;
   }

   public PluginMetadata setGroupId(String groupId) {
      m_groupId = groupId;
      return this;
   }

   public PluginMetadata setVersioning(Versioning versioning) {
      m_versioning = versioning;
      return this;
   }

}
