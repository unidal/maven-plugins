<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" />
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="xs-namespace" select="'http://www.w3.org/2001/XMLSchema'"/>

<xsl:template match="/">
   <xsl:element name="xs:schema" namespace="{$xs-namespace}">
      <xsl:attribute name="elementFormDefault">qualified</xsl:attribute>
      <xsl:attribute name="attributeFormDefault">unqualified</xsl:attribute>
      
      <xsl:call-template name="root-element">
         <xsl:with-param name="entity" select="/model/entity[@root='true']"/>
      </xsl:call-template>
      
      <xsl:apply-templates select="/model/entity"/>
   </xsl:element>
</xsl:template>

<xsl:template match="entity">
   <xsl:element name="xs:complexType" namespace="{$xs-namespace}">
      <xsl:attribute name="name"><xsl:value-of select="@xsd-type"/></xsl:attribute>
      
      <xsl:choose>
         <xsl:when test="entity-ref[not(@render='false')] | element[not(@render='false' or @text='true')]">
            <xsl:element name="xs:sequence" namespace="{$xs-namespace}">
               <xsl:for-each select="element[not(@render='false' or @text='true')]">
                  <xsl:call-template name="element" />
               </xsl:for-each>

               <xsl:for-each select="entity-ref[not(@render='false')]">
                  <xsl:call-template name="entity-ref" />
               </xsl:for-each>
            </xsl:element>
         
            <xsl:for-each select="attribute[not(@render='false' or @text='true')]">
               <xsl:call-template name="attribute"/>
            </xsl:for-each>
         </xsl:when>
         <xsl:otherwise>
            <xsl:element name="xs:simpleContent" namespace="{$xs-namespace}">
               <xsl:element name="xs:extension" namespace="{$xs-namespace}">
                  <xsl:attribute name="base">xs:string</xsl:attribute>

                  <xsl:for-each select="attribute[not(@render='false' or @text='true')]">
                     <xsl:call-template name="attribute"/>
                  </xsl:for-each>
               </xsl:element>
            </xsl:element>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:element>
</xsl:template>

<xsl:template name="root-element">
   <xsl:param name="entity"/>
   
   <xsl:element name="xs:element">
      <xsl:attribute name="name"><xsl:value-of select="$entity/@name"/></xsl:attribute>
      <xsl:attribute name="type"><xsl:value-of select="$entity/@xsd-type"/></xsl:attribute>
   </xsl:element>
</xsl:template>

<xsl:template name="entity-ref">
   <xsl:variable name="name" select="@name"/>
   <xsl:variable name="entity" select="//entity[@name=$name]"/>
   
   <xsl:element name="xs:element">
      <xsl:choose>
         <xsl:when test="(@list='true' or @map='true') and @xml-indent='true'">
            <xsl:attribute name="name"><xsl:value-of select="@tag-name"/></xsl:attribute>
            <xsl:element name="xs:complexType" namespace="{$xs-namespace}">
               <xsl:element name="xs:sequence" namespace="{$xs-namespace}">
                  <xsl:attribute name="minOccurs">0</xsl:attribute>
                  <xsl:attribute name="maxOccurs">unbounded</xsl:attribute>
                  <xsl:element name="xs:element">
                     <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
                     <xsl:attribute name="type"><xsl:value-of select="$entity/@xsd-type"/></xsl:attribute>
                  </xsl:element>
               </xsl:element>
            </xsl:element>
         </xsl:when>
         <xsl:when test="@list='true' or @map='true'">
            <xsl:attribute name="minOccurs">0</xsl:attribute>
            <xsl:attribute name="maxOccurs">unbounded</xsl:attribute>
            <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
            <xsl:attribute name="type"><xsl:value-of select="$entity/@xsd-type"/></xsl:attribute>
         </xsl:when>
         <xsl:otherwise>
            <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
            <xsl:attribute name="type"><xsl:value-of select="$entity/@xsd-type"/></xsl:attribute>
            <xsl:if test="not(@required='true')">
               <xsl:attribute name="minOccurs">0</xsl:attribute>
               <xsl:attribute name="maxOccurs">1</xsl:attribute>
            </xsl:if>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:element>
</xsl:template>

<xsl:template name="element">
   <xsl:element name="xs:element">
      <xsl:choose>
         <xsl:when test="(@list='true' or @set='true') and @xml-indent='true'">
            <xsl:attribute name="name"><xsl:value-of select="@tag-name"/></xsl:attribute>
            <xsl:element name="xs:complexType" namespace="{$xs-namespace}">
               <xsl:element name="xs:sequence" namespace="{$xs-namespace}">
                  <xsl:attribute name="minOccurs">0</xsl:attribute>
                  <xsl:attribute name="maxOccurs">unbounded</xsl:attribute>
                  <xsl:element name="xs:element">
                     <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
                     <xsl:attribute name="type">
                        <xsl:call-template name="convert-type"/>
                     </xsl:attribute>
                  </xsl:element>
               </xsl:element>
            </xsl:element>
         </xsl:when>
         <xsl:when test="@list='true' or @set='true'">
            <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
            <xsl:attribute name="type">
               <xsl:call-template name="convert-type"/>
            </xsl:attribute>
            <xsl:attribute name="minOccurs">0</xsl:attribute>
            <xsl:attribute name="maxOccurs">unbounded</xsl:attribute>
         </xsl:when>
         <xsl:otherwise>
            <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
            <xsl:attribute name="type">
               <xsl:call-template name="convert-type"/>
            </xsl:attribute>
            <xsl:if test="not(@required='true')">
               <xsl:attribute name="minOccurs">0</xsl:attribute>
               <xsl:attribute name="maxOccurs">1</xsl:attribute>
            </xsl:if>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:element>
</xsl:template>

<xsl:template name="attribute">
   <xsl:element name="xs:attribute">
      <xsl:attribute name="name"><xsl:value-of select="@ref-name"/></xsl:attribute>
      <xsl:attribute name="type">
         <xsl:call-template name="convert-type"/>
      </xsl:attribute>
      <xsl:if test="@key='true' or @required='true'">
         <xsl:attribute name="use">required</xsl:attribute>
      </xsl:if>
      <xsl:choose>
         <xsl:when test="@default-value">
            <xsl:attribute name="default"><xsl:value-of select="@default-value"/></xsl:attribute>
         </xsl:when>
      </xsl:choose>
   </xsl:element>
</xsl:template>

<xsl:template name="convert-type">
   <xsl:param name="value-type" select="@value-type"/>
   
   <xsl:choose>
      <xsl:when test="$value-type = 'String'">xs:string</xsl:when>
      <xsl:when test="$value-type = 'Date'">xs:datetime</xsl:when>
      <xsl:when test="$value-type = 'Time'">xs:long</xsl:when>
      <xsl:when test="$value-type = 'boolean'">xs:boolean</xsl:when>
      <xsl:when test="$value-type = 'Boolean'">xs:boolean</xsl:when>
      <xsl:when test="$value-type = 'byte'">xs:byte</xsl:when>
      <xsl:when test="$value-type = 'Byte'">xs:byte</xsl:when>
      <xsl:when test="$value-type = 'char'">xs:string</xsl:when>
      <xsl:when test="$value-type = 'Character'">xs:string</xsl:when>
      <xsl:when test="$value-type = 'short'">xs:short</xsl:when>
      <xsl:when test="$value-type = 'Short'">xs:short</xsl:when>
      <xsl:when test="$value-type = 'int'">xs:int</xsl:when>
      <xsl:when test="$value-type = 'Integer'">xs:int</xsl:when>
      <xsl:when test="$value-type = 'long'">xs:long</xsl:when>
      <xsl:when test="$value-type = 'Long'">xs:long</xsl:when>
      <xsl:when test="$value-type = 'float'">xs:float</xsl:when>
      <xsl:when test="$value-type = 'Float'">xs:float</xsl:when>
      <xsl:when test="$value-type = 'double'">xs:double</xsl:when>
      <xsl:when test="$value-type = 'Double'">xs:double</xsl:when>
      <xsl:otherwise>xs:string</xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
