<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="xs-namespace" select="'http://www.w3.org/2001/XMLSchema'"/>
<xsl:variable name="xsi-namespace" select="'http://www.w3.org/2001/XMLSchema-instance'"/>

<xsl:template match="/">
   <xsl:call-template name="entity">
      <xsl:with-param name="entity" select="/model/entity[@root='true']"/>
   </xsl:call-template>
</xsl:template>

<xsl:template match="entity-ref">
   <xsl:variable name="name" select="@name"/>
   <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
   <xsl:choose>
      <xsl:when test="(@list='true' or @map='true') and @xml-indent='true'">
         <xsl:element name="{@tag-name}">
            <xsl:call-template name="entity">
               <xsl:with-param name="entity" select="$entity"/>
            </xsl:call-template>
	        <xsl:comment> more <xsl:value-of select="@name"/><xsl:value-of select="$space"/></xsl:comment>
         </xsl:element>
      </xsl:when>
      <xsl:when test="@list='true' or @map='true'">
         <xsl:call-template name="entity">
            <xsl:with-param name="entity" select="$entity"/>
         </xsl:call-template>
         <xsl:comment> more <xsl:value-of select="@name"/><xsl:value-of select="$space"/></xsl:comment>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="entity">
            <xsl:with-param name="entity" select="$entity"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template match="element">
   <xsl:choose>
      <xsl:when test="(@list='true' or @set='true') and @xml-indent='true'">
         <xsl:element name="{@tag-name}">
            <xsl:element name="{@ref-name}">{<xsl:value-of select="@name"/>}</xsl:element>
            <xsl:comment> more <xsl:value-of select="@name"/><xsl:value-of select="$space"/></xsl:comment>
         </xsl:element>
      </xsl:when>
      <xsl:when test="@list='true' or @set='true'">
         <xsl:element name="{@ref-name}">{<xsl:value-of select="@name"/>}</xsl:element>
         <xsl:comment> more <xsl:value-of select="@name"/><xsl:value-of select="$space"/></xsl:comment>
      </xsl:when>
      <xsl:otherwise>
         <xsl:element name="{@ref-name}">{<xsl:value-of select="@name"/>}</xsl:element>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="entity">
   <xsl:param name="entity" select="."/>
   
   <xsl:element name="{$entity/@ref-name}">
      <xsl:if test="$entity/@root='true'">
         <xsl:attribute name="xsi:noNamespaceSchemaLocation" namespace="{$xsi-namespace}"><xsl:value-of select="$entity/@name"/>.xsd</xsl:attribute>
      </xsl:if>
      <xsl:for-each select="$entity/attribute[@required='true' and not(@text='true')]">
         <xsl:attribute name="{@ref-name}">
            <xsl:choose>
               <xsl:when test="@value-type='Boolean'">false</xsl:when>
               <xsl:when test="@value-type='Integer'">0</xsl:when>
               <xsl:when test="@value-type='Long'">0</xsl:when>
               <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
         </xsl:attribute>
      </xsl:for-each>
      <xsl:apply-templates select="$entity/element[(@required='true' or @list='true' or @set='true') and not(@render='false' or @text='true')]"/>
      <xsl:apply-templates select="$entity/entity-ref[@name!=$entity/@name and not(@render='false')]"/>
   </xsl:element>
</xsl:template>

</xsl:stylesheet>
