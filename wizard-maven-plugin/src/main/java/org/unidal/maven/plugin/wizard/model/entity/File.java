/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.maven.plugin.wizard.model.entity;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class File extends BaseEntity<File> {
   private String m_path;

   public File() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitFile(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof File) {
         File _o = (File) obj;

         if (!equals(getPath(), _o.getPath())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getPath() {
      return m_path;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_path == null ? 0 : m_path.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(File other) {
      if (other.getPath() != null) {
         m_path = other.getPath();
      }
   }

   public File setPath(String path) {
      m_path = path;
      return this;
   }

}
