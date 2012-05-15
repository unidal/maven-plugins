<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/entities/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:variable name="package" select="@do-package"/>
   <xsl:if test="$package">
      <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class <xsl:value-of select='@do-class'/> extends DataObject {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="declare-field-variables"/>
   <xsl:call-template name="method-get-fields"/>
   <xsl:call-template name="method-set-fields"/>
   <xsl:call-template name="method-toString"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="relation | member | var">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>import static <xsl:value-of select="$entity/@do-package"/>.<xsl:value-of select="$entity/@entity-class"/>.<xsl:value-of select='@upper-name'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import com.site.dal.jdbc.DataObject;<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="relation">
      <xsl:sort select="@name"/>
      
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      <xsl:if test="generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
        <xsl:if test="../@do-package != $entity/@do-package">
            <xsl:value-of select='$empty'/>import <xsl:value-of select="$entity/@do-package"/>.<xsl:value-of select="$entity/@do-class"/>;<xsl:value-of select="$empty-line"/>
        </xsl:if>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="declare-field-variables">
   <xsl:for-each select="relation | member | var">
      <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type'/><xsl:value-of select="$space"/>
      <xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-get-fields">
   <xsl:for-each select="relation | member | var">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-set-fields">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="relation | member | var">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select='@value-type'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      setFieldUsed(<xsl:value-of select='@upper-name'/>, true);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/><xsl:text>      </xsl:text><xsl:value-of select='@field-name'/> = <xsl:value-of select='@param-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-toString">
   <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public String toString() {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      StringBuilder sb = new StringBuilder(1024);<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      sb.append("<xsl:value-of select='@do-class'/>[");<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="relation | member | var">
      <xsl:sort select="@name"/>
      
      <xsl:if test="position() = 1">      sb.append("</xsl:if>
      <xsl:if test="position() != 1">      sb.append(", </xsl:if>
  	  <xsl:value-of select='@name'/>: ").append(<xsl:value-of select="$empty"/>
      <xsl:choose>
         <xsl:when test="contains(@value-type, '[]')"><xsl:value-of select='@field-name'/> == null ? null : java.util.Arrays.asList(<xsl:value-of select='@field-name'/>)</xsl:when>
         <xsl:otherwise><xsl:value-of select='@field-name'/></xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>      sb.append("]");<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      return sb.toString();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>
</xsl:stylesheet>
