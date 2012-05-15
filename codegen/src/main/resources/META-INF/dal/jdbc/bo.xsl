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
   <xsl:if test="@bo-package">
      <xsl:value-of select='$empty'/>package <xsl:value-of select="@bo-package"/>;<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:call-template name="import-list"/>
   <xsl:value-of select='$empty'/>public class <xsl:value-of select='@bo-class'/> implements BizObject {<xsl:value-of select='$empty-line'/>
   <xsl:call-template name="variable-definition"/>
   <xsl:call-template name="constructor"/>
   <xsl:call-template name="method-get-do"/>
   <xsl:call-template name="method-get-fields"/>
   <xsl:call-template name="method-set-fields"/>
   <xsl:value-of select='$empty'/>}<xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:value-of select='$empty'/>import com.site.dal.jdbc.BizObject;<xsl:value-of select='$empty-line'/>
   <xsl:if test="relation">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.BizObjectHelper;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="@bo-package != @do-package">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@do-class"/>;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="relation">
      <xsl:sort select="@name"/>
      
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      <xsl:if test="generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
         <xsl:if test="../@bo-package != $entity/@bo-package">
            <xsl:value-of select='$empty'/>import <xsl:value-of select="$entity/@bo-package"/>.<xsl:value-of select="$entity/@bo-class"/>;<xsl:value-of select="$empty-line"/>
        </xsl:if>
        <xsl:if test="../@bo-package != $entity/@do-package">
            <xsl:value-of select='$empty'/>import <xsl:value-of select="$entity/@do-package"/>.<xsl:value-of select="$entity/@do-class"/>;<xsl:value-of select="$empty-line"/>
        </xsl:if>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="variable-definition">
   private <xsl:value-of select='@do-class'/> m_do;
</xsl:template>

<xsl:template name="constructor">
   public <xsl:value-of select='@bo-class'/>(<xsl:value-of select='@do-class'/><xsl:value-of select="' '"/><xsl:value-of select='@param-name'/>) {
      m_do = <xsl:value-of select='@param-name'/>;
   }
</xsl:template>

<xsl:template name="method-get-do">
   public <xsl:value-of select='@do-class'/> getDo() {
      return m_do;
   }
</xsl:template>

<xsl:template name="method-get-fields">
   <xsl:for-each select="relation">
      <xsl:sort select="@name"/>
      
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@multiply='*' or @multiply='+'">
            <xsl:value-of select="$empty"/>   public List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return BizObjectHelper.wrap(m_do.<xsl:value-of select='@get-method'/>(), <xsl:value-of select="$entity/@bo-class"/>.class);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="$entity/@bo-class"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return BizObjectHelper.wrap(m_do.<xsl:value-of select='@get-method'/>(), <xsl:value-of select="$entity/@bo-class"/>.class);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>

   <xsl:for-each select="member | var">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@enum-class and @value-type='int'">
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@enum-class"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return <xsl:value-of select="@enum-class"/>.getById(m_do.<xsl:value-of select='@get-method'/>());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@enum-class and @value-type='String'">
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@enum-class"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return <xsl:value-of select="@enum-class"/>.getByName(m_do.<xsl:value-of select='@get-method'/>());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return m_do.<xsl:value-of select='@get-method'/>();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-set-fields">
   <xsl:for-each select="relation">
      <xsl:sort select="@name"/>
      
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@multiply='*' or @multiply='+'">
            <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_do.<xsl:value-of select='@set-method'/>(BizObjectHelper.unwrap(<xsl:value-of select='@param-name'/> ,<xsl:value-of select="$entity/@do-class"/>.class));<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select="$entity/@bo-class"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_do.<xsl:value-of select='@set-method'/>(BizObjectHelper.unwrap(<xsl:value-of select='@param-name'/> ,<xsl:value-of select="$entity/@do-class"/>.class));<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>

   <xsl:for-each select="member[not(@insert-expr)] | var">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@enum-class and @value-type='int'">
            <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select="@enum-class"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_do.<xsl:value-of select='@set-method'/>(<xsl:value-of select='@param-name'/>.getId());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@enum-class and @value-type='String'">
            <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select="@enum-class"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_do.<xsl:value-of select='@set-method'/>(<xsl:value-of select='@param-name'/>.getName());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select='@value-type'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_do.<xsl:value-of select='@set-method'/>(<xsl:value-of select='@param-name'/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="generic-bo-type">
   <xsl:param name="entity" select="."/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$entity/@bo-class"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
   <xsl:text> </xsl:text>
</xsl:template>

<xsl:template name="generic-do-type">
   <xsl:param name="entity" select="."/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$entity/@do-class"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
   <xsl:text> </xsl:text>
</xsl:template>

</xsl:stylesheet>
