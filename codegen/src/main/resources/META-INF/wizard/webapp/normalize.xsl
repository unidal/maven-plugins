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
      
      <xsl:attribute name="capital-name">
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="package">
         <xsl:choose>
            <xsl:when test="@package"><xsl:value-of select="@package"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../@package"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="server-port">
         <xsl:choose>
            <xsl:when test="@name">
               <xsl:value-of select="translate(substring(concat(@name,'000'),1,4),'-_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789','1122233344455566677778889999222333444555666777788899990000000000')"/>
            </xsl:when>
            <xsl:otherwise>8080</xsl:otherwise>
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
      
      <xsl:attribute name="capital-name">
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
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
      <xsl:variable name="class-prefix" select="@class-prefix"/>

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
      <xsl:attribute name="delegate-class">
         <xsl:value-of select="$class-prefix"/><xsl:value-of select="'Delegate'"/>
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
      <xsl:attribute name="detail-view">
         <xsl:choose>
            <xsl:when test="starts-with(@detail-view, '/')"><xsl:value-of select="@detail-view"/></xsl:when>
            <xsl:when test="@view">/jsp/<xsl:value-of select="../@name"/>/<xsl:value-of select="@detail-view"/></xsl:when>
            <xsl:otherwise>/jsp/<xsl:value-of select="../@name"/>/<xsl:value-of select="@name"/>-detail.jsp</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="model">
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
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>

      <!-- attribute definition -->
      <xsl:attribute name="page-size">
         <xsl:choose>
            <xsl:when test="@page-size"><xsl:value-of select="@page-size"/></xsl:when>
            <xsl:otherwise>9</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="class-name">
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="field-names">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
         <xsl:value-of select="'s'"/>
      </xsl:attribute>
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="param-names">
         <xsl:value-of select="$normalized-name"/>
         <xsl:value-of select="'s'"/>
      </xsl:attribute>
      <xsl:attribute name="value-type">
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="value-types">
         <xsl:value-of select="'List&lt;'" disable-output-escaping="yes"/>
         <xsl:value-of select="$capital-name"/>
         <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="get-methods">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
         <xsl:value-of select="'s'"/>
      </xsl:attribute>
      <xsl:attribute name="set-method">
         <xsl:value-of select="'set'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="set-methods">
         <xsl:value-of select="'set'"/>
         <xsl:value-of select="$capital-name"/>
         <xsl:value-of select="'s'"/>
      </xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="field">
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
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="normalized-value-type">
          <xsl:call-template name="normalize-key-type">
             <xsl:with-param name="value-type">
                <xsl:call-template name="convert-type"/>
             </xsl:with-param>
          </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="value-type-element">
         <xsl:choose>
            <xsl:when test="contains($normalized-value-type, '.')">
               <xsl:value-of select="$normalized-value-type"/>
            </xsl:when>
            <xsl:when test="@primitive='true'">
               <xsl:call-template name="convert-type"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:call-template name="capital-name">
                  <xsl:with-param name="name" select="$normalized-value-type" />
               </xsl:call-template>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="value-type-generic">
          <xsl:choose>
             <xsl:when test="@multiple='true'">
                <xsl:call-template name="generic-type">
                   <xsl:with-param name="type" select="$value-type-element"/>
                </xsl:call-template>
             </xsl:when>
             <xsl:otherwise>
               <xsl:value-of select="$value-type-element" />
             </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>
      <xsl:variable name="value-type">
          <xsl:choose>
             <xsl:when test="@multiple='true'">
                <xsl:value-of select="'List'"/>
                <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
             </xsl:when>
             <xsl:otherwise>
                <xsl:value-of select="$value-type-element"/>
             </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>

      <!-- attribute definition -->
      <xsl:attribute name="name">
         <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="capital-name">
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="property">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="value-type-element">
         <xsl:value-of select="$value-type-element"/>
      </xsl:attribute>
      <xsl:attribute name="value-type-generic">
         <xsl:value-of select="$value-type-generic"/>
      </xsl:attribute>
      <xsl:attribute name="value-type">
         <xsl:value-of select="$value-type"/>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:if test="@multiple='true'">
         <xsl:attribute name="get-methods">
            <xsl:value-of select="'get'"/>
            <xsl:value-of select="$capital-name"/>
            <xsl:value-of select="'s'"/>
         </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="set-method">
         <xsl:value-of select="'set'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:if test="@value-type='boolean' or @value-type='Boolean'">
         <xsl:attribute name="is-method">
            <xsl:value-of select="'is'"/>
            <xsl:value-of select="$capital-name"/>
         </xsl:attribute>
      </xsl:if>
      
      <xsl:apply-templates />
   </xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
   <xsl:copy>
      <xsl:copy-of select="@*" />
      
      <xsl:if test="node()">
         <xsl:apply-templates />
      </xsl:if>
   </xsl:copy>
</xsl:template>

<xsl:template name="convert-type">
   <xsl:param name="value-type" select="@value-type"/>
   
   <xsl:choose>
      <xsl:when test="$value-type = 'Class'">
          <xsl:value-of select="$value-type"/>
          <xsl:call-template name="generic-type">
             <xsl:with-param name="type">?</xsl:with-param>
          </xsl:call-template>
      </xsl:when>
      <xsl:when test="$value-type = 'String'">String</xsl:when>
      <xsl:when test="$value-type = 'Date'">java.util.Date</xsl:when>
      <xsl:when test="$value-type = 'Time'">Long</xsl:when>
      <xsl:when test="@primitive='true'">
         <xsl:choose>
         <xsl:when test="$value-type = 'boolean'">boolean</xsl:when>
         <xsl:when test="$value-type = 'byte'">byte</xsl:when>
         <xsl:when test="$value-type = 'char'">char</xsl:when>
         <xsl:when test="$value-type = 'short'">short</xsl:when>
         <xsl:when test="$value-type = 'int'">int</xsl:when>
         <xsl:when test="$value-type = 'long'">long</xsl:when>
         <xsl:when test="$value-type = 'float'">float</xsl:when>
         <xsl:when test="$value-type = 'double'">double</xsl:when>
         <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
         </xsl:choose>
      </xsl:when>
      <xsl:when test="$value-type = 'boolean'">Boolean</xsl:when>
      <xsl:when test="$value-type = 'byte'">Byte</xsl:when>
      <xsl:when test="$value-type = 'char'">Character</xsl:when>
      <xsl:when test="$value-type = 'short'">Short</xsl:when>
      <xsl:when test="$value-type = 'int'">Integer</xsl:when>
      <xsl:when test="$value-type = 'long'">Long</xsl:when>
      <xsl:when test="$value-type = 'float'">Float</xsl:when>
      <xsl:when test="$value-type = 'double'">Double</xsl:when>
      <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>