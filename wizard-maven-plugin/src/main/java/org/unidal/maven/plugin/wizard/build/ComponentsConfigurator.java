package org.unidal.maven.plugin.wizard.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.maven.plugin.pom.MavenContainer;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.pom.VersionMapping;
import org.unidal.maven.plugin.wizard.webapp.WebAppPomBuilder;
import org.unidal.maven.plugin.wizard.webapp.WebAppWizardBuilder;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(MavenContainer.class));
      all.add(A(VersionMapping.class));
      all.add(A(PomDelegate.class));

      all.add(A(WebAppPomBuilder.class));
      all.add(A(WebAppWizardBuilder.class));
      
      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }
}
