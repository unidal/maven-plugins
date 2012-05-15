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

import java.io.IOException;

import javax.servlet.ServletException;

import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
import com.site.lookup.annotation.Inject;
import com.site.web.mvc.PageHandler;
import com.site.web.mvc.annotation.InboundActionMeta;
import com.site.web.mvc.annotation.OutboundActionMeta;
import com.site.web.mvc.annotation.PayloadMeta;

public class <xsl:value-of select="@handler-class"/> implements PageHandler<xsl:call-template name="generic-type"><xsl:with-param name="type" select="@context-class"/></xsl:call-template> {
	@Inject
	private <xsl:value-of select="@jsp-viewer-class"/> m_jspViewer;

	@Override
	@PayloadMeta(<xsl:value-of select="@payload-class"/>.class)
	@InboundActionMeta(name = "<xsl:value-of select="@path"/>")
	public void handleInbound(<xsl:value-of select="@context-class"/> ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "<xsl:value-of select="@path"/>")
	public void handleOutbound(<xsl:value-of select="@context-class"/> ctx) throws ServletException, IOException {
		<xsl:value-of select="@model-class"/> model = new <xsl:value-of select="@model-class"/>(ctx);

		model.setAction(Action.VIEW);
		model.setPage(<xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>);
		m_jspViewer.view(ctx, model);
	}
}
</xsl:template>

</xsl:stylesheet>
