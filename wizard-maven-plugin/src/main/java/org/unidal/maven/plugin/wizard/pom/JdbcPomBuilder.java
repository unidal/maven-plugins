package org.unidal.maven.plugin.wizard.pom;

import java.io.File;
import java.io.IOException;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@Named
public class JdbcPomBuilder extends AbstractPomBuilder {
   private PomDelegate m_pom = new PomDelegate();

   private void addDependencies(Element root, Wizard wizard) {
      Element dependencies = m_pom.findOrCreateChild(root, "dependencies", "build", null);

      if (!m_pom.checkDependency(dependencies, "org.unidal.framework", "dal-jdbc", "6.0.0", null)) {
         m_pom.checkDependency(dependencies, "mysql", "mysql-connector-java", "8.0.28", "runtime");
      }
   }

   public void build(File pomFile, Wizard wizard) throws JDOMException, IOException {
      Document doc = super.loadPom(pomFile);
      Element root = doc.getRootElement();

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
      Element plugin = m_pom.checkPlugin(plugins, "org.unidal.maven.plugins", "codegen-maven-plugin", "5.0.0");
      Element generate = m_pom.checkPluginExecution(plugin, "dal-jdbc", "generate-sources", "generate jdbc files");
      Element configuration = m_pom.findOrCreateChild(generate, "configuration");
      Element manifestElement = m_pom.findOrCreateChild(configuration, "manifest");

      // for pom.xml rewrite
      m_pom.setModified(true);
      manifestElement.setContent(new CDATA(getManifestFiles(wizard)));
   }

   private String getManifestFiles(Wizard wizard) {
      StringBuilder sb = new StringBuilder(1024);
      String prefix = "${basedir}/src/main/resources/META-INF/dal/jdbc/";
      String indent = "                        ";
      boolean first = true;

      for (Jdbc jdbc : wizard.getJdbcs()) {
         for (Group group : jdbc.getGroups()) {
            if (first) {
               first = false;
            } else {
               sb.append(",");
            }

            sb.append("\r\n");
            sb.append(indent).append(prefix).append(group.getName()).append("-manifest.xml");
         }
      }

      return sb.toString();
   }
}
