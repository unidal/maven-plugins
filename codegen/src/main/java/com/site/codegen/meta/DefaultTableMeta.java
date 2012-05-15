package com.site.codegen.meta;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

public class DefaultTableMeta implements TableMeta {
   private static final String KEY_PREFIX = "key-";

   private void addIndices(Element entity, DatabaseMetaData meta, String table) throws SQLException {
      ResultSet rs = meta.getIndexInfo(null, null, table, false, false);
      StringBuilder sb = new StringBuilder(256);
      Element primaryKey = entity.getChild("primary-key");
      String primaryKeyName = primaryKey.getAttributeValue("name");
      Element index = new Element("index");
      String indexName = null;

      while (rs.next()) {
         String column = rs.getString("COLUMN_NAME");
         String name = rs.getString("INDEX_NAME");
         String sort = rs.getString("ASC_OR_DESC");
         boolean nonUnique = rs.getBoolean("NON_UNIQUE");

         if (name.equals(primaryKeyName)) {
            continue;
         }

         if (sb.length() > 0 && !name.equals(indexName)) {
            index.setAttribute("members", sb.toString());
            sb.setLength(0);
            entity.addContent(index);
            index = new Element("index");
         }

         indexName = name;
         index.setAttribute("name", name);

         if (!nonUnique) {
            index.setAttribute("unique", "true");
         }

         if (sb.length() > 0) {
            sb.append(", ");
         }

         sb.append(column);

         if (sort != null) {
            if ("A".equals(sort)) {
               sb.append(" ASC");
            } else {
               sb.append(" DESC");
            }
         }
      }

      rs.close();

      if (sb.length() > 0) {
         index.setAttribute("members", sb.toString());
         entity.addContent(index);
      }
   }

   private void addMember(Element entity, ResultSet rs, List<String> keyMembers) throws SQLException {
      String fieldName = rs.getString("COLUMN_NAME");
      int dataType = rs.getInt("DATA_TYPE");
      int length = rs.getInt("COLUMN_SIZE");
      int decimal = rs.getInt("DECIMAL_DIGITS");
      int nullable = rs.getInt("NULLABLE");
      String valueType = convertValueType(dataType, length, decimal);
      boolean hasLength = !"Date".equals(valueType) && !"boolean".equals(valueType);
      boolean autoIncrement = rs.getBoolean("IS_AUTOINCREMENT");
      Element member = new Element("member");
      String name = normalize(fieldName);

      entity.addContent(member);
      member.setAttribute("name", name);
      member.setAttribute("field", fieldName);
      member.setAttribute("value-type", valueType);

      if (hasLength) {
         member.setAttribute("length", String.valueOf(length));
      }

      if (decimal > 0) {
         member.setAttribute("decimal", String.valueOf(decimal));
      }

      if (nullable == 0) {
         member.setAttribute("nullable", "false");
      }

      if (keyMembers.contains(name)) {
         member.setAttribute("key", "true");
      }

      if (autoIncrement) {
         member.setAttribute("auto-increment", "true");
      }
   }

   private void addMembers(Element entity, DatabaseMetaData meta, String table, List<String> keyMembers)
         throws SQLException {
      ResultSet rs = meta.getColumns(null, null, table, null);

      while (rs.next()) {
         addMember(entity, rs, keyMembers);
      }

      rs.close();
   }

   private void addQueryDefs(Element entity, List<String> keyMembers) {
      Element queryDefs = new Element("query-defs");

      entity.addContent(queryDefs);

      addQueryFindByPK(queryDefs, keyMembers);
      addQueryInsert(queryDefs, keyMembers);
      addQueryUpdateByPK(queryDefs, keyMembers);
      addQueryDeleteByPK(queryDefs, keyMembers);
   }

   private void addQueryDeleteByPK(Element queryDefs, List<String> keyMembers) {
      Element query = new Element("query");

      queryDefs.addContent(query);
      query.setAttribute("name", "delete-by-PK");
      query.setAttribute("type", "DELETE");

      addQueryParams(query, keyMembers);

      Element statement = new Element("statement");
      query.addContent(statement);

      StringBuilder sb = new StringBuilder();

      sb.append("DELETE FROM <TABLE/>");
      sb.append(getQueryKeyClause(keyMembers));
      statement.addContent(new CDATA(sb.toString()));
   }

   private void addQueryFindByPK(Element queryDefs, List<String> keyMembers) {
      Element query = new Element("query");

      queryDefs.addContent(query);
      query.setAttribute("name", "find-by-PK");
      query.setAttribute("type", "SELECT");

      addQueryParams(query, keyMembers);

      Element statement = new Element("statement");
      query.addContent(statement);

      StringBuilder sb = new StringBuilder();

      sb.append("SELECT <FIELDS/> FROM <TABLE/>");
      sb.append(getQueryKeyClause(keyMembers));
      statement.addContent(new CDATA(sb.toString()));
   }

   private void addQueryInsert(Element queryDefs, List<String> keyMembers) {
      Element query = new Element("query");

      queryDefs.addContent(query);
      query.setAttribute("name", "insert");
      query.setAttribute("type", "INSERT");

      Element statement = new Element("statement");
      query.addContent(statement);

      StringBuilder sb = new StringBuilder();

      sb.append("INSERT INTO <TABLE/>(<FIELDS/>) VALUES(<VALUES/>)");
      statement.addContent(new CDATA(sb.toString()));
   }

   private void addQueryParams(Element query, List<String> keyMembers) {
      for (String name : keyMembers) {
         Element param = new Element("param");

         query.addContent(param);
         param.setAttribute("name", KEY_PREFIX + name);
      }
   }

   private void addQueryUpdateByPK(Element queryDefs, List<String> keyMembers) {
      Element query = new Element("query");

      queryDefs.addContent(query);
      query.setAttribute("name", "update-by-PK");
      query.setAttribute("type", "UPDATE");

      addQueryParams(query, keyMembers);

      Element statement = new Element("statement");
      query.addContent(statement);

      StringBuilder sb = new StringBuilder();

      sb.append("UPDATE <TABLE/> SET <FIELDS/>");
      sb.append(getQueryKeyClause(keyMembers));
      statement.addContent(new CDATA(sb.toString()));
   }

   private void addReadsets(Element entity) {
      Element readsets = new Element("readsets");
      Element readset = new Element("readset");

      entity.addContent(readsets);
      readsets.addContent(readset);

      readset.setAttribute("name", "FULL");
      readset.setAttribute("all", "true");
   }

   private void addUpdatesets(Element entity) {
      Element updatesets = new Element("updatesets");
      Element updateset = new Element("updateset");

      entity.addContent(updatesets);
      updatesets.addContent(updateset);

      updateset.setAttribute("name", "FULL");
      updateset.setAttribute("all", "true");
   }

   private void addVars(Element entity, List<String> keyMembers) {
      for (String name : keyMembers) {
         Element var = new Element("var");

         entity.addContent(var);
         var.setAttribute("name", KEY_PREFIX + name);
         var.setAttribute("value-type", findValueType(entity, name));
         var.setAttribute("key-member", name);
      }
   }

   private String convertValueType(int dataType, int size, int decimal) {
      switch (dataType) {
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
         return "String";
      case Types.VARBINARY:
      case Types.LONGVARBINARY:
         return "byte[]";
      case Types.TINYINT:
      case Types.SMALLINT:
      case Types.INTEGER:
         if (size < 12) {
            return "int";
         } else {
            return "long";
         }
      case Types.BIGINT:
         return "long";
      case Types.REAL:
      case Types.DOUBLE:
      case Types.DECIMAL:
         return "double";
      case Types.DATE:
      case Types.TIMESTAMP:
         return "Date";
      case Types.BIT:
      case Types.BOOLEAN:
         return "boolean";
      }

      return "" + dataType;
   }

   @SuppressWarnings("unchecked")
   private String findValueType(Element entity, String name) {
      List<Element> members = entity.getChildren("member");

      for (Element member : members) {
         if (name.equals(member.getAttributeValue("name"))) {
            return member.getAttributeValue("value-type");
         }
      }

      return "";
   }

   private String getAlias(String src) {
      int len = src.length();
      StringBuilder sb = new StringBuilder(len);
      boolean first = true;

      for (int i = 0; i < len; i++) {
         char ch = src.charAt(i);

         if (ch == '_') {
            first = true;
         } else if (first) {
            sb.append(Character.toLowerCase(ch));
            first = false;
         }
      }

      return sb.toString();
   }

   @Override
   public Document getManifest(String codegenXml, String modelXml) {
      Element manifest = new Element("manifest");

      manifest.addContent(new Element("file").setAttribute("path", codegenXml));
      manifest.addContent(new Element("file").setAttribute("path", modelXml));

      return new Document(manifest);
   }

   @Override
   public Document getModel(String packageName) {
      Element model = new Element("entities");

      model.setAttribute("do-package", packageName);
      model.setAttribute("gen", "true");
      return new Document(model);
   }

   private Element getPrimaryKey(DatabaseMetaData meta, String table, List<String> keyMembers) throws SQLException {
      ResultSet rs = meta.getPrimaryKeys(null, null, table);
      String primaryKeyName = null;

      String[] columns = new String[64];
      int maxSeq = 0;

      while (rs.next()) {
         int seq = rs.getInt("KEY_SEQ");
         String column = rs.getString("COLUMN_NAME");
         String name = rs.getString("PK_NAME");

         columns[seq - 1] = column;
         primaryKeyName = name;

         if (seq > maxSeq) {
            maxSeq = seq;
         }
      }

      rs.close();

      StringBuilder sb = new StringBuilder(64);

      for (int i = 0; i < maxSeq; i++) {
         if (i > 0) {
            sb.append(", ");
         }

         keyMembers.add(normalize(columns[i]));
         sb.append(columns[i]);
      }

      Element primaryKey = new Element("primary-key");

      if (primaryKeyName != null) {
         primaryKey.setAttribute("name", primaryKeyName);
         primaryKey.setAttribute("members", sb.toString());
      }

      return primaryKey;
   }

   private String getQueryKeyClause(List<String> keyMembers) {
      StringBuilder sb = new StringBuilder(256);
      int size = keyMembers.size();

      for (int i = 0; i < size; i++) {
         String name = keyMembers.get(i);
         String keyName = KEY_PREFIX + name;

         if (i == 0) {
            sb.append(" WHERE");
         } else {
            sb.append(" AND");
         }

         sb.append(" <FIELD name='").append(name).append("'/> = ${").append(keyName).append("}");
      }

      return sb.toString();
   }

   public Element getTableMeta(DatabaseMetaData meta, String table) throws SQLException {
      List<String> keyMembers = new ArrayList<String>();
      Element entity = new Element("entity");
      Element primaryKey = getPrimaryKey(meta, table, keyMembers);

      setAttributes(entity, meta, table);
      addMembers(entity, meta, table, keyMembers);
      addVars(entity, keyMembers);
      entity.addContent(primaryKey);
      addIndices(entity, meta, table);
      addReadsets(entity);
      addUpdatesets(entity);
      addQueryDefs(entity, keyMembers);

      return entity;
   }

   String normalize(String src) {
      int len = src.length();
      StringBuilder sb = new StringBuilder(len + 4);
      boolean flag = true;

      for (int i = 0; i < len; i++) {
         char ch = src.charAt(i);

         if (ch == '_' || ch == '-') {
            sb.append('-');
            flag = true;
         } else {
            char lowerCase = Character.toLowerCase(ch);

            if (ch != lowerCase) {
               if (!flag) {
                  sb.append('-');
                  flag = true;
               }
            } else {
               flag = false;
            }

            sb.append(lowerCase);
         }
      }

      return sb.toString();
   }

   private void setAttributes(Element table, DatabaseMetaData meta, String name) {
      table.setAttribute("name", normalize(name));
      table.setAttribute("table", name);
      table.setAttribute("alias", getAlias(name));
   }
}
