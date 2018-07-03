<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp"/>
</xsl:template>

<xsl:template match="webapp">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
		List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}
</xsl:template>

</xsl:stylesheet>
