package org.unidal.maven.plugin.project.plugin.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.project.plugin.BaseEntity;
import org.unidal.maven.plugin.project.plugin.IVisitor;

public class Versions extends BaseEntity<Versions> {
   private List<String> m_versions = new ArrayList<String>();

   public Versions() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitVersions(this);
   }

   public Versions addVersion(String version) {
      m_versions.add(version);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Versions) {
         Versions _o = (Versions) obj;

         if (!equals(getVersions(), _o.getVersions())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public List<String> getVersions() {
      return m_versions;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      for (String e : m_versions) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Versions other) {
   }

}
