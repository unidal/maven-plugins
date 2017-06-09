package org.unidal.maven.plugin.project.model.entity;

import org.unidal.maven.plugin.project.model.BaseEntity;
import org.unidal.maven.plugin.project.model.IVisitor;

public class FieldModel extends BaseEntity<FieldModel> {
   private String m_name;

   private String m_signature;

   private String m_baselineSignature;

   public FieldModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitField(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof FieldModel) {
         FieldModel _o = (FieldModel) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         if (!equals(getSignature(), _o.getSignature())) {
            return false;
         }

         if (!equals(getBaselineSignature(), _o.getBaselineSignature())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public String getBaselineSignature() {
      return m_baselineSignature;
   }

   public String getName() {
      return m_name;
   }

   public String getSignature() {
      return m_signature;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());
      hash = hash * 31 + (m_signature == null ? 0 : m_signature.hashCode());
      hash = hash * 31 + (m_baselineSignature == null ? 0 : m_baselineSignature.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(FieldModel other) {
      if (other.getName() != null) {
         m_name = other.getName();
      }

      if (other.getSignature() != null) {
         m_signature = other.getSignature();
      }

      if (other.getBaselineSignature() != null) {
         m_baselineSignature = other.getBaselineSignature();
      }
   }

   public FieldModel setBaselineSignature(String baselineSignature) {
      m_baselineSignature = baselineSignature;
      return this;
   }

   public FieldModel setName(String name) {
      m_name = name;
      return this;
   }

   public FieldModel setSignature(String signature) {
      m_signature = signature;
      return this;
   }

}
