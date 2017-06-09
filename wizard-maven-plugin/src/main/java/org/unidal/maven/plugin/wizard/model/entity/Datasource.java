package org.unidal.maven.plugin.wizard.model.entity;

import static org.unidal.maven.plugin.wizard.model.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_DATASOURCE;

import org.unidal.maven.plugin.wizard.model.BaseEntity;
import org.unidal.maven.plugin.wizard.model.IVisitor;

public class Datasource extends BaseEntity<Datasource> {
   private String m_driver;

   private String m_url;

   private String m_user;

   private String m_password;

   private String m_properties;

   private String m_name;

   public Datasource() {
   }

   public Datasource(String name) {
      m_name = name;
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitDatasource(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Datasource) {
         Datasource _o = (Datasource) obj;

         if (!equals(getName(), _o.getName())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public String getDriver() {
      return m_driver;
   }

   public String getName() {
      return m_name;
   }

   public String getPassword() {
      return m_password;
   }

   public String getProperties() {
      return m_properties;
   }

   public String getUrl() {
      return m_url;
   }

   public String getUser() {
      return m_user;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_name == null ? 0 : m_name.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(Datasource other) {
      assertAttributeEquals(other, ENTITY_DATASOURCE, ATTR_NAME, m_name, other.getName());

   }

   public Datasource setDriver(String driver) {
      m_driver = driver;
      return this;
   }

   public Datasource setName(String name) {
      m_name = name;
      return this;
   }

   public Datasource setPassword(String password) {
      m_password = password;
      return this;
   }

   public Datasource setProperties(String properties) {
      m_properties = properties;
      return this;
   }

   public Datasource setUrl(String url) {
      m_url = url;
      return this;
   }

   public Datasource setUser(String user) {
      m_user = user;
      return this;
   }

}
