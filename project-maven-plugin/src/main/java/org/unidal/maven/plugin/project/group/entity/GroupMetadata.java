package org.unidal.maven.plugin.project.group.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.project.group.BaseEntity;
import org.unidal.maven.plugin.project.group.IVisitor;

public class GroupMetadata extends BaseEntity<GroupMetadata> {
   private List<Plugin> m_plugins = new ArrayList<Plugin>();

   public GroupMetadata() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitMetadata(this);
   }

   public GroupMetadata addPlugin(Plugin plugin) {
      m_plugins.add(plugin);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof GroupMetadata) {
         GroupMetadata _o = (GroupMetadata) obj;

         if (!equals(getPlugins(), _o.getPlugins())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Plugin findPlugin(String artifactId) {
      for (Plugin plugin : m_plugins) {
         if (!equals(plugin.getArtifactId(), artifactId)) {
            continue;
         }

         return plugin;
      }

      return null;
   }

   public List<Plugin> getPlugins() {
      return m_plugins;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      for (Plugin e : m_plugins) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(GroupMetadata other) {
   }

   public Plugin removePlugin(String artifactId) {
      int len = m_plugins.size();

      for (int i = 0; i < len; i++) {
         Plugin plugin = m_plugins.get(i);

         if (!equals(plugin.getArtifactId(), artifactId)) {
            continue;
         }

         return m_plugins.remove(i);
      }

      return null;
   }

}
