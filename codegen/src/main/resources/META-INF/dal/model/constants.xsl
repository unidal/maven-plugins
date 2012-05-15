<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
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
   <xsl:value-of select="$empty"/>public class Constants {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='constant-attributes'/>
   <xsl:call-template name='constant-elements'/>
   <xsl:call-template name='constant-entities'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="constant-attributes">
   <xsl:for-each select="entity/attribute[not(@text='true' or @render='false')]">
      <xsl:sort select="@name"/>

      <xsl:variable name="name" select="@name"/>
      <xsl:if test="generate-id(//entity/attribute[not(@text='true' or @render='false')][@name=$name][1])=generate-id()">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   public static final String <xsl:value-of select="@upper-name"/> = "<xsl:value-of select="@name"/>";<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="constant-elements">
   <xsl:for-each select="entity/element[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:variable name="upper-name-element" select="@upper-name-element"/>
      <xsl:if test="generate-id(//entity/element[not(@render='false')][@upper-name=$upper-name or @upper-name-element=$upper-name-element][1])=generate-id()">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   public static final String <xsl:value-of select="@upper-name-element"/> = "<xsl:value-of select="@name"/>";<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="generate-id(//entity/element[(@list='true' or @set='true') and not(@render='false')][@upper-name-element=$upper-name-element][1])=generate-id()">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   public static final String <xsl:value-of select="@upper-name"/> = "<xsl:value-of select="@tag-name"/>";<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="constant-entities">
   <xsl:for-each select="entity | entity/entity-ref[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id((//entity | //entity/entity-ref[not(@render='false')])[@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   public static final String <xsl:value-of select="@upper-name"/> = "<xsl:value-of select="@tag-name"/>";<xsl:value-of select="$empty-line"/>
         <xsl:if test="name()='entity-ref' and (@list='true' or @map='true')" >
            <xsl:variable name="upper-names" select="@upper-names"/>
            <xsl:if test="not(//entity/entity-ref[@upper-name=$upper-names])">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   public static final String <xsl:value-of select="@upper-names"/> = "<xsl:value-of select="@tag-names"/>";<xsl:value-of select="$empty-line"/>
            </xsl:if>
         </xsl:if>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
