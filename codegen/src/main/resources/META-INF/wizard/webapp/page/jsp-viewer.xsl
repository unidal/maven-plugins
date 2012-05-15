<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="module"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$module]/page[@name=$name]"/>
</xsl:template>

<xsl:template match="page">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
import com.site.web.mvc.view.BaseJspViewer;
<xsl:variable name="type">
	<xsl:value-of select="../@page-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@action-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@context-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@model-class"/>
</xsl:variable>
public class <xsl:value-of select="@jsp-viewer-class"/> extends BaseJspViewer<xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> {
	@Override
	protected String getJspFilePath(<xsl:value-of select="@context-class"/> ctx, <xsl:value-of select="@model-class"/> model) {
		<xsl:value-of select="@action-class"/> action = model.getAction();

		switch (action) {
		case VIEW:
			return <xsl:value-of select="@jsp-file-class"/>.VIEW.getPath();
		}

		throw new RuntimeException("Unknown action: " + action);
	}
}
</xsl:template>

</xsl:stylesheet>
