package org.unidal.maven.plugin.wizard.model.entity;

import static org.unidal.maven.plugin.wizard.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_MODULE;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Module extends BaseEntity<Module> {
   private String m_name;

   private String m_path;

   private Boolean m_default;

   private String m_package;

   private List<Page> m_pages = new ArrayList<Page>();

   public Module() {
   }

   public Module(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitModule(this);
   }

   public Module addPage(Page page) {
      m_pages.add(page);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Module) {
         Module _o = (Module) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public Page findPage(String name) {
      for (Page page : m_pages) {
         if (!equals(page.getName(), name)) {
            continue;
         }

         return page;
      }

      return null;
   }

   public Boolean getDefault() {
      return m_default;
   }

   public String getName() {
      return m_name;
   }

   public String getPackage() {
      return m_package;
   }

   public List<Page> getPages() {
      return m_pages;
   }

   public String getPath() {
      return m_path;
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

   @Override
   public void mergeAttributes(Module other) {
      assertAttributeEquals(other, ENTITY_MODULE, ATTR_NAME, m_name, other.getName());

      if (other.getPath() != null) {
         m_path = other.getPath();
      }

      if (other.getDefault() != null) {
         m_default = other.getDefault();
      }

      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }
   }

   public Page removePage(String name) {
      int len = m_pages.size();

      for (int i = 0; i < len; i++) {
         Page page = m_pages.get(i);

         if (!equals(page.getName(), name)) {
            continue;
         }

         return m_pages.remove(i);
      }

      return null;
   }

   public Module setDefault(Boolean _default) {
      m_default = _default;
      return this;
   }

   public Module setName(String name) {
      m_name = name;
      return this;
   }

   public Module setPackage(String _package) {
      m_package = _package;
      return this;
   }

   public Module setPath(String path) {
      m_path = path;
      return this;
   }

}
