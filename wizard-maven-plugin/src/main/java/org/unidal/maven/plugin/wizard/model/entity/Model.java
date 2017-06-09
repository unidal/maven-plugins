package org.unidal.maven.plugin.wizard.model.entity;

import static org.unidal.maven.plugin.wizard.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_MODEL;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Model extends BaseEntity<Model> {
   private String m_package;

   private String m_name;

   private String m_sampleModel;

   public Model() {
   }

   public Model(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitModel(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Model) {
         Model _o = (Model) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getName() {
      return m_name;
   }

   public String getPackage() {
      return m_package;
   }

   public String getSampleModel() {
      return m_sampleModel;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Model other) {
      assertAttributeEquals(other, ENTITY_MODEL, ATTR_NAME, m_name, other.getName());

      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }
   }

   public Model setName(String name) {
      m_name = name;
      return this;
   }

   public Model setPackage(String _package) {
      m_package = _package;
      return this;
   }

   public Model setSampleModel(String sampleModel) {
      m_sampleModel = sampleModel;
      return this;
   }

}
