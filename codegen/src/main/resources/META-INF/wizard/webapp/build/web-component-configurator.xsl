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
<xsl:for-each select="module">
import <xsl:value-of select="@package"/>.<xsl:value-of select="@module-class"/>;<xsl:value-of select="$empty"/>
</xsl:for-each>

import com.site.lookup.configuration.Component;
import com.site.web.configuration.AbstractWebComponentsConfigurator;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
		List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();

		defineModuleRegistry(all, <xsl:value-of select="$empty"/>
        <xsl:choose>
           <xsl:when test="module[@default='true']"><xsl:value-of select="module[@default='true']/@module-class"/>.class</xsl:when>
           <xsl:when test="module"><xsl:value-of select="module/@module-class"/>.class</xsl:when>
           <xsl:otherwise>null</xsl:otherwise>
        </xsl:choose>
        <xsl:for-each select="module">
           <xsl:value-of select="$empty"/>, <xsl:value-of select="@module-class"/>.class<xsl:value-of select="$empty"/>
        </xsl:for-each>
        <xsl:value-of select="$empty"/>);

		return all;
	}
}
</xsl:template>

</xsl:stylesheet>
