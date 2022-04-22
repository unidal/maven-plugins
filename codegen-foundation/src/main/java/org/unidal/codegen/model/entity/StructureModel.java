/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.codegen.model.entity;

import org.unidal.codegen.model.BaseEntity;
import org.unidal.codegen.model.IVisitor;

public class StructureModel extends BaseEntity<StructureModel> {
   private NodeModel m_node;

   public StructureModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitStructure(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof StructureModel) {
         StructureModel _o = (StructureModel) obj;

         if (!equals(getNode(), _o.getNode())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public NodeModel getNode() {
      return m_node;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_node == null ? 0 : m_node.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(StructureModel other) {
   }

   public StructureModel setNode(NodeModel node) {
      m_node = node;
      return this;
   }

}