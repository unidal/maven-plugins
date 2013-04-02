<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../../common.xsl"/>

<xsl:template name="single-policy">
   <xsl:param name="name"/>
   <xsl:param name="node" select="single"/>
   <xsl:param name="default" select="'true'"/>
   
   <xsl:variable name="enable-policy" select="$node/attribute::*[name()=concat('enable-', $name)]"/>
   <xsl:variable name="disable-policy" select="$node/attribute::*[name()=concat('disable-', $name)]"/>
   <xsl:choose>
      <xsl:when test="$disable-policy">
         <xsl:value-of select="not($disable-policy='true')"/>
      </xsl:when>
      <xsl:when test="$enable-policy">
         <xsl:value-of select="$enable-policy='true'"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$default"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="directive">
   <xsl:param name="body"/>
   
   <xsl:text disable-output-escaping="yes">&lt;%@ </xsl:text>
   <xsl:value-of select="$body" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes"> %&gt;</xsl:text>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="tag">
   <xsl:param name="body"/>
   
   <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
   <xsl:value-of select="$body" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">/&gt;</xsl:text>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="element">
   <xsl:param name="indent" select="''"/>
   <xsl:param name="name"/>
   <xsl:param name="attributes" select="''"/>
   <xsl:param name="body" select="''"/>
   <xsl:param name="last" select="false()"/>
   
   <xsl:if test="$indent">
      <xsl:value-of select="$empty-line"/><xsl:value-of select="$indent"/>
   </xsl:if>
   <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
   <xsl:value-of select="$name" disable-output-escaping="yes"/>
   <xsl:value-of select="$attributes" disable-output-escaping="yes"/>
   <xsl:choose>
      <xsl:when test="$body">
         <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
         <xsl:value-of select="$body" disable-output-escaping="yes"/>
         <xsl:text disable-output-escaping="yes">&lt;/</xsl:text>
         <xsl:value-of select="$name" disable-output-escaping="yes"/>
         <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
      </xsl:when>
      <xsl:otherwise>
         <xsl:text disable-output-escaping="yes">/&gt;</xsl:text>
      </xsl:otherwise>
   </xsl:choose>
   <xsl:if test="$indent and $last">
      <xsl:value-of select="$empty-line"/><xsl:value-of select="substring($indent, 4)"/>
   </xsl:if>
</xsl:template>

<xsl:template name="lt">
   <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
</xsl:template>

<xsl:template name="gt">
   <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
</xsl:template>

<xsl:template name="gt2">
   <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
   <xsl:value-of select="$empty-line"/>
</xsl:template>


<xsl:template name="amp">
   <xsl:text disable-output-escaping="yes">&amp;</xsl:text>
</xsl:template>

</xsl:stylesheet>
