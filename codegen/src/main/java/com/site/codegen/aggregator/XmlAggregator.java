package com.site.codegen.aggregator;

import java.io.File;
import java.net.URL;

public interface XmlAggregator {
	public String aggregate(File manifestXml);

   public String aggregate(URL manifestXml);
}
