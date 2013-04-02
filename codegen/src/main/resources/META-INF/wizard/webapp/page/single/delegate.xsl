<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="module"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$module]/page[@name=$name]"/>
</xsl:template>

<xsl:template match="page">
<xsl:variable name="policy-add">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'add'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-delete">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'delete'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-edit">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'edit'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-search">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'search'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-pagination">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'pagination'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="model" select="single/model"/>
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import <xsl:value-of select="$model/@package"></xsl:value-of>.<xsl:value-of select="$model/@class-name"/>;

public class <xsl:value-of select="@delegate-class"/> {
<xsl:if test="$policy-delete='true'">
   public void delete(<xsl:value-of select="$model/field[@key='true']/@value-type"/><xsl:value-of select="$space"/><xsl:value-of select="$model/field[@key='true']/@param-name"/>) {
      // TODO to be implemented
   }
</xsl:if>
<xsl:choose>
   <xsl:when test="$policy-search='true' and $policy-pagination='true'">
   public void list(<xsl:value-of select="@model-class"/> model, String keyword, int pageNo, int pageSize) {
      // TODO to be implemented
   }
   </xsl:when>
   <xsl:when test="$policy-search='true'">
   public void list(<xsl:value-of select="@model-class"/> model, String keyword) {
      // TODO to be implemented
   }
   </xsl:when>
   <xsl:when test="$policy-pagination='true'">
   public void list(<xsl:value-of select="@model-class"/> model, int pageNo, int pageSize) {
      // TODO to be implemented
   }
   </xsl:when>
   <xsl:otherwise>
   public void list(<xsl:value-of select="@model-class"/> model) {
      // TODO to be implemented
   }
   </xsl:otherwise>
</xsl:choose>
<xsl:if test="$policy-add='true' or $policy-edit='true'">
   public void save(<xsl:value-of select="$model/@value-type"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@param-name"/>, boolean isNew) {
      // TODO to be implemented
   }
</xsl:if>
   public void view(<xsl:value-of select="@model-class"/> model, <xsl:value-of select="$model/field[@key='true']/@value-type"/><xsl:value-of select="$space"/><xsl:value-of select="$model/field[@key='true']/@param-name"/>) {
      // TODO to be implemented
   }
}
</xsl:template>

</xsl:stylesheet>
