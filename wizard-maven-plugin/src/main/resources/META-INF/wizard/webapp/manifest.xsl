<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="src-main-java"/>
<xsl:param name="src-main-resources"/>
<xsl:param name="src-test-java"/>
<xsl:param name="src-test-resources"/>
<xsl:param name="src-main-webapp"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:variable name="webapp-root">
   <xsl:choose>
      <xsl:when test="/wizard/webapp/@module='true'"><xsl:value-of select="concat($src-main-resources, '/META-INF/resources', /wizard/webapp/@path)"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="$src-main-webapp"/></xsl:otherwise>
   </xsl:choose>
</xsl:variable>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="manifest">
      <xsl:apply-templates select="/wizard"/>
      <xsl:apply-templates select="/wizard/webapp"/>
      <xsl:apply-templates select="/wizard/webapp/module"/>
      <xsl:apply-templates select="/wizard/webapp/module/page"/>
   </xsl:element>
</xsl:template>

<xsl:template match="wizard">
   <!-- AllTests class -->
   <xsl:call-template name="generate-test-java">
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="class" select="'AllTests'"/>
     <xsl:with-param name="template" select="'test/all-tests.xsl'"/>
   </xsl:call-template>

   <xsl:if test="@module='false'">
      <!-- TestServer class -->
      <xsl:call-template name="generate-test-java">
        <xsl:with-param name="package" select="@package"/>
        <xsl:with-param name="class" select="'TestServer'"/>
        <xsl:with-param name="template" select="'test/test-server.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <!-- ComponentsConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="package" select="concat(@package, '.build')"/>
     <xsl:with-param name="class" select="'ComponentsConfigurator'"/>
     <xsl:with-param name="template" select="'build/components-configurator.xsl'"/>
   </xsl:call-template>

   <!-- WebComponentConfigurator class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="package" select="concat(@package, '.build')"/>
     <xsl:with-param name="class" select="'WebComponentConfigurator'"/>
     <xsl:with-param name="template" select="'build/web-component-configurator.xsl'"/>
     <xsl:with-param name="mode" select="'create_or_overwrite'"/>
   </xsl:call-template>

   <xsl:if test="@module='false'">
      <!-- WEB-INF/web.xml file -->
      <xsl:call-template name="generate-web-resource">
        <xsl:with-param name="file" select="'WEB-INF/web.xml'"/>
        <xsl:with-param name="template" select="'web-inf/web-xml.xsl'"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template match="webapp">
   <xsl:if test="@layout='bootstrap'">
      <!-- NavigationBar class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="package" select="concat(@package, '.view')"/>
        <xsl:with-param name="class" select="'NavigationBar'"/>
        <xsl:with-param name="template" select="'pres/navigation-bar.xsl'"/>
      </xsl:call-template>

      <!-- layout.tag class -->
      <xsl:call-template name="generate-web-resource">
        <xsl:with-param name="file" select="@layout-tag"/>
        <xsl:with-param name="template" select="'web-inf/tags/layout-tag.xsl'"/>
      </xsl:call-template>

      <!-- app.tld class -->
      <xsl:call-template name="generate-web-resource">
        <xsl:with-param name="file" select="@app-tld"/>
        <xsl:with-param name="template" select="'web-inf/app-tld.xsl'"/>
      </xsl:call-template>

      <xsl:call-template name="copy-resources">
        <xsl:with-param name="file" select="'bootstrap'"/>
        <xsl:with-param name="target" select="$webapp-root" />
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template match="module">
   <!-- Module class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@module-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="'module/module.xsl'"/>
     <xsl:with-param name="mode" select="'create_or_overwrite'"/>
   </xsl:call-template>

   <!-- Context class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@context-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="'module/context.xsl'"/>
   </xsl:call-template>

   <!-- Page class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@page-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="'module/page.xsl'"/>
     <xsl:with-param name="mode" select="'create_or_overwrite'"/>
   </xsl:call-template>
</xsl:template>

<xsl:template match="page">
   <!-- Action class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@action-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page',  '/action.xsl')"/>
   </xsl:call-template>

   <!-- Context class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@context-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/context.xsl')"/>
   </xsl:call-template>

   <!-- Handler class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@handler-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/handler.xsl')"/>
   </xsl:call-template>

   <!-- JspFile class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@jsp-file-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/jsp-file.xsl')"/>
   </xsl:call-template>

   <!-- JspViewer class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@jsp-viewer-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/jsp-viewer.xsl')"/>
   </xsl:call-template>

   <!-- Model class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@model-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/model.xsl')"/>
   </xsl:call-template>

   <!-- Payload class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="@payload-class"/>
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="template" select="concat('page', '/payload.xsl')"/>
   </xsl:call-template>
   
   <!-- view.jsp -->
   <xsl:call-template name="generate-resource">
     <xsl:with-param name="src-dir" select="$webapp-root" />
     <xsl:with-param name="package" select="@package"/>
     <xsl:with-param name="module" select="../@name"/>
     <xsl:with-param name="name" select="@name"/>
     <xsl:with-param name="file" select="substring(@view,1)"/>
     <xsl:with-param name="template" select="concat('pres', '/view-jsp.xsl')"/>
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

<xsl:template name="generate-java">
   <xsl:param name="src-dir" select="$src-main-java" />
   <xsl:param name="template"/>
   <xsl:param name="package"/>
   <xsl:param name="class"/>
   <xsl:param name="module" select="''" />
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
      <xsl:with-param name="module" select="$module"/>
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-resource">
   <xsl:param name="src-dir" select="$src-main-resources" />
   <xsl:param name="template"/>
   <xsl:param name="package" select="''" />
   <xsl:param name="module" select="''" />
   <xsl:param name="name" select="''" />
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
      <xsl:with-param name="package" select="$package"/>
      <xsl:with-param name="module" select="$module"/>
      <xsl:with-param name="name" select="$name"/>
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

<xsl:template name="generate-web-resource">
   <xsl:param name="src-dir" select="$webapp-root" />
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
   <xsl:param name="module" select="''"/>
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