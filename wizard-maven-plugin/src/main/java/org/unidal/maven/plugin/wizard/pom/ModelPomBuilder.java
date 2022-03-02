package org.unidal.maven.plugin.wizard.pom;

import java.io.File;
import java.io.IOException;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

@Named
public class ModelPomBuilder extends AbstractPomBuilder {
   public void build(File pomFile, Wizard wizard) throws JDOMException, IOException {
      Document doc = super.loadPom(pomFile);
      Element root = doc.getRootElement();

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
      Element plugin = m_pom.checkPlugin(plugins, "org.unidal.maven.plugins", "codegen-maven-plugin");
      Element generate = m_pom.checkPluginExecution(plugin, "dal-model", "generate-sources", "generate model files");
      Element configuration = m_pom.findOrCreateChild(generate, "configuration");
      Element manifestElement = m_pom.findOrCreateChild(configuration, "manifest");

      manifestElement.setContent(new CDATA(getManifestFiles(wizard)));
   }

   private String getManifestFiles(Wizard wizard) {
      StringBuilder sb = new StringBuilder(1024);
      String prefix = "${basedir}/src/main/resources/META-INF/dal/model/";
      String indent = "                        ";
      boolean first = true;

      for (Model model : wizard.getModels()) {
         if (first) {
            first = false;
         } else {
            sb.append(",");
         }

         sb.append("\r\n");
         sb.append(indent).append(prefix).append(model.getName()).append("-manifest.xml");
      }

      return sb.toString();
   }
}
