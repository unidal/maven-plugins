package com.site.codegen;

import java.util.ArrayList;
import java.util.List;

import com.site.codegen.aggregator.DefaultXmlAggregator;
import com.site.codegen.aggregator.XmlAggregator;
import com.site.codegen.generator.Generator;
import com.site.codegen.generator.XslGenerator;
import com.site.codegen.manifest.DefaultManifestCreator;
import com.site.codegen.manifest.DefaultManifestParser;
import com.site.codegen.manifest.ManifestCreator;
import com.site.codegen.manifest.ManifestParser;
import com.site.codegen.meta.DefaultModelMeta;
import com.site.codegen.meta.DefaultTableMeta;
import com.site.codegen.meta.DefaultWizardMeta;
import com.site.codegen.meta.DefaultXmlMeta;
import com.site.codegen.meta.DefaultXmlMetaHelper;
import com.site.codegen.meta.ModelMeta;
import com.site.codegen.meta.TableMeta;
import com.site.codegen.meta.WizardMeta;
import com.site.codegen.meta.XmlMeta;
import com.site.codegen.meta.XmlMetaHelper;
import com.site.codegen.template.DefaultXslTemplateManager;
import com.site.codegen.template.XslTemplateManager;
import com.site.codegen.transformer.DefaultXslTransformer;
import com.site.codegen.transformer.XslTransformer;
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
