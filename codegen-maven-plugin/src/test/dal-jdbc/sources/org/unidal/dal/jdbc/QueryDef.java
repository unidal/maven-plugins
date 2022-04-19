package org.unidal.dal.jdbc;

public class QueryDef {
   private String m_name;

   private Class<?> m_entityClass;

   private String m_pattern;

   private QueryType m_type;

   private boolean m_storeProcedure;

   private boolean m_raw;

   public QueryDef(String name, Class<?> entityClass, QueryType type, String pattern) {
      m_name = name;
      m_entityClass = entityClass;
      m_type = type;
      m_pattern = pattern;
      m_raw = false;
   }

   public QueryDef(String name, Class<?> entityClass, QueryType type, String pattern, boolean isStoreProcedure) {
      this(name, entityClass, type, pattern);

      m_storeProcedure = isStoreProcedure;
   }

   public Class<?> getEntityClass() {
      return m_entityClass;
   }

   public String getName() {
      return m_name;
   }

   public QueryType getType() {
      return m_type;
   }

   public boolean isRaw() {
      return m_raw;
   }

   public boolean isStoreProcedure() {
      return m_storeProcedure;
   }

   @Override
   public String toString() {
      return m_pattern;
   }
}
