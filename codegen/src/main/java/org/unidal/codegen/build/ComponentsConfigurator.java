package org.unidal.codegen.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.aggregator.DefaultXmlAggregator;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.generator.XslGenerator;
import org.unidal.codegen.manifest.DefaultManifestCreator;
import org.unidal.codegen.manifest.DefaultManifestParser;
import org.unidal.codegen.manifest.ManifestCreator;
import org.unidal.codegen.manifest.ManifestParser;
import org.unidal.codegen.meta.DefaultModelMeta;
import org.unidal.codegen.meta.DefaultTableMeta;
import org.unidal.codegen.meta.DefaultWizardMeta;
import org.unidal.codegen.meta.DefaultXmlMeta;
import org.unidal.codegen.meta.DefaultXmlMetaHelper;
import org.unidal.codegen.meta.ModelMeta;
import org.unidal.codegen.meta.TableMeta;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.codegen.meta.XmlMeta;
import org.unidal.codegen.meta.XmlMetaHelper;
import org.unidal.codegen.template.DefaultXslTemplateManager;
import org.unidal.codegen.template.XslTemplateManager;
import org.unidal.codegen.transformer.DefaultXslTransformer;
import org.unidal.codegen.transformer.XslTransformer;
import com.site.lookup.configuration.AbstractResourceConfigurator;
import com.site.lookup.configuration.Component;

class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(TableMeta.class, DefaultTableMeta.class));
		all.add(C(XmlMeta.class, DefaultXmlMeta.class));
		all.add(C(ModelMeta.class, DefaultModelMeta.class));
		all.add(C(WizardMeta.class, DefaultWizardMeta.class));
		all.add(C(XmlMetaHelper.class, DefaultXmlMetaHelper.class) //
				.req(XmlMeta.class));
		all.add(C(ManifestCreator.class, DefaultManifestCreator.class));

		all.add(C(XslTemplateManager.class, DefaultXslTemplateManager.class));
		all.add(C(XslTransformer.class, DefaultXslTransformer.class) //
				.req(XslTemplateManager.class));
		all.add(C(ManifestParser.class, DefaultManifestParser.class));

		all.add(C(XmlAggregator.class, "dal-jdbc", DefaultXmlAggregator.class) //
				.config(E("structureFile").value("META-INF/dal/jdbc/structure.xml")));
		all.add(C(Generator.class, "dal-jdbc", XslGenerator.class).is(PER_LOOKUP) //
				.req(XslTransformer.class, ManifestParser.class) //
				.req(XmlAggregator.class, "dal-jdbc"));

		all.add(C(XmlAggregator.class, "dal-xml", DefaultXmlAggregator.class) //
				.config(E("structureFile").value("META-INF/dal/xml/structure.xml")));
		all.add(C(Generator.class, "dal-xml", XslGenerator.class).is(PER_LOOKUP) //
				.req(XslTransformer.class, ManifestParser.class) //
				.req(XmlAggregator.class, "dal-xml"));
		
		all.add(C(XmlAggregator.class, "dal-model", DefaultXmlAggregator.class) //
				.config(E("structureFile").value("META-INF/dal/model/structure.xml")));
		all.add(C(Generator.class, "dal-model", XslGenerator.class).is(PER_LOOKUP) //
				.req(XslTransformer.class, ManifestParser.class) //
				.req(XmlAggregator.class, "dal-model"));

		all.add(C(XmlAggregator.class, "wizard-webapp", DefaultXmlAggregator.class) //
				.config(E("structureFile").value("META-INF/wizard/webapp/structure.xml")));
		all.add(C(Generator.class, "wizard-webapp", XslGenerator.class).is(PER_LOOKUP) //
				.req(XslTransformer.class, ManifestParser.class) //
				.req(XmlAggregator.class, "wizard-webapp"));
		
		all.add(C(XmlAggregator.class, "wizard-jdbc", DefaultXmlAggregator.class) //
		      .config(E("structureFile").value("META-INF/wizard/jdbc/structure.xml")));
		all.add(C(Generator.class, "wizard-jdbc", XslGenerator.class).is(PER_LOOKUP) //
		      .req(XslTransformer.class, ManifestParser.class) //
		      .req(XmlAggregator.class, "wizard-jdbc"));
		
		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
