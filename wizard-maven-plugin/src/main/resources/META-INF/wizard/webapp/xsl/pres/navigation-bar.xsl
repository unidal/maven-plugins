<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
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

<xsl:for-each select="module">
   <xsl:value-of select="$empty"/>import <xsl:value-of select="@package"/>.<xsl:value-of select="@page-class"/>;
</xsl:for-each>
<xsl:value-of select="$empty"/>import org.unidal.web.mvc.Page;

public class NavigationBar {
   public Page[] getVisiblePages() {
      return new Page[] {
   <xsl:value-of select="$empty-line"/>
<xsl:for-each select="module/page">
   <xsl:value-of select="'      '"/><xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>
   <xsl:if test="position()!=last()">,</xsl:if>
   <xsl:value-of select="$empty-line"/>
</xsl:for-each>
		};
   }
}
</xsl:template>

</xsl:stylesheet>
