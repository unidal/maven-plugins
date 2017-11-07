package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;
import java.io.FileWriter;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@Named(instantiationStrategy = Named.PER_LOOKUP)
public class WebAppPomBuilder implements LogEnabled {
   private File m_pomFile;

   private Document m_doc;

   private PomDelegate m_pom;

   private Wizard m_wizard;

   private Webapp m_webapp;

   private Logger m_logger;

   public void build(File pomFile, Wizard wizard) throws Exception {
      m_pomFile = pomFile;
      m_wizard = wizard;
      m_webapp = m_wizard.getWebapp();
      m_doc = new SAXBuilder().build(pomFile);
      m_pom = new PomDelegate();

      build();
   }

   private void build() {
      Element root = m_doc.getRootElement();
      Element dependencies = m_pom.findOrCreateChild(root, "dependencies");
      Element build = m_pom.findOrCreateChild(root, "build", null, "dependencies");

      buildProject(root);
      buildDependencies(dependencies);
      buildPluginManagement(build);
      buildPlugins(build);
      buildProperties(root);
   }

   private void buildDependencies(Element dependencies) {
      if (!m_pom.checkDependency(dependencies, "org.unidal.framework", "web-framework", "4.0.0", null)) {
         if (m_webapp.isJstl()) {
            m_pom.checkDependency(dependencies, "javax.servlet", "jstl", "1.2", null);
         }

         if (m_webapp.isWebres()) {
            m_pom.checkDependency(dependencies, "org.unidal.webres", "WebResServer", "1.2.1", null);
         }

         m_pom.checkDependency(dependencies, "javax.servlet", "javax.servlet-api", "3.1.0", "provided");
         m_pom.checkDependency(dependencies, "junit", "junit", "4.8.1", "test");
         m_pom.checkDependency(dependencies, "org.unidal.framework", "foundation-service", "4.0.0", null);
         m_pom.checkDependency(dependencies, "org.unidal.framework", "test-framework", "4.0.0", "test");
         m_pom.checkDependency(dependencies, "org.eclipse.jetty", "jetty-jsp", "9.2.9.v20150224", "test");
      }
   }

   private void buildPluginManagement(Element build) {
      if (m_webapp.isPluginManagement()) {
         Element pluginManagement = m_pom.findOrCreateChild(build, "pluginManagement");
         Element pluginManagementPlugins = m_pom.findOrCreateChild(pluginManagement, "plugins");
         Element compilerPlugin = m_pom.checkPlugin(pluginManagementPlugins, null, "maven-compiler-plugin", "2.5.1");
         Element compilerConfiguration = m_pom.findOrCreateChild(compilerPlugin, "configuration");

         m_pom.findOrCreateChild(compilerConfiguration, "source").setText("1.6");
         m_pom.findOrCreateChild(compilerConfiguration, "target").setText("1.6");

         Element eclipsePlugin = m_pom.checkPlugin(pluginManagementPlugins, null, "maven-eclipse-plugin", "2.9");
         Element eclipseConfiguration = m_pom.findOrCreateChild(eclipsePlugin, "configuration");

         m_pom.findOrCreateChild(eclipseConfiguration, "downloadSources").setText("true");
         m_pom.findOrCreateChild(eclipseConfiguration, "ajdtVersion").setText("none");

         Element additionalConfig = m_pom.findOrCreateChild(eclipseConfiguration, "additionalConfig");
         Element file = m_pom.findOrCreateChild(additionalConfig, "file");

         m_pom.findOrCreateChild(file, "name").setText(".settings/org.eclipse.jdt.core.prefs");

         Element contentElement = m_pom.findOrCreateChild(file, "content");

         if (contentElement.getContent().isEmpty()) {
            String indent = "                           ";

            contentElement.addContent(new CDATA( //
                  indent + "eclipse.preferences.version=1\r\n" + //
                        indent + "org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.6\r\n" + //
                        indent + "org.eclipse.jdt.core.compiler.source=1.6\r\n" + //
                        indent + "org.eclipse.jdt.core.compiler.compliance=1.6\r\n"));
         }
      }
   }

   private void buildPlugins(Element build) {
      Element plugins = m_pom.findOrCreateChild(build, "plugins");
      Element plexusPlugin = m_pom.checkPlugin(plugins, "org.unidal.maven.plugins", "plexus-maven-plugin", "3.0.2");
      Element plexus = m_pom.checkPluginExecution(plexusPlugin, "plexus", null, "generate plexus component descriptor");
      Element codegenPlexusConfiguration = m_pom.findOrCreateChild(plexus, "configuration");

      m_pom.findOrCreateChild(codegenPlexusConfiguration, "className").setText(
            m_wizard.getPackage() + ".build.ComponentsConfigurator");
   }

   private void buildProject(Element project) {
      Element packaging = m_pom.findChild(project, "packaging");
      String projectType = packaging == null ? "jar" : packaging.getText();

      if (!m_webapp.isModule() && "jar".equals(projectType)) {
         m_logger.info(String.format("Change project packaging type from %s to war.", projectType));

         m_pom.findOrCreateChild(project, "packaging", "dependencies", null).setText("war");
      }
   }

   private void buildProperties(Element project) {
      Element properties = m_pom.findOrCreateChild(project, "properties");
      Element sourceEncoding = m_pom.findOrCreateChild(properties, "project.build.sourceEncoding");

      if (sourceEncoding.getText().length() == 0) {
         sourceEncoding.setText("utf-8");
      }
   }

   public void save() throws Exception {
      if (m_pom.isModified()) {
         File parent = m_pomFile.getCanonicalFile().getParentFile();

         if (!parent.exists()) {
            parent.mkdirs();
         }

         Format format = Format.getPrettyFormat().setIndent("   ");
         XMLOutputter outputter = new XMLOutputter(format);
         FileWriter writer = new FileWriter(m_pomFile);

         try {
            outputter.output(m_doc, writer);
            m_logger.info(String.format("File(%s) generated.", m_pomFile.getCanonicalPath()));
         } finally {
            writer.close();
         }

         m_logger.info(String.format("Added dependencies to POM file(%s).", m_pomFile));
         m_logger.info("Please run following command to setup eclipse environment:");
         m_logger.info("   mvn eclipse:clean eclipse:eclipse");
      }
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }
}
