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
import com.site.web.mvc.ActionContext;
import com.site.web.mvc.ActionPayload;
import com.site.web.mvc.payload.annotation.FieldMeta;
<xsl:variable name="type">
	<xsl:value-of select="../@page-class"/>
	<xsl:value-of select="', '"/>
	<xsl:value-of select="@action-class"/>
</xsl:variable>
public class <xsl:value-of select="@payload-class"/> implements ActionPayload<xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> {
	private <xsl:value-of select="../@page-class"/> m_page;

	@FieldMeta("op")
	private <xsl:value-of select="@action-class"/> m_action;

	public void setAction(<xsl:value-of select="@action-class"/> action) {
		m_action = action;
	}

	@Override
	public <xsl:value-of select="@action-class"/> getAction() {
		return m_action;
	}

	@Override
	public <xsl:value-of select="../@page-class"/> getPage() {
		return m_page;
	}

	@Override
	public void setPage(String page) {
		m_page = <xsl:value-of select="../@page-class"/>.getByName(page, <xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>);
	}

	@Override
	public void validate(ActionContext<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'?'"/></xsl:call-template> ctx) {
	}
}
</xsl:template>

</xsl:stylesheet>
