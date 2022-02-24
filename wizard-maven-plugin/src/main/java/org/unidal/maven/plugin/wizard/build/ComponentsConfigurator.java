package org.unidal.maven.plugin.wizard.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.maven.plugin.pom.MavenContainer;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.pom.VersionMapping;
import org.unidal.maven.plugin.wizard.meta.WebAppWizardBuilder;
import org.unidal.maven.plugin.wizard.pom.WebAppPomBuilder;

class ComponentsConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}

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

	@Override
	protected boolean isMavenPlugin() {
		return true;
	}
}
