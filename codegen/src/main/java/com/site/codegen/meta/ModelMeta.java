package com.site.codegen.meta;

import java.io.Reader;

import org.jdom.Document;

public interface ModelMeta {
	public Document getCodegen(Reader reader);

	public Document getModel(String packageName);
	
	public Document getManifest(String codegenXml, String modelXml);
}
