<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="base-dir"/>
<xsl:param name="src-main-java"/>
<xsl:param name="src-main-resources"/>
<xsl:param name="src-test-java"/>
<xsl:param name="src-test-resources"/>
<xsl:param name="src-main-webapp"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="outputs">
      <xsl:apply-templates select="/wizard" />
	  
	  <!-- datasources.xml -->
	  <xsl:call-template name="generate-resource">
	     <xsl:with-param name="src-dir" select="''" />
	     <xsl:with-param name="file" select="'datasources.xml'"/>
	     <xsl:with-param name="template" select="'xml/datasources.xsl'"/>
         <xsl:with-param name="mode" select="'create_or_overwrite'"/>
	  </xsl:call-template>
   </xsl:element>
</xsl:template>

<xsl:template match="wizard">
   <!-- ComponentsConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'ComponentsConfigurator'"/>
     <xsl:with-param name="package" select="/wizard/@build-package"/>
     <xsl:with-param name="template" select="'build/components-configurator.xsl'"/>
   </xsl:call-template>

   <!-- DatabseConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'DatabaseConfigurator'"/>
     <xsl:with-param name="package" select="/wizard/@build-package"/>
     <xsl:with-param name="template" select="'build/database-configurator.xsl'"/>
     <xsl:with-param name="mode" select="'create_or_overwrite'"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-java">
   <xsl:param name="src-dir" select="$src-main-java" />
   <xsl:param name="template"/>
   <xsl:param name="package"/>
   <xsl:param name="class"/>
   <xsl:param name="name" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
        <xsl:value-of select="$src-dir"/>/<xsl:value-of select="translate($package,'.','/')"/>/<xsl:value-of select="$empty"/>
        <xsl:value-of select="$class"/>.java<xsl:value-of select="$empty"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
      <xsl:with-param name="package" select="$package"/>
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-resource">
   <xsl:param name="src-dir" select="$src-main-resources" />
   <xsl:param name="template"/>
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-test-java">
   <xsl:param name="src-dir" select="$src-test-java" />
   <xsl:param name="template"/>
   <xsl:param name="package"/>
   <xsl:param name="class"/>
   <xsl:param name="name" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
        <xsl:value-of select="$src-dir"/>/<xsl:value-of select="translate($package,'.','/')"/>/<xsl:value-of select="$empty"/>
        <xsl:value-of select="$class"/>.java<xsl:value-of select="$empty"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
      <xsl:with-param name="package" select="$package"/>
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-test-resource">
   <xsl:param name="src-dir" select="$src-test-resources" />
   <xsl:param name="template"/>
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

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
    <xsl:element name="output">
       <xsl:attribute name="op">apply_template</xsl:attribute>
       <xsl:attribute name="path"><xsl:value-of select="$path"/></xsl:attribute>
       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
       
       <xsl:if test="$package">
          <xsl:element name="property">
             <xsl:attribute name="name">package</xsl:attribute>
             <xsl:value-of select="$package"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$class">
          <xsl:element name="property">
             <xsl:attribute name="name">class</xsl:attribute>
             <xsl:value-of select="$class"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$name">
          <xsl:element name="property">
             <xsl:attribute name="name">name</xsl:attribute>
             <xsl:value-of select="$name"/>
          </xsl:element>
       </xsl:if>
    </xsl:element>
</xsl:template>

</xsl:stylesheet>