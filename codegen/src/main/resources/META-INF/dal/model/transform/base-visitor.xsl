<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public abstract class BaseVisitor implements IVisitor {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-visit'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="//entity[@all-children-in-sequence='true']">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.BaseEntity;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.Any;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-visit">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="entity/any/@visit-method"/>(Any any) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>
      
      <xsl:variable name="entity" select="."/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@all-children-in-sequence='true'">
            <xsl:value-of select="$empty"/>      for (BaseEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> child : <xsl:value-of select="@param-name"/>.<xsl:value-of select="@method-get-all-children-in-sequence"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         child.accept(this);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="entity-ref">
            <xsl:for-each select="entity-ref">
               <xsl:variable name="name" select="@name"/>
               <xsl:variable name="current" select="//entity[@name=$name]"/>
               <xsl:choose>
                  <xsl:when test="@list='true'">
                     <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
                  </xsl:when>
                  <xsl:when test="@map='true'">
                     <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().values()) {<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
                  </xsl:otherwise>
               </xsl:choose>
               <xsl:if test="position()!=last()">
                  <xsl:value-of select="$empty-line"/>
               </xsl:if>
            </xsl:for-each>
         </xsl:when>
      </xsl:choose>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:if test="position()!=last()">
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
