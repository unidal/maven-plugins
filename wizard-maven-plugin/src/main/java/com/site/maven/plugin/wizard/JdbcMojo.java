package com.site.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

import com.site.codegen.generator.AbstractGenerateContext;
import com.site.codegen.generator.GenerateContext;
import com.site.codegen.generator.Generator;
import com.site.codegen.meta.WizardMeta;
import com.site.helper.Files;
import com.site.maven.plugin.common.PropertyProviders;
import com.site.maven.plugin.wizard.model.entity.Datasource;
import com.site.maven.plugin.wizard.model.entity.Datasources;
import com.site.maven.plugin.wizard.model.entity.JdbcConnection;
import com.site.maven.plugin.wizard.model.entity.Wizard;
import com.site.maven.plugin.wizard.model.transform.BaseVisitor;
import com.site.maven.plugin.wizard.model.transform.DefaultXmlParser;

/**
 * Create a new page of web application project.
 * 
 * @goal jdbc
 */
public class JdbcMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Current project base directory
    * 
    * @parameter expression="${basedir}"
    * @required
    * @readonly
    */
   protected File baseDir;

   /**
    * XSL code generator implementation
    * 
    * @component role="com.site.codegen.generator.Generator"
    *            role-hint="wizard-jdbc"
    * @required
    * @readonly
    */
   protected Generator m_generator;

   /**
    * Wizard meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected WizardMeta m_meta;

   /**
    * Current project base directory
    * 
    * @parameter expression="${sourceDir}" default-value="${basedir}"
    * @required
    */
   protected String sourceDir;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}" default-value=
    *            "${basedir}/src/main/resources/META-INF/wizard/jdbc/manifest.xml"
    * @required
    */
   protected String manifest;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}" default-value="/META-INF/wizard/jdbc"
    * @required
    */
   protected String resouceBase;

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
    * @parameter expression="${packageName}"
    */
   protected String packageName;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   protected boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   protected boolean debug;

   private String detectPackageName() {
      if (packageName != null) {
         return packageName;
      }

      String groupId = m_project.getGroupId();
      String artifactId = m_project.getArtifactId();

      return (groupId + "." + artifactId + ".dal").replace('-', '.');
   }

   protected Wizard buildWizard(File wizardFile) throws IOException, SAXException {
      Wizard wizard;

      if (wizardFile.isFile()) {
         String content = Files.forIO().readFrom(wizardFile, "utf-8");
         wizard = new DefaultXmlParser().parse(content);
      } else {
         wizard = new Wizard();
         wizard.setDatasources(new Datasources());
      }

      Datasources datasources = wizard.getDatasources();
      final List<String> names = new ArrayList<String>();

      datasources.accept(new BaseVisitor() {
         @Override
         public void visitDatasource(Datasource datasource) {
            names.add(datasource.getName());
         }
      });

      String name = PropertyProviders.fromConsole().forString("datasource", "Datasource name:", names, null, null);
      Datasource datasource = new Datasource(name);
      JdbcConnection conn = new JdbcConnection();

      datasources.addDatasource(datasource);
      datasource.setJdbcConnection(conn);

      driver = getProperty(driver, "jdbc.driver", "JDBC driver:", "com.mysql.jdbc.Driver");
      url = getProperty(url, "jdbc.url", "JDBC URL[for example, jdbc:mysql://localhost:3306/test]:", null);
      user = getProperty(user, "jdbc.user", "JDBC user:", null);
      password = getProperty(password, "jdbc.password", "JDBC password:", null);
      connectionProperties = getProperty(connectionProperties, "jdbc.connectionProperties",
            "JDBC connection properties:", "useUnicode=true&autoReconnect=true");

      conn.setDriver(driver);
      conn.setUrl(url);
      conn.setUser(user);
      conn.setPassword(password);
      conn.setProperties(connectionProperties);

      String doPpackage = getProperty(packageName, "packageName", "Target package name:", detectPackageName());

      datasource.setDoPackage(doPpackage);

      return wizard;
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final File manifestFile = getFile(manifest);
         File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");
         Reader reader = new StringReader(buildWizard(wizardFile).toString());

         if (!manifestFile.exists()) {
            saveXml(m_meta.getManifest("wizard.xml"), manifestFile);
         }

         saveXml(m_meta.getWizard(reader), wizardFile);

         final URL manifestXml = manifestFile.toURI().toURL();
         final GenerateContext ctx = new AbstractGenerateContext(m_project.getBasedir(), resouceBase, sourceDir) {
            public URL getManifestXml() {
               return manifestXml;
            }

            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().debug(message);
                  }
                  break;
               case INFO:
                  if (debug || verbose) {
                     getLog().info(message);
                  }
                  break;
               case ERROR:
                  getLog().error(message);
                  break;
               }
            }
         };

         m_generator.generate(ctx);
         m_project.addCompileSourceRoot(sourceDir);
         getLog().info(ctx.getGeneratedFiles() + " files generated.");
      } catch (Exception e) {
         throw new MojoExecutionException("Code generating failed.", e);
      }
   }

   protected File getFile(String path) {
      File file;

      if (path.startsWith("/") || path.indexOf(':') > 0) {
         file = new File(path);
      } else {
         file = new File(baseDir, path);
      }

      return file;
   }

   protected String getProperty(String value, String name, String prompt, String defaultValue) {
      if (value != null) {
         return value;
      } else {
         return PropertyProviders.fromConsole().forString(name, prompt, defaultValue, null);
      }
   }

   protected void saveXml(Document doc, File file) throws IOException {
      File parent = file.getCanonicalFile().getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      Format format = Format.getPrettyFormat();
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(doc, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }
}
