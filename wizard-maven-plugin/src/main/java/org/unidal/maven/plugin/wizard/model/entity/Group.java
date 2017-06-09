package org.unidal.maven.plugin.wizard.model.entity;

import static org.unidal.maven.plugin.wizard.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_GROUP;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Group extends BaseEntity<Group> {
   private String m_name;

   private String m_package;

   private List<Table> m_tables = new ArrayList<Table>();

   public Group() {
   }

   public Group(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitGroup(this);
   }

   public Group addTable(Table table) {
      m_tables.add(table);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Group) {
         Group _o = (Group) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public Table findTable(String name) {
      for (Table table : m_tables) {
         if (!equals(table.getName(), name)) {
            continue;
         }

         return table;
      }

      return null;
   }

   public Table findOrCreateTable(String name) {
      synchronized (m_tables) {
         for (Table table : m_tables) {
            if (!equals(table.getName(), name)) {
               continue;
            }

            return table;
         }

         Table table = new Table(name);

         m_tables.add(table);
         return table;
      }
   }

   public String getName() {
      return m_name;
   }

   public String getPackage() {
      return m_package;
   }

   public List<Table> getTables() {
      return m_tables;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Group other) {
      assertAttributeEquals(other, ENTITY_GROUP, ATTR_NAME, m_name, other.getName());

      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }
   }

   public Table removeTable(String name) {
      int len = m_tables.size();

      for (int i = 0; i < len; i++) {
         Table table = m_tables.get(i);

         if (!equals(table.getName(), name)) {
            continue;
         }

         return m_tables.remove(i);
      }

      return null;
   }

   public Group setName(String name) {
      m_name = name;
      return this;
   }

   public Group setPackage(String _package) {
      m_package = _package;
      return this;
   }

}
