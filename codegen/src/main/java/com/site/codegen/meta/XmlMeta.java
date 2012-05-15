package com.site.codegen.meta;

import java.io.Reader;

import org.jdom.Element;

public interface XmlMeta {
   public Element getMeta(Reader reader);
}
