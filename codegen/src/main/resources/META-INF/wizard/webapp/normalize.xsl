<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard"/>
</xsl:template>

<xsl:template match="wizard">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:attribute name="build-package">
         <xsl:value-of select="@package"/>
         <xsl:value-of select="'.build'"/>
      </xsl:attribute>

      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="webapp">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:attribute name="package">
         <xsl:choose>
            <xsl:when test="@package"><xsl:value-of select="@package"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../@package"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>

      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="module">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:variable name="name">
      	<xsl:value-of select="@name"/>
      </xsl:variable>
      <xsl:variable name="normalized-name">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="$name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>

      <xsl:attribute name="package">
         <xsl:choose>
            <xsl:when test="@package"><xsl:value-of select="@package"/></xsl:when>
            <xsl:when test="../@package"><xsl:value-of select="../@package"/>.<xsl:value-of select="$normalized-name"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../../@package"/>.<xsl:value-of select="$normalized-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="module-class">
         <xsl:choose>
            <xsl:when test="@module-class"><xsl:value-of select="@module-class"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/>Module</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="context-class">
         <xsl:choose>
            <xsl:when test="@module-context"><xsl:value-of select="@module-context"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/>Context</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="page-class">
         <xsl:choose>
            <xsl:when test="@module-page"><xsl:value-of select="@module-page"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/>Page</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:attribute name="webres">
         <xsl:choose>
            <xsl:when test="@webres"><xsl:value-of select="@webres"/></xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="page">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <!-- variable definition -->
      <xsl:variable name="name">
         <xsl:choose>
            <xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="normalized-name">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="$name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="normalized-module-name">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="../@name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="class-name">
         <xsl:choose>
            <xsl:when test="@class-name">
               <xsl:call-template name="capital-name">
                  <xsl:with-param name="name">
                     <xsl:call-template name="normalize">
                        <xsl:with-param name="source" select="@class-name"/>
                     </xsl:call-template>
                  </xsl:with-param>
               </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$capital-name"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="class-prefix"></xsl:variable>

      <!-- attribute definition -->
      <xsl:attribute name="upper-name">
         <xsl:call-template name="upper-name">
            <xsl:with-param name="name" select="$name"/>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="package">
         <xsl:choose>
            <xsl:when test="@package"><xsl:value-of select="@package"/></xsl:when>
            <xsl:when test="../@package"><xsl:value-of select="../@package"/>.page.<xsl:value-of select="$normalized-name"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../../@package"/>.<xsl:value-of select="$normalized-module-name"/>.page.<xsl:value-of select="$normalized-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="path">
         <xsl:choose>
            <xsl:when test="@path"><xsl:value-of select="@path"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="title">
         <xsl:choose>
            <xsl:when test="@title"><xsl:value-of select="@title"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="description">
         <xsl:choose>
            <xsl:when test="@description"><xsl:value-of select="@description"/></xsl:when>
            <xsl:when test="description"><xsl:value-of select="normalize-space(description)"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="default">
         <xsl:choose>
            <xsl:when test="@default='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="standalone">
         <xsl:choose>
            <xsl:when test="@standalone='false'">false</xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="action-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Action'"/>
      </xsl:attribute>
      <xsl:attribute name="context-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Context'"/>
      </xsl:attribute>
      <xsl:attribute name="handler-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Handler'"/>
      </xsl:attribute>
      <xsl:attribute name="jsp-file-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'JspFile'"/>
      </xsl:attribute>
      <xsl:attribute name="jsp-viewer-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'JspViewer'"/>
      </xsl:attribute>
      <xsl:attribute name="model-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Model'"/>
      </xsl:attribute>
      <xsl:attribute name="payload-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Payload'"/>
      </xsl:attribute>
      
      <xsl:attribute name="view">
         <xsl:choose>
            <xsl:when test="starts-with(@view, '/')"><xsl:value-of select="@view"/></xsl:when>
            <xsl:when test="@view">/jsp/<xsl:value-of select="../@name"/>/<xsl:value-of select="@view"/></xsl:when>
            <xsl:otherwise>/jsp/<xsl:value-of select="../@name"/>/<xsl:value-of select="@name"/>.jsp</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

</xsl:stylesheet>