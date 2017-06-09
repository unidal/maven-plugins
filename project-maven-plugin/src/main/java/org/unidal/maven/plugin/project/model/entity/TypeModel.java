package org.unidal.maven.plugin.project.model.entity;

import static org.unidal.maven.plugin.project.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.project.model.Constants.ENTITY_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.project.model.BaseEntity;
import org.unidal.maven.plugin.project.model.IVisitor;

public class TypeModel extends BaseEntity<TypeModel> {
   private String m_name;

   private String m_signature;

   private String m_baselineSignature;

   private List<FieldModel> m_fields = new ArrayList<FieldModel>();

   private List<ConstructorModel> m_constructors = new ArrayList<ConstructorModel>();

   private List<MethodModel> m_methods = new ArrayList<MethodModel>();

   public TypeModel() {
   }

   public TypeModel(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitType(this);
   }

   public TypeModel addConstructor(ConstructorModel constructor) {
      m_constructors.add(constructor);
      return this;
   }

   public TypeModel addField(FieldModel field) {
      m_fields.add(field);
      return this;
   }

   public TypeModel addMethod(MethodModel method) {
      m_methods.add(method);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TypeModel) {
         TypeModel _o = (TypeModel) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getBaselineSignature() {
      return m_baselineSignature;
   }

   public List<ConstructorModel> getConstructors() {
      return m_constructors;
   }

   public List<FieldModel> getFields() {
      return m_fields;
   }

   public List<MethodModel> getMethods() {
      return m_methods;
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

      return hash;
   }

   @Override
   public void mergeAttributes(TypeModel other) {
      assertAttributeEquals(other, ENTITY_TYPE, ATTR_NAME, m_name, other.getName());

      if (other.getSignature() != null) {
         m_signature = other.getSignature();
      }

      if (other.getBaselineSignature() != null) {
         m_baselineSignature = other.getBaselineSignature();
      }
   }

   public TypeModel setBaselineSignature(String baselineSignature) {
      m_baselineSignature = baselineSignature;
      return this;
   }

   public TypeModel setName(String name) {
      m_name = name;
      return this;
   }

   public TypeModel setSignature(String signature) {
      m_signature = signature;
      return this;
   }

}
