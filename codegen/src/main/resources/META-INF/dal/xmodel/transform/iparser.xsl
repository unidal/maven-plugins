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
   <xsl:value-of select="$empty"/>public interface IParser<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-parse-entities'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-parse-entities">
   <xsl:for-each select="entity[@root='true']">
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/> parse(IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> maker, ILinker linker, T node);<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:for-each select="entity[not(@root='true')]">
      <xsl:sort select="@build-method"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void parseFor<xsl:value-of select="@entity-class"/>(IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> maker, ILinker linker, <xsl:value-of select="@entity-class"/> parent, T node);<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
