package org.unidal.maven.plugin.wizard.pom;

import java.io.File;

import org.jdom.Document;
import org.jdom.Element;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@Named
public class WebAppPomBuilder extends AbstractPomBuilder {
   private PomDelegate m_pom = new PomDelegate();

   private void addDependencies(Element root, Wizard wizard) {
      Element dependencies = m_pom.findOrCreateChild(root, "dependencies", "build", null);

      if (!m_pom.checkDependency(dependencies, "org.unidal.framework", "web-framework", "6.0.0", null)) {
         m_pom.checkDependency(dependencies, "javax.servlet", "javax.servlet-api", "3.1.0", "provided");
         m_pom.checkDependency(dependencies, "org.unidal.test", "test-framework", "6.0.0", "test");
      }
   }

   public void build(File pomFile, Wizard wizard) throws Exception {
      Document doc = super.loadPom(pomFile);
      Element root = doc.getRootElement();

      setPackaging(root, wizard);
      addDependencies(root, wizard);
      configurePlugin(root, wizard);

      if (m_pom.isModified()) {
         super.savePom(pomFile, doc);

         m_logger.info(String.format("Added dependencies to POM file(%s).", pomFile));
         m_logger.info("");
         m_logger.info("Please run following command to setup eclipse environment:");
         m_logger.info("   mvn eclipse:clean eclipse:eclipse");
      }
   }

   private void configurePlugin(Element root, Wizard wizard) {
      Element build = m_pom.findOrCreateChild(root, "build", null, "dependencies");
      Element plugins = m_pom.findOrCreateChild(build, "plugins");
      Element plexusPlugin = m_pom.checkPlugin(plugins, "org.unidal.maven.plugins", "plexus-maven-plugin", "5.0.0");
      Element plexus = m_pom.checkPluginExecution(plexusPlugin, "plexus", null, "generate plexus component descriptor");
      Element codegenPlexusConfiguration = m_pom.findOrCreateChild(plexus, "configuration");
      Element className = m_pom.findOrCreateChild(codegenPlexusConfiguration, "className");

      className.setText(wizard.getPackage() + ".build.ComponentsConfigurator");
   }

   private void setPackaging(Element project, Wizard wizard) {
      Element packaging = m_pom.findChild(project, "packaging");
      String type = (packaging == null ? "jar" : packaging.getText());

      if (!wizard.getWebapp().isModule() && "jar".equals(type)) {
         m_pom.findOrCreateChild(project, "packaging", "dependencies", null).setText("war");

         m_logger.info(String.format("Change project packaging type from %s to war.", type));
      }
   }
}
