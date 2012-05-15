package com.site.codegen.meta;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.jdom.Document;
import org.jdom.Element;

public interface TableMeta {
   public Element getTableMeta(DatabaseMetaData meta, String table) throws SQLException;

	public Document getModel(String packageName);
	
	public Document getManifest(String codegenXml, String modelXml);
}
