/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.codegen.model.entity;

import static org.unidal.codegen.model.Constants.ATTR_NAME;
import static org.unidal.codegen.model.Constants.ENTITY_NODE;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.model.BaseEntity;
import org.unidal.codegen.model.IVisitor;

public class NodeModel extends BaseEntity<NodeModel> {
   private String m_name;

   private Boolean m_noNamespace;

   private String m_key;

   private List<NodeModel> m_nodes = new ArrayList<NodeModel>();

   public NodeModel() {
   }

   public NodeModel(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitNode(this);
   }

   public NodeModel addNode(NodeModel node) {
      m_nodes.add(node);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof NodeModel) {
         NodeModel _o = (NodeModel) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public NodeModel findNode(String name) {
      for (NodeModel node : m_nodes) {
         if (!equals(node.getName(), name)) {
            continue;
         }

         return node;
      }

      return null;
   }

   public String getKey() {
      return m_key;
   }

   public String getName() {
      return m_name;
   }

   public List<NodeModel> getNodes() {
      return m_nodes;
   }

   public Boolean getNoNamespace() {
      return m_noNamespace;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());

      return hash;
   }

   public boolean isNoNamespace() {
      return m_noNamespace != null && m_noNamespace.booleanValue();
   }

   @Override
   public void mergeAttributes(NodeModel other) {
      assertAttributeEquals(other, ENTITY_NODE, ATTR_NAME, m_name, other.getName());

      if (other.getNoNamespace() != null) {
         m_noNamespace = other.getNoNamespace();
      }

      if (other.getKey() != null) {
         m_key = other.getKey();
      }
   }

   public NodeModel removeNode(String name) {
      int len = m_nodes.size();

      for (int i = 0; i < len; i++) {
         NodeModel node = m_nodes.get(i);

         if (!equals(node.getName(), name)) {
            continue;
         }

         return m_nodes.remove(i);
      }

      return null;
   }

   public NodeModel setKey(String key) {
      m_key = key;
      return this;
   }

   public NodeModel setName(String name) {
      m_name = name;
      return this;
   }

   public NodeModel setNoNamespace(Boolean noNamespace) {
      m_noNamespace = noNamespace;
      return this;
   }

}
