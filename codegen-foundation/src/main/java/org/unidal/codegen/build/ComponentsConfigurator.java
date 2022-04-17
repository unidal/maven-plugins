package org.unidal.codegen.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.aggregator.DefaultXmlAggregator;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.code.DefaultObfuscater;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.generator.XslGenerator;
import org.unidal.codegen.manifest.DefaultManifestCreator;
import org.unidal.codegen.manifest.DefaultManifestParser;
import org.unidal.codegen.manifest.ManifestParser;
import org.unidal.codegen.template.DefaultXslTemplateManager;
import org.unidal.codegen.transformer.DefaultXslTransformer;
import org.unidal.codegen.transformer.XslTransformer;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

class ComponentsConfigurator extends AbstractResourceConfigurator {
   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new ComponentsConfigurator());
   }

   @Override
   public List<Component> defineComponents() {
      List<Component> all = new ArrayList<Component>();

      all.add(A(DefaultObfuscater.class));
      all.add(A(DefaultManifestCreator.class));
      all.add(A(DefaultXslTemplateManager.class));
      all.add(A(DefaultXslTransformer.class));
      all.add(A(DefaultManifestParser.class));

      all.add(C(XmlAggregator.class, "dal-jdbc", DefaultXmlAggregator.class) //
            .config(E("structureFile").value("META-INF/dal/jdbc/structure.xml")));
      all.add(C(Generator.class, "dal-jdbc", XslGenerator.class).is(PER_LOOKUP) //
            .req(XslTransformer.class, ManifestParser.class) //
            .req(XmlAggregator.class, "dal-jdbc"));
      all.add(C(Generator.class, "dal-ibatis", XslGenerator.class).is(PER_LOOKUP) //
            .req(XslTransformer.class, ManifestParser.class) //
            .req(XmlAggregator.class, "dal-jdbc"));

      all.add(C(XmlAggregator.class, "dal-model", DefaultXmlAggregator.class) //
            .config(E("structureFile").value("META-INF/dal/model/structure.xml")));
      all.add(C(Generator.class, "dal-model", XslGenerator.class).is(PER_LOOKUP) //
            .req(XslTransformer.class, ManifestParser.class) //
            .req(XmlAggregator.class, "dal-model"));


      return all;
   }

	@Override
	protected boolean isMavenPlugin() {
		return true;
	}
}
