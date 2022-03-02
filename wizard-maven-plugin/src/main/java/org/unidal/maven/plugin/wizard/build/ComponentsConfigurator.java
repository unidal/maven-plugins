package org.unidal.maven.plugin.wizard.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.aggregator.DefaultXmlAggregator;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.generator.XslGenerator;
import org.unidal.codegen.manifest.ManifestParser;
import org.unidal.codegen.transformer.XslTransformer;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.maven.plugin.pom.PomDelegate;
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

      all.add(A(JdbcPomBuilder.class));
      all.add(A(JdbcWizardBuilder.class));

      all.add(C(XmlAggregator.class, "wizard-jdbc", DefaultXmlAggregator.class) //
            .config(E("structureFile").value("META-INF/wizard/jdbc/structure.xml")));
      all.add(C(Generator.class, "wizard-jdbc", XslGenerator.class) //
            .req(XslTransformer.class, ManifestParser.class) //
            .req(XmlAggregator.class, "wizard-jdbc"));

      return all;
   }

   private List<Component> defineModelComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(ModelPomBuilder.class));
      all.add(A(ModelWizardBuilder.class));

      return all;
   }

   private List<Component> defineWebappComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(WebAppPomBuilder.class));
      all.add(A(WebAppWizardBuilder.class));

      all.add(C(XmlAggregator.class, "wizard-webapp", DefaultXmlAggregator.class) //
            .config(E("structureFile").value("META-INF/wizard/webapp/structure.xml")));
      all.add(C(Generator.class, "wizard-webapp", XslGenerator.class) //
            .req(XslTransformer.class, ManifestParser.class) //
            .req(XmlAggregator.class, "wizard-webapp"));

      return all;
   }

   @Override
   protected boolean isMavenPlugin() {
      return true;
   }
}
