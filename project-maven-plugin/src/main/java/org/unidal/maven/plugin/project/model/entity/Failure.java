package org.unidal.maven.plugin.project.model.entity;

import static org.unidal.maven.plugin.project.model.Constants.ATTR_TYPE;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_FAILURE;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.project.model.BaseEntity;
import org.unidal.maven.plugin.project.model.IVisitor;

public class Failure extends BaseEntity<Failure> {
   private String m_type;

   private List<TypeModel> m_types = new ArrayList<TypeModel>();

   public Failure() {
   }

   public Failure(String type) {
      m_type = type;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitFailure(this);
   }

   public Failure addType(TypeModel type) {
      m_types.add(type);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Failure) {
         Failure _o = (Failure) obj;

         if (!equals(getType(), _o.getType())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public TypeModel findType(String name) {
      for (TypeModel type : m_types) {
         if (!equals(type.getName(), name)) {
            continue;
         }

         return type;
      }

      return null;
   }

   public String getType() {
      return m_type;
   }

   public List<TypeModel> getTypes() {
      return m_types;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_type == null ? 0 : m_type.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Failure other) {
      assertAttributeEquals(other, ENTITY_FAILURE, ATTR_TYPE, m_type, other.getType());

   }

   public TypeModel removeType(String name) {
      int len = m_types.size();

      for (int i = 0; i < len; i++) {
         TypeModel type = m_types.get(i);

         if (!equals(type.getName(), name)) {
            continue;
         }

         return m_types.remove(i);
      }

      return null;
   }

   public Failure setType(String type) {
      m_type = type;
      return this;
   }

}
