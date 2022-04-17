package org.unidal.maven.plugin.wizard.meta;

import java.io.Reader;

import org.jdom.Document;

public interface WizardMeta {
	public Document getWizard(Reader reader);

	public Document getManifest(String wizardXml);
}
