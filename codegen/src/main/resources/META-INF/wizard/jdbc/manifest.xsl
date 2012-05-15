<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="xsl-dir"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="manifest">
      <xsl:apply-templates select="/wizard/datasources" />
      <xsl:apply-templates select="/wizard/datasources/datasource[@name]" />
   </xsl:element>
</xsl:template>

<xsl:template match="datasources">
   <!-- ComponentsConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'ComponentsConfigurator'"/>
     <xsl:with-param name="package" select="/wizard/@build-package"/>
     <xsl:with-param name="template" select="'build/components-configurator.xsl'"/>
   </xsl:call-template>

   <!-- AllTests class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="src-dir" select="concat(/wizard/@base-dir, '/src/test/java')" />
     <xsl:with-param name="class" select="'AllTests'"/>
     <xsl:with-param name="package" select="/wizard/@package"/>
     <xsl:with-param name="template" select="'test/all-tests.xsl'"/>
   </xsl:call-template>
</xsl:template>

<xsl:template match="datasource">
   <!-- DatabseConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@configurator-class"/>
     <xsl:with-param name="package" select="/wizard/@build-package"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="'build/database-configurator.xsl'"/>
     <xsl:with-param name="mode" select="'create_or_overwrite'"/>
   </xsl:call-template>

</xsl:template>

<xsl:template name="wizard-policy">
   <xsl:param name="name"/>
   <xsl:param name="default" select="'false'"/>
   
   <xsl:variable name="wizard" select="/wizard"/>
   <xsl:variable name="enable-policy" select="$wizard/attribute::*[name()=concat('enable-', $name)]"/>
   <xsl:variable name="disable-policy" select="$wizard/attribute::*[name()=concat('disable-', $name)]"/>
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

<xsl:template name="generate-resource">
   <xsl:param name="src-dir" select="concat(/wizard/@base-dir, '/src/main/resources')" />
   <xsl:param name="package"/>
   <xsl:param name="module"/>
   <xsl:param name="name"/>
   <xsl:param name="file"/>
   <xsl:param name="template"/>
   <xsl:param name="mode" select="'create_if_not_exists'"/>

    <xsl:value-of select="$empty-line"/>
    <xsl:element name="file">
       <xsl:attribute name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
       </xsl:attribute>
       
       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
       
       <xsl:value-of select="$empty-line"/>
       <xsl:element name="property">
          <xsl:attribute name="name">package</xsl:attribute>
          
          <xsl:value-of select="$package"/>
       </xsl:element>
       
       <xsl:if test="$module">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">module</xsl:attribute>
          
             <xsl:value-of select="$module"/>
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

<xsl:template name="generate-java">
   <xsl:param name="src-dir" select="concat(/wizard/@base-dir, '/src/main/java')" />
   <xsl:param name="module" select="''" />
   <xsl:param name="name" select="''" />
   <xsl:param name="path" select="''" />
   <xsl:param name="package"/>
   <xsl:param name="class" select="''"/>
   <xsl:param name="template"/>
   <xsl:param name="mode" select="'create_if_not_exists'"/>

    <xsl:value-of select="$empty-line"/>
    <xsl:element name="file">
       <xsl:attribute name="path">
          <xsl:choose>
             <xsl:when test="$path">
                <xsl:value-of select="$path"/>
             </xsl:when>
             <xsl:otherwise>
                <xsl:value-of select="$src-dir"/>/<xsl:value-of select="translate($package,'.','/')"/>/<xsl:value-of select="$empty"/>
                <xsl:value-of select="$class"/>.java<xsl:value-of select="$empty"/>
             </xsl:otherwise>
          </xsl:choose>
       </xsl:attribute>

       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>

       <xsl:value-of select="$empty-line"/>
       <xsl:element name="property">
          <xsl:attribute name="name">package</xsl:attribute>
          
          <xsl:value-of select="$package"/>
       </xsl:element>

       <xsl:value-of select="$empty-line"/>
       <xsl:element name="property">
          <xsl:attribute name="name">class</xsl:attribute>
          
          <xsl:value-of select="$class"/>
       </xsl:element>

       <xsl:if test="$module">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">module</xsl:attribute>
          
             <xsl:value-of select="$module"/>
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

</xsl:stylesheet>