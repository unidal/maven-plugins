package org.unidal.maven.plugin.wizard.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.meta.DefaultModelMeta;
import org.unidal.maven.plugin.wizard.meta.DefaultTableMeta;
import org.unidal.maven.plugin.wizard.meta.DefaultWizardMeta;
import org.unidal.maven.plugin.wizard.meta.JdbcWizardBuilder;
import org.unidal.maven.plugin.wizard.meta.ModelWizardBuilder;
import org.unidal.maven.plugin.wizard.meta.WebAppWizardBuilder;
import org.unidal.maven.plugin.wizard.pom.JdbcPomBuilder;
import org.unidal.maven.plugin.wizard.pom.ModelPomBuilder;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(PomDelegate.class));

      all.addAll(defineModelComponents());
      all.addAll(defineJdbcComponents());
      all.addAll(defineWebappComponents());

      return all;
   }

   private List<Component> defineJdbcComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultTableMeta.class));
      all.add(A(JdbcPomBuilder.class));
      all.add(A(JdbcWizardBuilder.class));

      return all;
   }

   private List<Component> defineModelComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultModelMeta.class));
      all.add(A(ModelPomBuilder.class));
      all.add(A(ModelWizardBuilder.class));

      return all;
   }

   private List<Component> defineWebappComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultWizardMeta.class));
      all.add(A(WebAppPomBuilder.class));
      all.add(A(WebAppWizardBuilder.class));

      return all;
   }

   @Override
   protected boolean isMavenPlugin() {
      return true;
   }
}
