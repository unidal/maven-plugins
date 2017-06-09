package org.unidal.maven.plugin.project.group.entity;

import org.unidal.maven.plugin.project.group.BaseEntity;
import org.unidal.maven.plugin.project.group.IVisitor;

public class Plugin extends BaseEntity<Plugin> {
   private String m_name;

   private String m_prefix;

   private String m_artifactId;

   public Plugin() {
   }

   public Plugin(String artifactId) {
      m_artifactId = artifactId;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitPlugin(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Plugin) {
         Plugin _o = (Plugin) obj;

         if (!equals(getArtifactId(), _o.getArtifactId())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getArtifactId() {
      return m_artifactId;
   }

   public String getName() {
      return m_name;
   }

   public String getPrefix() {
      return m_prefix;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_artifactId == null ? 0 : m_artifactId.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Plugin other) {
   }

   public Plugin setArtifactId(String artifactId) {
      m_artifactId = artifactId;
      return this;
   }

   public Plugin setName(String name) {
      m_name = name;
      return this;
   }

   public Plugin setPrefix(String prefix) {
      m_prefix = prefix;
      return this;
   }

}
