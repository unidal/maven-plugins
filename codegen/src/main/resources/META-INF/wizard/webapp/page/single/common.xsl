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

<xsl:template name="lt">
   <xsl:text disable-output-escaping="yes">&lt;</xsl:text>
</xsl:template>

<xsl:template name="gt">
   <xsl:text disable-output-escaping="yes">&gt;</xsl:text>
</xsl:template>

</xsl:stylesheet>
