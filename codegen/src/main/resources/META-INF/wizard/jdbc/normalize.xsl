<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard"/>
</xsl:template>

<xsl:template match="wizard">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:attribute name="build-package">
         <xsl:value-of select="@package"/>
         <xsl:value-of select="'.build'"/>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="datasources">
   <xsl:copy>
      <xsl:copy-of select="@*"/>

      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="datasource">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:variable name="name">
      	<xsl:value-of select="@name"/>
      </xsl:variable>
      <xsl:variable name="normalized-name">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="$name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:attribute name="do-package">
         <xsl:choose>
            <xsl:when test="@do-package"><xsl:value-of select="@do-package"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../../@package"/>.dal.<xsl:value-of select="$normalized-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="configurator-class">
         <xsl:choose>
            <xsl:when test="@configurator-class"><xsl:value-of select="@configurator-class"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/>DatabaseConfigurator</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
   <xsl:copy>
      <xsl:copy-of select="@*" />
      
      <xsl:if test="node()">
         <xsl:apply-templates />
      </xsl:if>
   </xsl:copy>
</xsl:template>

</xsl:stylesheet>