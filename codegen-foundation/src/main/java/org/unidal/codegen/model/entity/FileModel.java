/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.codegen.model.entity;

import org.unidal.codegen.model.BaseEntity;
import org.unidal.codegen.model.IVisitor;

public class FileModel extends BaseEntity<FileModel> {
   private String m_path;

   public FileModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitFile(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof FileModel) {
         FileModel _o = (FileModel) obj;

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
   public void mergeAttributes(FileModel other) {
      if (other.getPath() != null) {
         m_path = other.getPath();
      }
   }

   public FileModel setPath(String path) {
      m_path = path;
      return this;
   }

}
