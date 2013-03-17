<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="base-dir"/>
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
   <xsl:element name="manifest">
      <xsl:apply-templates select="/model"/>
   </xsl:element>
</xsl:template>

<xsl:template match="model">
   <xsl:variable name="package" select="@model-package"/>
   
   <!-- Entity.h -->
   <xsl:call-template name="generate-c">
     <xsl:with-param name="template" select="'entity/entity_h.xsl'"/>
     <xsl:with-param name="file" select="'Entity.h'"/>
   </xsl:call-template>
   
   <!-- Entity.m -->
   <xsl:call-template name="generate-c">
     <xsl:with-param name="template" select="'entity/entity_m.xsl'"/>
     <xsl:with-param name="file" select="'Entity.m'"/>
   </xsl:call-template>
   
   <!-- XmlBuilder.h -->
   <xsl:call-template name="generate-c">
     <xsl:with-param name="template" select="'transform/xml-builder_h.xsl'"/>
     <xsl:with-param name="file" select="'XmlBuilder.h'"/>
   </xsl:call-template>
   
   <!-- XmlBuilder.m -->
   <xsl:call-template name="generate-c">
     <xsl:with-param name="template" select="'transform/xml-builder_m.xsl'"/>
     <xsl:with-param name="file" select="'XmlBuilder.m'"/>
   </xsl:call-template>
   
   <xsl:variable name="policy-sax-parser">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'sax-parser'"/>
      </xsl:call-template>
   </xsl:variable>
   
   <xsl:if test="$policy-sax-parser='true'">
      <!-- XmlParser.h -->
      <xsl:call-template name="generate-c">
        <xsl:with-param name="template" select="'transform/xml-parser_h.xsl'"/>
        <xsl:with-param name="file" select="'XmlParser.h'"/>
      </xsl:call-template>
      
      <!-- XmlParser.m -->
      <xsl:call-template name="generate-c">
        <xsl:with-param name="template" select="'transform/xml-parser_m.xsl'"/>
        <xsl:with-param name="file" select="'XmlParser.m'"/>
      </xsl:call-template>
   </xsl:if>
   
   <!-- XmlWriter.h -->
   <xsl:call-template name="copy-resources">
      <xsl:with-param name="file" select="'xml/XmlWriter.h'"/>
      <xsl:with-param name="target" select="$src-main-java"/>
   </xsl:call-template>
   
   <!-- XmlWriter.m -->
   <xsl:call-template name="copy-resources">
      <xsl:with-param name="file" select="'xml/XmlWriter.m'"/>
      <xsl:with-param name="target" select="$src-main-java"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="model-policy">
   <xsl:param name="name"/>
   <xsl:param name="default" select="'false'"/>
   
   <xsl:variable name="model" select="/model"/>
   <xsl:variable name="enable-policy" select="$model/attribute::*[name()=concat('enable-', $name)]"/>
   <xsl:variable name="disable-policy" select="$model/attribute::*[name()=concat('disable-', $name)]"/>
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

<xsl:template name="generate-c">
   <xsl:param name="src-dir" select="$src-main-java" />
   <xsl:param name="template"/>
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_or_overwrite'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-code">
   <xsl:param name="path" />
   <xsl:param name="template"/>
   <xsl:param name="mode"/>
   <xsl:param name="package" select="''"/>
   <xsl:param name="class" select="''"/>
   <xsl:param name="name" select="''"/>

    <xsl:value-of select="$empty-line"/>
    <xsl:element name="file">
       <xsl:attribute name="path"><xsl:value-of select="$path"/></xsl:attribute>
       
       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
       
       <xsl:if test="$package">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">package</xsl:attribute>
             
             <xsl:value-of select="$package"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$class">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">class</xsl:attribute>
          
             <xsl:value-of select="$class"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$name">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">name</xsl:attribute>
          
             <xsl:value-of select="$name"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:value-of select="$empty-line"/>
    </xsl:element>
</xsl:template>

<xsl:template name="copy-resources">
   <xsl:param name="file"/>
   <xsl:param name="target"/>
   <xsl:param name="mode" select="'create_if_not_exists'"/>

    <xsl:value-of select="$empty-line"/>
    <xsl:element name="file">
       <xsl:attribute name="op">copy_resources</xsl:attribute>
       
       <xsl:attribute name="path"><xsl:value-of select="$target"/></xsl:attribute>
       
       <xsl:attribute name="template"><xsl:value-of select="$file"/></xsl:attribute>
       
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>

       <xsl:value-of select="$empty-line"/>
    </xsl:element>
</xsl:template>

</xsl:stylesheet>