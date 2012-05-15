<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public interface IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-build-children'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.<xsl:value-of select='entity/any/@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-build-children">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public Any <xsl:value-of select="entity/any/@build-method"/>(T node);<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity | entity/element[(@list='true' or @set='true') and not(@render='false')]">
      <xsl:sort select="@build-method"/>

      <xsl:choose>
         <xsl:when test="name()='element'">
            <xsl:variable name="build-method" select="@build-method"/>
            
            <xsl:if test="generate-id(//entity/element[@build-method=$build-method][(@list='true' or @set='true') and not(@render='false')][1])=generate-id()">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(T node);<xsl:value-of select="$empty-line"/>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(T node);<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
