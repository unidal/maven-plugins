package org.unidal.maven.plugin.wizard.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Wizard extends BaseEntity<Wizard> {
   private String m_package;

   private Webapp m_webapp;

   private List<Jdbc> m_jdbcs = new ArrayList<Jdbc>();

   private List<Model> m_models = new ArrayList<Model>();

   public Wizard() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitWizard(this);
   }

   public Wizard addJdbc(Jdbc jdbc) {
      m_jdbcs.add(jdbc);
      return this;
   }

   public Wizard addModel(Model model) {
      m_models.add(model);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Wizard) {
         Wizard _o = (Wizard) obj;

         if (!equals(getPackage(), _o.getPackage())) {
            return false;
         }

         if (!equals(getWebapp(), _o.getWebapp())) {
            return false;
         }

         if (!equals(getJdbcs(), _o.getJdbcs())) {
            return false;
         }

         if (!equals(getModels(), _o.getModels())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public Jdbc findJdbc(String name) {
      for (Jdbc jdbc : m_jdbcs) {
         if (!equals(jdbc.getName(), name)) {
            continue;
         }

         return jdbc;
      }

      return null;
   }

   public Model findModel(String name) {
      for (Model model : m_models) {
         if (!equals(model.getName(), name)) {
            continue;
         }

         return model;
      }

      return null;
   }

   public List<Jdbc> getJdbcs() {
      return m_jdbcs;
   }

   public List<Model> getModels() {
      return m_models;
   }

   public String getPackage() {
      return m_package;
   }

   public Webapp getWebapp() {
      return m_webapp;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_package == null ? 0 : m_package.hashCode());
      hash = hash * 31 + (m_webapp == null ? 0 : m_webapp.hashCode());
      for (Jdbc e : m_jdbcs) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }

      for (Model e : m_models) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(Wizard other) {
      if (other.getPackage() != null) {
         m_package = other.getPackage();
      }
   }

   public Jdbc removeJdbc(String name) {
      int len = m_jdbcs.size();

      for (int i = 0; i < len; i++) {
         Jdbc jdbc = m_jdbcs.get(i);

         if (!equals(jdbc.getName(), name)) {
            continue;
         }

         return m_jdbcs.remove(i);
      }

      return null;
   }

   public Model removeModel(String name) {
      int len = m_models.size();

      for (int i = 0; i < len; i++) {
         Model model = m_models.get(i);

         if (!equals(model.getName(), name)) {
            continue;
         }

         return m_models.remove(i);
      }

      return null;
   }

   public Wizard setPackage(String _package) {
      m_package = _package;
      return this;
   }

   public Wizard setWebapp(Webapp webapp) {
      m_webapp = webapp;
      return this;
   }

}
