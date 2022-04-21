<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="src-main-java"/>
<xsl:param name="src-main-resources"/>
<xsl:param name="src-test-java"/>
<xsl:param name="src-test-resources"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="outputs">
      <xsl:apply-templates select="/model"/>
      <xsl:apply-templates select="/model/entity"/>
   </xsl:element>
</xsl:template>

<xsl:template match="model">
	 <xsl:call-template name="copy-resources">
	   <xsl:with-param name="source" select="'meta.xml'"/>
	   <xsl:with-param name="target" select="'meta.xml'"/>
	 </xsl:call-template>
</xsl:template>

<xsl:template match="entity">
   <xsl:call-template name="generate-java">
      <xsl:with-param name="template" select="'entity.xsl'"/>
      <xsl:with-param name="name" select="@name"/>
   </xsl:call-template>
</xsl:template>


<xsl:template name="generate-java">
   <xsl:param name="template"/>
   <xsl:param name="name"/>
   <xsl:param name="mode" select="'create_or_overwrite'"/>

    <xsl:element name="output">
       <xsl:attribute name="op">apply_template</xsl:attribute>
       <xsl:attribute name="path"><xsl:value-of select="$name"/>.java</xsl:attribute>
       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
       <xsl:element name="property">
          <xsl:attribute name="name">name</xsl:attribute>
          <xsl:value-of select="$name"/>
       </xsl:element>
  </xsl:element>
</xsl:template>

<xsl:template name="copy-resources">
   <xsl:param name="source"/>
   <xsl:param name="target"/>
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:element name="output">
       <xsl:attribute name="op">copy_resources</xsl:attribute>
       <xsl:attribute name="path"><xsl:value-of select="$target"/></xsl:attribute>
       <xsl:attribute name="template"><xsl:value-of select="$source"/></xsl:attribute>
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
   </xsl:element>
</xsl:template>

</xsl:stylesheet>