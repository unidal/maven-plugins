/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.maven.plugin.wizard.model.entity;

import static org.unidal.maven.plugin.wizard.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_PAGE;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Page extends BaseEntity<Page> {
   private String m_name;

   private String m_path;

   private Boolean m_default;

   private String m_package;

   private String m_title;

   private Boolean m_standalone;

   private String m_view;

   private String m_description;

   public Page() {
   }

   public Page(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitPage(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Page) {
         Page _o = (Page) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public Boolean getDefault() {
      return m_default;
   }

   public String getDescription() {
      return m_description;
   }

   public String getName() {
      return m_name;
   }

   public String getPackage() {
      return m_package;
   }

   public String getPath() {
      return m_path;
   }

   public Boolean getStandalone() {
      return m_standalone;
   }

   public String getTitle() {
      return m_title;
   }

   public String getView() {
      return m_view;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());

      return hash;
   }

   public boolean isDefault() {
      return m_default != null && m_default.booleanValue();
   }

   public boolean isStandalone() {
      return m_standalone != null && m_standalone.booleanValue();
   }

   @Override
   public void mergeAttributes(Page other) {
      assertAttributeEquals(other, ENTITY_PAGE, ATTR_NAME, m_name, other.getName());

      if (other.getPath() != null) {
         m_path = other.getPath();
      }

      if (other.getDefault() != null) {
         m_default = other.getDefault();
      }

      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }

      if (other.getTitle() != null) {
         m_title = other.getTitle();
      }

      if (other.getStandalone() != null) {
         m_standalone = other.getStandalone();
      }

      if (other.getView() != null) {
         m_view = other.getView();
      }
   }

   public Page setDefault(Boolean _default) {
      m_default = _default;
      return this;
   }

   public Page setDescription(String description) {
      m_description = description;
      return this;
   }

   public Page setName(String name) {
      m_name = name;
      return this;
   }

   public Page setPackage(String _package) {
      m_package = _package;
      return this;
   }

   public Page setPath(String path) {
      m_path = path;
      return this;
   }

   public Page setStandalone(Boolean standalone) {
      m_standalone = standalone;
      return this;
   }

   public Page setTitle(String title) {
      m_title = title;
      return this;
   }

   public Page setView(String view) {
      m_view = view;
      return this;
   }

}
