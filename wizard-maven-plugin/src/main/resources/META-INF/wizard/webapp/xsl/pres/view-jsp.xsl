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
<xsl:call-template name="directive"><xsl:with-param name="body">page contentType="text/html; charset=utf-8" isELIgnored="false" trimDirectiveWhitespaces="true"</xsl:with-param></xsl:call-template>
<xsl:if test="/wizard/webapp/@layout='bootstrap'">
<xsl:call-template name="directive"><xsl:with-param name="body">taglib prefix="a" uri="<xsl:value-of select="/wizard/webapp/@app-tld"/>"</xsl:with-param></xsl:call-template>
</xsl:if>
<xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="ctx" type="<xsl:value-of select="$package"/>.Context" scope="request"</xsl:with-param></xsl:call-template>
<xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="payload" type="<xsl:value-of select="$package"/>.Payload" scope="request"</xsl:with-param></xsl:call-template>
<xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="model" type="<xsl:value-of select="$package"/>.Model" scope="request"</xsl:with-param></xsl:call-template>

<xsl:choose>
   <xsl:when test="/wizard/webapp/@layout='bootstrap'">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>a:layout<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/><xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   View of <xsl:value-of select="$name"/> page under <xsl:value-of select="$module"/><xsl:value-of select="$empty-line"/>
      <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>/a:layout<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
   </xsl:when>
   <xsl:otherwise>
      <xsl:value-of select="$empty"/>View of <xsl:value-of select="$name"/> page under <xsl:value-of select="$module"/>
   </xsl:otherwise>
</xsl:choose>

</xsl:template>

<xsl:template name="directive">
   <xsl:param name="body"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;%@ </xsl:text>
   <xsl:value-of select="$body" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes"> %&#x3e;</xsl:text>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="tag">
   <xsl:param name="body"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$body" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">/&#x3e;</xsl:text>
   <xsl:value-of select="$empty-line"/>
</xsl:template>
</xsl:stylesheet>