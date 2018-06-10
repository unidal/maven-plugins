package org.unidal.maven.plugin.wizard.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Webapp extends BaseEntity<Webapp> {
   private String m_package;

   private String m_name;

   private Boolean m_module;

   private Boolean m_webres;

   private Boolean m_cat;

   private Boolean m_pluginManagement;

   private Boolean m_jstl;

   private String m_layout;

   private List<Module> m_modules = new ArrayList<Module>();

   public Webapp() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitWebapp(this);
   }

   public Webapp addModule(Module module) {
      m_modules.add(module);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Webapp) {
         Webapp _o = (Webapp) obj;

         if (!equals(getPackage(), _o.getPackage())) {
            return false;
         }

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         if (!equals(getModule(), _o.getModule())) {
            return false;
         }

         if (!equals(getWebres(), _o.getWebres())) {
            return false;
         }

         if (!equals(getCat(), _o.getCat())) {
            return false;
         }

         if (!equals(getPluginManagement(), _o.getPluginManagement())) {
            return false;
         }

         if (!equals(getJstl(), _o.getJstl())) {
            return false;
         }

         if (!equals(getLayout(), _o.getLayout())) {
            return false;
         }

         if (!equals(getModules(), _o.getModules())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Module findModule(String name) {
      for (Module module : m_modules) {
         if (!equals(module.getName(), name)) {
            continue;
         }

         return module;
      }

      return null;
   }

   public Boolean getCat() {
      return m_cat;
   }

   public Boolean getJstl() {
      return m_jstl;
   }

   public String getLayout() {
      return m_layout;
   }

   public Boolean getModule() {
      return m_module;
   }

   public List<Module> getModules() {
      return m_modules;
   }

   public String getName() {
      return m_name;
   }

   public String getPackage() {
      return m_package;
   }

   public Boolean getPluginManagement() {
      return m_pluginManagement;
   }

   public Boolean getWebres() {
      return m_webres;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_package == null ? 0 : m_package.hashCode());
      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());
      hash = hash * 31 + (m_module == null ? 0 : m_module.hashCode());
      hash = hash * 31 + (m_webres == null ? 0 : m_webres.hashCode());
      hash = hash * 31 + (m_cat == null ? 0 : m_cat.hashCode());
      hash = hash * 31 + (m_pluginManagement == null ? 0 : m_pluginManagement.hashCode());
      hash = hash * 31 + (m_jstl == null ? 0 : m_jstl.hashCode());
      hash = hash * 31 + (m_layout == null ? 0 : m_layout.hashCode());
      for (Module e : m_modules) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   public boolean isCat() {
      return m_cat != null && m_cat.booleanValue();
   }

   public boolean isJstl() {
      return m_jstl != null && m_jstl.booleanValue();
   }

   public boolean isModule() {
      return m_module != null && m_module.booleanValue();
   }

   public boolean isPluginManagement() {
      return m_pluginManagement != null && m_pluginManagement.booleanValue();
   }

   public boolean isWebres() {
      return m_webres != null && m_webres.booleanValue();
   }

   @Override
   public void mergeAttributes(Webapp other) {
      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }

      if (other.getName() != null) {
         m_name = other.getName();
      }

      if (other.getModule() != null) {
         m_module = other.getModule();
      }

      if (other.getWebres() != null) {
         m_webres = other.getWebres();
      }

      if (other.getCat() != null) {
         m_cat = other.getCat();
      }

      if (other.getPluginManagement() != null) {
         m_pluginManagement = other.getPluginManagement();
      }

      if (other.getJstl() != null) {
         m_jstl = other.getJstl();
      }

      if (other.getLayout() != null) {
         m_layout = other.getLayout();
      }
   }

   public Module removeModule(String name) {
      int len = m_modules.size();

      for (int i = 0; i < len; i++) {
         Module module = m_modules.get(i);

         if (!equals(module.getName(), name)) {
            continue;
         }

         return m_modules.remove(i);
      }

      return null;
   }

   public Webapp setCat(Boolean cat) {
      m_cat = cat;
      return this;
   }

   public Webapp setJstl(Boolean jstl) {
      m_jstl = jstl;
      return this;
   }

   public Webapp setLayout(String layout) {
      m_layout = layout;
      return this;
   }

   public Webapp setModule(Boolean module) {
      m_module = module;
      return this;
   }

   public Webapp setName(String name) {
      m_name = name;
      return this;
   }

   public Webapp setPackage(String _package) {
      m_package = _package;
      return this;
   }

   public Webapp setPluginManagement(Boolean pluginManagement) {
      m_pluginManagement = pluginManagement;
      return this;
   }

   public Webapp setWebres(Boolean webres) {
      m_webres = webres;
      return this;
   }

}
