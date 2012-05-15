package com.site.maven.plugin.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.site.codegen.meta.TableMeta;
import com.site.maven.plugin.common.PropertyProviders;

/**
 * DAL Metadata generator for JDBC
 * 
 * @goal dal-jdbc-meta
 * @author Frankie Wu
 */
public class DalJdbcMetaMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Table meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected TableMeta m_meta;

   /**
    * Current project base directory
    * 
    * @parameter expression="${basedir}"
    * @required
    * @readonly
    */
   protected File baseDir;

   /**
    * @parameter expression="${jdbc.driver}"
    */
   protected String driver;

   /**
    * @parameter expression="${jdbc.url}"
    */
   protected String url;

   /**
    * @parameter expression="${jdbc.user}"
    */
   protected String user;

   /**
    * @parameter expression="${jdbc.password}"
    */
   protected String password;

   /**
    * @parameter expression="${jdbc.connectionProperties}"
    */
   protected String connectionProperties;

   /**
    * @parameter
    */
   protected List<String> includes;

   /**
    * @parameter
    */
   protected List<String> excludes;

   /**
    * @parameter expression="${outputDir}"
    *            default-value="${basedir}/src/main/resources/META-INF/dal/jdbc"
    * @required
    */
   protected String outputDir;

   /**
    * @parameter expression="${packageName}"
    */
   protected String packageName;

   private void validateParameters() {
      driver = getProperty(driver, "jdbc.driver", "JDBC driver:", "com.mysql.jdbc.Driver");
      url = getProperty(url, "jdbc.url", "JDBC URL[for example, jdbc:mysql://localhost:3306/test]:", null);
      user = getProperty(user, "jdbc.user", "JDBC user:", null);
      password = getProperty(password, "jdbc.password", "JDBC password:", null);
      connectionProperties = getProperty(connectionProperties, "jdbc.connectionProperties",
            "JDBC connection properties:", "useUnicode=true&autoReconnect=true");
      packageName = getProperty(packageName, "packageName", "Target package name:", null);
   }

   private String detectPackageName() {
      if (packageName != null) {
         return packageName;
      }

      String groupId = m_project.getGroupId();
      String artifactId = m_project.getArtifactId();

      return (groupId + "." + artifactId + ".dal").replace('-', '.');
   }

   @SuppressWarnings("unchecked")
   private void resolveAliasConfliction(Element entities) {
      Map<String, Integer> map = new HashMap<String, Integer>();
      List<Element> children = entities.getChildren("entity");

      for (Element entity : children) {
         String alias = entity.getAttributeValue("alias");
         Integer count = map.get(alias);

         if (count == null) {
            map.put(alias, 1);
         } else {
            count++;
            map.put(alias, count);
            entity.setAttribute("alias", alias + (count));
         }
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      validateParameters();

      Connection conn = setupConnection();

      try {
         DatabaseMetaData meta = conn.getMetaData();
         List<String> tables = getTables(meta);

         Element entities = new Element("entities");

         for (String table : tables) {
            Element entity = m_meta.getTableMeta(meta, table);

            entities.addContent(entity);
         }

         resolveAliasConfliction(entities);

         File outDir = getFile(outputDir);
         File outFile = new File(outDir, "codegen.xml");

         if (!outDir.exists()) {
            outDir.mkdirs();
         }

         saveFile(new Document(entities), outFile);

         File modelFile = new File(outDir, "dal.xml");

         if (!modelFile.exists()) {
            Document model = m_meta.getModel(detectPackageName());

            saveFile(model, modelFile);
         }

         File manifestFile = new File(outDir, "manifest.xml");

         if (!manifestFile.exists()) {
            Document manifest = m_meta.getManifest(outFile.getName(), modelFile.getName());

            saveFile(manifest, manifestFile);
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Error when generating DAL meta: " + e, e);
      }
   }

   private String getProperty(String value, String name, String prompt, String defaultValue) {
      if (value != null) {
         return value;
      } else {
         return PropertyProviders.fromConsole().forString(name, prompt, defaultValue, null);
      }
   }

   private Connection setupConnection() throws MojoExecutionException {
      Properties info = new Properties();

      info.put("user", user);
      info.put("password", password);

      if (connectionProperties != null) {
         String[] pairs = connectionProperties.split(Pattern.quote("&"));

         for (String pair : pairs) {
            int pos = pair.indexOf('=');

            if (pos > 0) {
               info.put(pair.substring(0, pos), pair.substring(pos + 1));
            } else {
               getLog().warn("invalid property: " + pair + " ignored.");
            }
         }
      }

      try {
         Class.forName(driver);

         return DriverManager.getConnection(url, info);
      } catch (Exception e) {
         throw new MojoExecutionException("Can't get connection: " + e, e);
      }
   }

   private File getFile(String path) {
      File file;

      if (path.startsWith("/") || path.indexOf(':') > 0) {
         file = new File(path);
      } else {
         file = new File(baseDir, path);
      }

      return file;
   }

   private void saveFile(Document codegen, File file) throws IOException {
      Format format = Format.getPrettyFormat();
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(codegen, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }

   private List<String> getTables(DatabaseMetaData meta) throws SQLException {
      List<String> tables = new ArrayList<String>();

      if (includes == null) {
         includes = new ArrayList<String>();

         ResultSet rs = meta.getTables(null, null, "%", new String[] { "TABLE" });

         while (rs.next()) {
            String table = rs.getString("TABLE_NAME");

            tables.add(table);
         }

         rs.close();
      } else {
         for (String include : includes) {
            ResultSet rs = meta.getTables(null, null, include, new String[] { "TABLE" });

            while (rs.next()) {
               String table = rs.getString("TABLE_NAME");

               if (!tables.contains(table)) {
                  tables.add(table);
               }
            }

            rs.close();
         }
      }

      if (excludes != null) {
         for (String exclude : excludes) {
            int index = tables.indexOf(exclude);

            if (index >= 0) {
               tables.remove(index);
            }
         }
      }

      Collections.sort(tables);

      return tables;
   }
}
