package org.unidal.maven.plugin.wizard.meta;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.maven.project.MavenProject;
import org.unidal.codegen.code.DefaultObfuscater;
import org.unidal.codegen.code.Obfuscater;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.codegen.helper.PropertyProviders.IValidator;
import org.unidal.helper.Codes;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.model.entity.Datasource;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Table;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.xml.sax.SAXException;

@Named
public class JdbcWizardBuilder extends AbstractWizardBuilder {
   private Obfuscater m_obfuscater = new DefaultObfuscater();

   private Connection m_conn;

   public Wizard build(MavenProject project) throws IOException, SAXException {
      super.initialize(project);

      Wizard wizard = super.loadWizard();

      wizard.accept(new Builder());

      super.saveWizard(wizard);
      super.saveManifest();

      return wizard;
   }

   public Connection getConnection() {
      return m_conn;
   }

   private class Builder extends BaseVisitor {
      private Wizard m_wizard;

      private Jdbc m_jdbc;

      private List<String> getAvailableTableNames(Jdbc jdbc) {
         final Set<String> existing = new HashSet<String>();

         for (Group group : jdbc.getGroups()) {
            for (Table table : group.getTables()) {
               existing.add(table.getName());
            }
         }

         try {
            List<String> tables = new ArrayList<String>();
            ResultSet rs = m_conn.getMetaData().getTables(null, null, "%", new String[] { "TABLE" });

            while (rs.next()) {
               String table = rs.getString("TABLE_NAME");

               if (!existing.contains(table)) {
                  tables.add(table);
               }
            }

            rs.close();
            Collections.sort(tables);
            return tables;
         } catch (SQLException e) {
            throw new RuntimeException(e);
         }
      }

      private Connection setupConnection(Datasource ds) {
         Properties properties = new Properties();
         String password = ds.getPassword();

         if (password.startsWith("~{") && password.endsWith("}")) {
            password = Codes.forDecode().decode(password.substring(2, password.length() - 1));
         }

         properties.put("user", ds.getUser());
         properties.put("password", password);

         if (ds.getProperties() != null) {
            String[] pairs = ds.getProperties().split(Pattern.quote("&"));

            for (String pair : pairs) {
               int pos = pair.indexOf('=');

               if (pos > 0) {
                  properties.put(pair.substring(0, pos), pair.substring(pos + 1));
               } else {
                  System.err.println("invalid property: " + pair + " ignored.");
               }
            }
         }

         try {
            Class.forName(ds.getDriver());

            return DriverManager.getConnection(ds.getUrl(), properties);
         } catch (Exception e) {
            throw new RuntimeException("Cannot get connection due to " + e, e);
         }
      }

      @Override
      public void visitDatasource(Datasource ds) {
         ConsoleProvider console = PropertyProviders.fromConsole();

         ds.setDriver(console.forString("driver", "JDBC driver:", "com.mysql.cj.jdbc.Driver", null));
         ds.setUrl(console.forString("url", "JDBC URL:", "jdbc:mysql://localhost:3306/" + ds.getName(), null));
         ds.setUser(console.forString("user", "User:", null, null));

         String password = console.forString("password", "Password:(use '<none>' if no password)", null, null);

         if (password.equals("<none>")) {
            password = "";
         }

         try {
            ds.setPassword("~{" + m_obfuscater.encode(password) + "}");
         } catch (Exception e) {
            ds.setPassword(password);
         }

         String defaultProperties = "useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=true";

         ds.setProperties(console.forString("properties", "Connection properties:", defaultProperties, null));

         m_conn = setupConnection(ds);
      }

      @Override
      public void visitGroup(final Group group) {
         if (group.getPackage() == null) {
            group.setPackage(m_jdbc.getPackage() + "." + group.getName().toLowerCase());
         }

         final List<String> availableNames = getAvailableTableNames(m_jdbc);
         final List<String> existingNames = new ArrayList<String>();
         ConsoleProvider console = PropertyProviders.fromConsole();

         System.out.println("Existing tables in group(" + group.getName() + ") is: ");

         for (Table table : group.getTables()) {
            existingNames.add(table.getName());
            System.out.println(table.getName());
         }

         System.out.println("Existing tables: " + existingNames);
         console.forString("table", "Select table name below, or '<end>' to end:", availableNames, null,
               new IValidator<String>() {
                  @Override
                  public boolean validate(String name) {
                     if ("<end>".equals(name)) {
                        return true;
                     } else if (availableNames.contains(name)) {
                        existingNames.add(name);
                        availableNames.remove(name);
                        System.out.println("Tables selected: " + existingNames);
                        group.findOrCreateTable(name);
                     }

                     return false;
                  }
               });
      }

      @Override
      public void visitJdbc(Jdbc jdbc) {
         m_jdbc = jdbc;

         ConsoleProvider console = PropertyProviders.fromConsole();

         // setup data source
         String packageName = console.forString("jdbc.package", "Jdbc Package:", m_wizard.getPackage() + ".dal", null);

         jdbc.setPackage(packageName);
         jdbc.setDatasource(new Datasource());

         visitDatasource(jdbc.getDatasource());

         // for group
         List<String> names = new ArrayList<String>();

         for (Group group : jdbc.getGroups()) {
            names.add(group.getName());
         }

         String name = console.forString("group", "Select group name below or input a new one:", names, null, null);
         Group group = jdbc.findOrCreateGroup(name);

         visitGroup(group);
      }

      @Override
      public void visitWizard(final Wizard wizard) {
         m_wizard = wizard;

         List<String> names = new ArrayList<String>();

         for (Jdbc jdbc : wizard.getJdbcs()) {
            names.add(jdbc.getName());
         }

         ConsoleProvider console = PropertyProviders.fromConsole();
         String name = console.forString("datasource", "Select datasource below or input a new one:", names, null,
               null);
         Jdbc jdbc = wizard.findJdbc(name);

         if (jdbc == null) { // must be a new sample model
            jdbc = new Jdbc(name);

            wizard.addJdbc(jdbc);
         }

         visitJdbc(jdbc);
      }
   }
}