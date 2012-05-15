<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:template match="/">
   <xsl:apply-templates/>
</xsl:template>

<xsl:template match="model">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:attribute name="transform-package">
         <xsl:choose>
            <xsl:when test="@transform-package">
               <xsl:value-of select="@transform-package"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="@model-package"/>
               <xsl:value-of select="'.transform'"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      
      <xsl:apply-templates mode="entity"/>
   </xsl:copy>
</xsl:template>

<xsl:template match="entity" mode="entity">
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

      <!-- attribute definition -->
      <xsl:attribute name="entity-package">
         <xsl:choose>
            <xsl:when test="@entity-package"><xsl:value-of select="@entity-package"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../@model-package"/>.entity</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="class-name">
         <xsl:value-of select="$class-name"/>
      </xsl:attribute>
      <xsl:attribute name="entity-class">
         <xsl:value-of select="$class-name"/>
      </xsl:attribute>
      <xsl:attribute name="tag-name">
         <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="xsd-type">
         <xsl:value-of select="$capital-name"/>
         <xsl:value-of select="'Type'"/>
      </xsl:attribute>
      <xsl:attribute name="upper-name">
         <xsl:value-of select="'ENTITY_'"/>
         <xsl:call-template name="upper-name">
            <xsl:with-param name="name" select="$name"/>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="local-name">
         <xsl:choose>
            <xsl:when test="$normalized-name='node'">node_</xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$normalized-name"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="ref-name">
         <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="visit-method">
         <xsl:value-of select="'visit'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="merge-method">
         <xsl:value-of select="'merge'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="visit-children-method">
         <xsl:value-of select="'visit'"/>
         <xsl:value-of select="$capital-name"/>
         <xsl:value-of select="'Children'"/>
      </xsl:attribute>
      <xsl:attribute name="build-method">
         <xsl:value-of select="'build'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="parse-for-method">
         <xsl:value-of select="'parseFor'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:if test="@all-children-in-sequence='true'">
         <xsl:attribute name="value-type-all-children-in-sequence">
            <xsl:value-of select="'List&lt;BaseEntity&lt;?&gt;&gt;'"/>
         </xsl:attribute>
         <xsl:attribute name="field-all-children-in-sequence">
            <xsl:value-of select="'m_allChildrenInSequence'"/>
         </xsl:attribute>
         <xsl:attribute name="method-get-all-children-in-sequence">
            <xsl:value-of select="'getAllChildrenInSequence'"/>
         </xsl:attribute>
      </xsl:if>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="attribute">
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
      <xsl:attribute name="value-type">
         <xsl:call-template name="normalize-value-type">
            <xsl:with-param name="value-type">
               <xsl:call-template name="convert-type"/>
            </xsl:with-param>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="ref-name">
         <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="render">
         <xsl:choose>
            <xsl:when test="@key='true'">true</xsl:when>
            <xsl:when test="@render"><xsl:value-of select="@render"/></xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="required">
         <xsl:choose>
            <xsl:when test="@key='true'">true</xsl:when>
            <xsl:when test="@required"><xsl:value-of select="@required"/></xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="readonly">
         <xsl:choose>
            <xsl:when test="@readonly"><xsl:value-of select="@readonly"/></xsl:when>
            <xsl:when test="@key='true'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="nullable">
         <xsl:choose>
            <xsl:when test="@nullable"><xsl:value-of select="@nullable"/></xsl:when>
            <xsl:when test="@key='true'">false</xsl:when>
            <xsl:when test="@required='true'">false</xsl:when>
            <xsl:otherwise>true</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="upper-name">
         <xsl:value-of select="'ATTR_'"/>
         <xsl:call-template name="upper-name">
            <xsl:with-param name="name" select="$name"/>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:choose>
            <xsl:when test="$capital-name='Class'">Clazz</xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:if test="@value-type='boolean' or @value-type='Boolean'">
         <xsl:attribute name="is-method">
            <xsl:value-of select="'is'"/>
            <xsl:value-of select="$capital-name"/>
         </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="set-method">
         <xsl:value-of select="'set'"/>
         <xsl:choose>
            <xsl:when test="$capital-name='Class'">Clazz</xsl:when>
            <xsl:otherwise><xsl:value-of select="$capital-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:if test="@method-inc='true'">
         <xsl:attribute name="inc-method">
            <xsl:value-of select="'inc'"/>
            <xsl:value-of select="$capital-name"/>
         </xsl:attribute>
      </xsl:if>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="element">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <!-- variable definition -->
      <xsl:variable name="element-name">
         <xsl:choose>
            <xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="name">
         <xsl:choose>
            <xsl:when test="(@type='list' or @type='set') and @names"><xsl:value-of select="@names"/></xsl:when>
            <xsl:when test="@type='set'"><xsl:value-of select="$element-name"/>Set</xsl:when>
            <xsl:when test="@type='list'"><xsl:value-of select="$element-name"/>List</xsl:when>
            <xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:variable name="normalized-name-element">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="$element-name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="normalized-name">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source" select="$name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="normalized-value-type">
          <xsl:call-template name="normalize-key-type">
             <xsl:with-param name="value-type">
                <xsl:call-template name="convert-type"/>
             </xsl:with-param>
          </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="capital-name-element">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name-element"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="capital-name">
         <xsl:call-template name="capital-name">
            <xsl:with-param name="name" select="$normalized-name"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="value-type-element">
         <xsl:choose>
            <xsl:when test="contains($normalized-value-type, '.')">
               <xsl:value-of select="$normalized-value-type"/>
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
             <xsl:when test="@type='list' or @type='set'">
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
             <xsl:when test="@type='set'">
                <xsl:value-of select="'Set'"/>
                <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
             </xsl:when>
             <xsl:when test="@type='list'">
                <xsl:value-of select="'List'"/>
                <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
             </xsl:when>
             <xsl:otherwise>
                <xsl:value-of select="$value-type-element"/>
             </xsl:otherwise>
          </xsl:choose>
      </xsl:variable>
   
      <!-- attribute definition -->
      <xsl:attribute name="value-type-element">
         <xsl:value-of select="$value-type-element"/>
      </xsl:attribute>
      <xsl:attribute name="value-type-generic">
         <xsl:value-of select="$value-type-generic"/>
      </xsl:attribute>
      <xsl:attribute name="value-type">
         <xsl:value-of select="$value-type"/>
      </xsl:attribute>
      <xsl:attribute name="capital-name-element">
         <xsl:value-of select="$capital-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="param-name-element">
         <xsl:value-of select="$normalized-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="local-name-element">
         <xsl:value-of select="$normalized-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="local-name">
         <xsl:choose>
            <xsl:when test="$normalized-name='node'">node_</xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$normalized-name"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="ref-name">
         <xsl:value-of select="$element-name"/>
      </xsl:attribute>
      <xsl:attribute name="upper-name">
         <xsl:value-of select="'ELEMENT_'"/>
         <xsl:call-template name="upper-name">
            <xsl:with-param name="name" select="$name"/>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="upper-name-element">
         <xsl:value-of select="'ELEMENT_'"/>
         <xsl:call-template name="upper-name">
            <xsl:with-param name="name" select="$element-name"/>
         </xsl:call-template>
      </xsl:attribute>
      <xsl:attribute name="tag-name">
         <xsl:value-of select="$name"/>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="add-method">
         <xsl:value-of select="'add'"/>
         <xsl:value-of select="$capital-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:if test="@value-type='boolean' or @value-type='Boolean'">
         <xsl:attribute name="is-method">
            <xsl:value-of select="'is'"/>
            <xsl:value-of select="$capital-name"/>
         </xsl:attribute>
      </xsl:if>
      <xsl:attribute name="remove-method">
         <xsl:value-of select="'remove'"/>
         <xsl:value-of select="$capital-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="set-method">
         <xsl:value-of select="'set'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="build-method">
         <xsl:value-of select="'build'"/>
         <xsl:value-of select="$capital-name-element"/>
      </xsl:attribute>
      <xsl:attribute name="is-annotation">
         <xsl:value-of select="@value-type='java.lang.annotation.Annotation'"/>
      </xsl:attribute>
      <xsl:if test="@method-inc='true'">
         <xsl:attribute name="inc-method">
            <xsl:value-of select="'inc'"/>
            <xsl:value-of select="$capital-name"/>
         </xsl:attribute>
      </xsl:if>

      <xsl:choose>
      	<xsl:when test="@type='list'">
      		<xsl:attribute name="list">true</xsl:attribute>
      	</xsl:when>
      	<xsl:when test="@type='set'">
      		<xsl:attribute name="set">true</xsl:attribute>
      	</xsl:when>
      </xsl:choose>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="any">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:variable name="name">
         <xsl:choose>
            <xsl:when test="@name"><xsl:value-of select="@name"/></xsl:when>
            <xsl:otherwise>dynamic-elements</xsl:otherwise>
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
      <xsl:variable name="value-type-element" select="'Any'"/>
      <xsl:variable name="value-type-generic">
         <xsl:call-template name="generic-type">
            <xsl:with-param name="type" select="$value-type-element"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="value-type">
         <xsl:value-of select="'List'"/>
         <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
      </xsl:variable>

      <!-- attribute definition -->
      <xsl:attribute name="entity-package">
         <xsl:choose>
            <xsl:when test="@entity-package"><xsl:value-of select="@entity-package"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="../../@model-package"/>.entity</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
      <xsl:attribute name="entity-class">
         <xsl:value-of select="'Any'"/>
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
      <xsl:attribute name="param-name">
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="field-name">
         <xsl:value-of select="'m_'"/>
         <xsl:value-of select="$normalized-name"/>
      </xsl:attribute>
      <xsl:attribute name="get-method">
         <xsl:value-of select="'get'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="set-method">
         <xsl:value-of select="'set'"/>
         <xsl:value-of select="$capital-name"/>
      </xsl:attribute>
      <xsl:attribute name="visit-method">
         <xsl:value-of select="'visitAny'"/>
      </xsl:attribute>
      <xsl:attribute name="build-method">
         <xsl:value-of select="'buildAny'"/>
      </xsl:attribute>

      <xsl:attribute name="list">true</xsl:attribute>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="entity">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
       <xsl:call-template name="entity">
          <xsl:with-param name="entity" select="."/>
       </xsl:call-template>
      
      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template match="entity-ref">
   <xsl:copy>
      <xsl:copy-of select="@*"/>
      
      <xsl:variable name="entity-name" select="@name"/>
      <xsl:variable name="entity" select="/model/entity[@name=$entity-name]"/>

      <xsl:choose>
         <xsl:when test="$entity">
            <xsl:call-template name="entity">
               <xsl:with-param name="entity" select="$entity"/>
               <xsl:with-param name="loop" select="boolean($entity/entity-ref[@name=$entity-name])"/>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <xsl:message>Entity(<xsl:value-of select="$entity-name"/>) is not defined!</xsl:message>
         </xsl:otherwise>
      </xsl:choose>
      
      <xsl:choose>
      	<xsl:when test="@type='list'">
      		<xsl:attribute name="list">true</xsl:attribute>
      	</xsl:when>
      	<xsl:when test="@type='map'">
      		<xsl:attribute name="map">true</xsl:attribute>
      	</xsl:when>
      </xsl:choose>

      <xsl:apply-templates/>
   </xsl:copy>
</xsl:template>

<xsl:template name="entity">
    <xsl:param name="entity"/>
    <xsl:param name="loop" select="'false'"/>
   
   <!-- variable definition -->
   <xsl:variable name="ref-name">
      <xsl:choose>
         <xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
         <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="name">
      <xsl:choose>
         <xsl:when test="(@type='list' or @type='map') and @names"><xsl:value-of select="@names"/></xsl:when>
         <xsl:when test="@type='map'"><xsl:value-of select="$ref-name"/>Map</xsl:when>
         <xsl:when test="@type='list'"><xsl:value-of select="$ref-name"/>List</xsl:when>
         <xsl:otherwise><xsl:value-of select="$ref-name"/></xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="normalized-name-element">
      <xsl:call-template name="normalize">
         <xsl:with-param name="source" select="$ref-name"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="normalized-name">
      <xsl:call-template name="normalize">
         <xsl:with-param name="source" select="$name"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="normalized-value-type">
      <xsl:call-template name="normalize-value-type">
         <xsl:with-param name="value-type">
            <xsl:value-of select="@value-type"/>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="capital-name-element">
      <xsl:call-template name="capital-name">
         <xsl:with-param name="name" select="$normalized-name-element"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="capital-name">
      <xsl:call-template name="capital-name">
         <xsl:with-param name="name" select="$normalized-name"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="value-type-key">
       <xsl:call-template name="normalize-key-type">
          <xsl:with-param name="value-type">
             <xsl:value-of select="$entity/node()[name()='attribute' or name()='element'][@key]/@value-type"/>
          </xsl:with-param>
       </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="value-type-element">
      <xsl:choose>
         <xsl:when test="@value-type">
            <xsl:call-template name="normalize-key-type">
               <xsl:with-param name="value-type">
                  <xsl:value-of select="@value-type"/>
               </xsl:with-param>
            </xsl:call-template>
         </xsl:when>
         <xsl:when test="$entity/@class-name">
            <xsl:call-template name="capital-name">
               <xsl:with-param name="name">
                  <xsl:call-template name="normalize">
                     <xsl:with-param name="source">
                        <xsl:value-of select="$entity/@class-name"/>
                     </xsl:with-param>
                  </xsl:call-template>
               </xsl:with-param>
            </xsl:call-template>
         </xsl:when>
         <xsl:otherwise>
            <xsl:call-template name="capital-name">
               <xsl:with-param name="name">
                  <xsl:call-template name="normalize">
                     <xsl:with-param name="source">
                        <xsl:choose>
                           <xsl:when test="$entity/@alias">
                              <xsl:value-of select="$entity/@alias"/>
                           </xsl:when>
                           <xsl:otherwise>
                              <xsl:value-of select="@name"/>
                           </xsl:otherwise>
                        </xsl:choose>
                     </xsl:with-param>
                  </xsl:call-template>
               </xsl:with-param>
            </xsl:call-template>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:variable>
   <xsl:variable name="value-type-generic">
       <xsl:choose>
          <xsl:when test="@type='map'">
             <xsl:call-template name="generic-type">
                <xsl:with-param name="type">
                   <xsl:value-of select="$value-type-key"/>
                   <xsl:text>, </xsl:text>
                   <xsl:value-of select="$value-type-element"/>
                </xsl:with-param>
             </xsl:call-template>
          </xsl:when>
          <xsl:when test="@type='list'">
             <xsl:call-template name="generic-type">
                <xsl:with-param name="type" select="$value-type-element"/>
             </xsl:call-template>
          </xsl:when>
       </xsl:choose>
   </xsl:variable>
   <xsl:variable name="value-type">
       <xsl:choose>
          <xsl:when test="@type='map'">
             <xsl:value-of select="'Map'"/>
             <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
          </xsl:when>
          <xsl:when test="@type='list'">
             <xsl:value-of select="'List'"/>
             <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
          </xsl:when>
          <xsl:when test="$normalized-value-type != ''">
             <xsl:value-of select="$normalized-value-type"/>
          </xsl:when>
          <xsl:otherwise>
             <xsl:value-of select="$value-type-element"/>
          </xsl:otherwise>
       </xsl:choose>
   </xsl:variable>

   <!-- attribute definition -->
   <xsl:attribute name="value-type-key">
      <xsl:value-of select="$value-type-key"/>
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
   <xsl:if test="@type='map'">
   	  <xsl:attribute name="value-type-entry">
         <xsl:value-of select="'Map.Entry'"/>
         <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
   	  </xsl:attribute>
   </xsl:if>
   <xsl:attribute name="param-name-element">
      <xsl:value-of select="$normalized-name-element"/>
   </xsl:attribute>
   <xsl:attribute name="param-name">
      <xsl:value-of select="$normalized-name"/>
   </xsl:attribute>
   <xsl:attribute name="local-name-element">
      <xsl:value-of select="$normalized-name-element"/>
      <xsl:if test="$loop='true'">_</xsl:if>
   </xsl:attribute>
   <xsl:attribute name="local-name">
      <xsl:choose>
         <xsl:when test="$normalized-name='node'">node_</xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$normalized-name"/>
            <xsl:if test="$loop='true'">_</xsl:if>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:attribute>
   <xsl:attribute name="ref-name">
      <xsl:value-of select="$ref-name"/>
   </xsl:attribute>
   <xsl:attribute name="upper-name">
      <xsl:value-of select="'ENTITY_'"/>
      <xsl:call-template name="upper-name">
         <xsl:with-param name="name">
            <xsl:choose>
               <xsl:when test="(@type='list' or @type='map') and not(@xml-indent='true') and @alias"><xsl:value-of select="@alias"/></xsl:when>
               <xsl:when test="(@type='list' or @type='map') and not(@xml-indent='true')"><xsl:value-of select="@name"/></xsl:when>
               <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
            </xsl:choose>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:attribute>
   <xsl:attribute name="upper-names">
      <xsl:value-of select="'ENTITY_'"/>
      <xsl:call-template name="upper-name">
         <xsl:with-param name="name">
            <xsl:value-of select="$name"/>
         </xsl:with-param>
      </xsl:call-template>
   </xsl:attribute>
   <xsl:attribute name="tag-name">
      <xsl:choose>
         <xsl:when test="(@type='list' or @type='map') and not(@xml-indent='true') and @alias"><xsl:value-of select="@alias"/></xsl:when>
         <xsl:when test="(@type='list' or @type='map') and not(@xml-indent='true')"><xsl:value-of select="@name"/></xsl:when>
         <xsl:otherwise><xsl:value-of select="$name"/></xsl:otherwise>
      </xsl:choose>
   </xsl:attribute>
   <xsl:attribute name="tag-names">
      <xsl:value-of select="$name"/>
   </xsl:attribute>
   <xsl:attribute name="field-name">
      <xsl:value-of select="'m_'"/>
      <xsl:value-of select="$normalized-name"/>
   </xsl:attribute>
   <xsl:attribute name="add-method">
      <xsl:value-of select="'add'"/>
      <xsl:value-of select="$capital-name-element"/>
   </xsl:attribute>
   <xsl:attribute name="find-method">
      <xsl:value-of select="'find'"/>
      <xsl:value-of select="$capital-name-element"/>
   </xsl:attribute>
   <xsl:attribute name="get-method">
      <xsl:value-of select="'get'"/>
      <xsl:value-of select="$capital-name"/>
   </xsl:attribute>
   <xsl:attribute name="remove-method">
      <xsl:value-of select="'remove'"/>
      <xsl:value-of select="$capital-name-element"/>
   </xsl:attribute>
   <xsl:attribute name="set-method">
      <xsl:value-of select="'set'"/>
      <xsl:value-of select="$capital-name"/>
   </xsl:attribute>
   <xsl:attribute name="on-event-method">
      <xsl:value-of select="'on'"/>
      <xsl:value-of select="$capital-name-element"/>
   </xsl:attribute>

   <xsl:if test="@method-find-or-create='true'">
      <xsl:attribute name="find-or-create-method">
         <xsl:value-of select="'findOrCreate'"/>
         <xsl:value-of select="$capital-name-element"/>
      </xsl:attribute>
   </xsl:if>

   <xsl:attribute name="xml-indent">
      <xsl:choose>
         <xsl:when test="../@all-children-in-sequence='true'">false</xsl:when>
         <xsl:when test="@xml-indent='true'">true</xsl:when>
         <xsl:otherwise>false</xsl:otherwise>
      </xsl:choose>
   </xsl:attribute>
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