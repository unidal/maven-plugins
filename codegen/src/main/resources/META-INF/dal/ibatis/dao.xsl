<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/entities/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:variable name="package" select="@do-package"/>
   <xsl:if test="$package">
      <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public interface <xsl:value-of select='@dao-class'/> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="method-query"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="relation">
      <xsl:sort select="@name"/>
      
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      <xsl:if test="generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
        <xsl:if test="../@do-package != $entity/@do-package">
            <xsl:value-of select='$empty'/>import <xsl:value-of select="$entity/@do-package"/>.<xsl:value-of select="$entity/@do-class"/>;<xsl:value-of select="$empty-line"/>
        </xsl:if>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-query">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="$empty"/>
      <xsl:choose>
         <xsl:when test="@type='SELECT'"><xsl:value-of select="$entity/@do-class" /></xsl:when>
         <xsl:otherwise>void</xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$space"/><xsl:value-of select='@name'/>(<xsl:value-of select="$empty"/>
      <xsl:choose>
         <xsl:when test="@type='SELECT' or @type='DELETE'"><xsl:value-of select="$entity/member[@key='true']/@value-type"/><xsl:value-of select="$space" /><xsl:value-of select="$entity/member[@key='true']/@param-name"/></xsl:when>
         <xsl:otherwise><xsl:value-of select="$entity/@do-class" /><xsl:value-of select="$space" /><xsl:value-of select="$entity/@param-name" /></xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
