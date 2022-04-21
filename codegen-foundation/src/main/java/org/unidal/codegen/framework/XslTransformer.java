package org.unidal.codegen.framework;

import java.net.URL;
import java.util.Map;

import javax.xml.transform.TransformerException;

public interface XslTransformer {
   public String transform(URL template, String source, Map<String, String> parameters) throws TransformerException;
}
