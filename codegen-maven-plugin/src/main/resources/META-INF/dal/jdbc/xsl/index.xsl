<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:value-of select="$empty"/>package <xsl:value-of select="/entities/@do-package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class _INDEX {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="get-classes">
      <xsl:with-param name="method" select="'getEntityClasses'"/>
      <xsl:with-param name="type" select="'Entity'"/>
   </xsl:call-template>
   <xsl:call-template name="get-classes">
      <xsl:with-param name="method" select="'getDaoClasses'"/>
      <xsl:with-param name="type" select="'Dao'"/>
   </xsl:call-template>
   <xsl:call-template name="get-classes">
      <xsl:with-param name="method" select="'getDoClasses'"/>
      <xsl:with-param name="type" select="'Do'"/>
   </xsl:call-template>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:variable name="do-package" select="concat(//entity/@do-package, '')"/>
   <xsl:if test="//entity[concat(@do-package, '')!=$do-package]">
      <xsl:for-each select="//entity[concat(@do-package, '')!=$do-package]">
         <xsl:sort select="@class-name"/>
         
         <xsl:value-of select="$empty"/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@do-class"/>;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@dao-class"/>;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@entity-class"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="get-classes">
   <xsl:param name="method" />
   <xsl:param name="type" />

   <xsl:value-of select="$empty"/>   public static Class<xsl:value-of select="$empty"/>
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type">?</xsl:with-param>
   </xsl:call-template>
   <xsl:value-of select="$empty"/>[] <xsl:value-of select="$method"/>() {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      return new Class<xsl:value-of select="$empty"/>
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type">?</xsl:with-param>
   </xsl:call-template>
   <xsl:value-of select="$empty"/>[] { <xsl:value-of select="$empty"/>
   <xsl:for-each select="/entities/entity[not(@gen='false')]">
      <xsl:if test="position() != 1">, </xsl:if>
      <xsl:choose>
         <xsl:when test="$type='Entity'"><xsl:value-of select="@entity-class"/></xsl:when>
         <xsl:when test="$type='Dao'"><xsl:value-of select="@dao-class"/></xsl:when>
         <xsl:when test="$type='Do'"><xsl:value-of select="@do-class"/></xsl:when>
      </xsl:choose>
      <xsl:value-of select="'.class'"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/> };<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

</xsl:stylesheet>
