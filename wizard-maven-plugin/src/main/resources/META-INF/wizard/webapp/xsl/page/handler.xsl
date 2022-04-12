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
<xsl:variable name="is-jsp" select="//webapp/@language='jsp'"/>
<xsl:variable name="is-th" select="//webapp/@language='thymeleaf'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$module]/page[@name=$name]"/>
</xsl:template>

<xsl:template match="page">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
<xsl:if test="$is-th">
import org.unidal.lookup.annotation.Inject;<xsl:value-of select="$empty-line"/>
</xsl:if>import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;
<xsl:if test="$is-th">
import org.unidal.web.mvc.view.HtmlTemplate;
</xsl:if>
@Named
public class <xsl:value-of select="@handler-class"/> implements PageHandler<xsl:call-template name="generic-type"><xsl:with-param name="type" select="@context-class"/></xsl:call-template> {
<xsl:choose>
   <xsl:when test="$is-jsp">
	@Inject
	private <xsl:value-of select="@jsp-viewer-class"/> m_jspViewer;
   </xsl:when>
   <xsl:when test="$is-th">
	@Inject
	private HtmlTemplate m_htmlTemplate;
   </xsl:when>
</xsl:choose>
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

		if (!ctx.isProcessStopped()) {<xsl:value-of select="$empty"/>
<xsl:choose>
   <xsl:when test="$is-jsp">
		   m_jspViewer.view(ctx, model);<xsl:value-of select="$empty"/>
   </xsl:when>
   <xsl:when test="$is-th">
		   m_htmlTemplate.render("<xsl:value-of select="../@path"/>/<xsl:value-of select="@path"/>", ctx, model);<xsl:value-of select="$empty"/>
   </xsl:when>
   <xsl:otherwise>
		   ctx.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request!");<xsl:value-of select="$empty"/>
   </xsl:otherwise>
</xsl:choose>
		}
	}
}
</xsl:template>

</xsl:stylesheet>
