<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
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
import org.unidal.web.mvc.ViewModel;
<xsl:variable name="type">
	<xsl:value-of select="../@page-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@action-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@context-class"/>
</xsl:variable>
public class <xsl:value-of select="@model-class"/> extends ViewModel<xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> {
	public <xsl:value-of select="@model-class"/>(Context ctx) {
		super(ctx);
	}

	@Override
	public <xsl:value-of select="@action-class"/> getDefaultAction() {
		return <xsl:value-of select="@action-class"/>.VIEW;
	}
}
</xsl:template>

</xsl:stylesheet>
