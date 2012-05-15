package com.site.codegen.meta;

import java.io.Reader;

import org.jdom.Document;

public interface WizardMeta {
	public Document getWizard(Reader reader);

	public Document getManifest(String wizardXml);
}
