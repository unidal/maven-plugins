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
<xsl:variable name="policy-pagination">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'pagination'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="model" select="single/model"/>
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.List;
import java.util.Map;

import org.unidal.web.mvc.ViewModel;

import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
import <xsl:value-of select="$model/@package"></xsl:value-of>.<xsl:value-of select="$model/@class-name"/>;
<xsl:variable name="type">
   <xsl:value-of select="../@page-class"/>
   <xsl:value-of select="', '"/>
   <xsl:value-of select="@action-class"/>
   <xsl:value-of select="', '"/>
   <xsl:value-of select="@context-class"/>
</xsl:variable>
public class <xsl:value-of select="@model-class"/> extends ViewModel<xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> {
<xsl:if test="$policy-pagination='true'">
   private <xsl:value-of select="$model/@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@field-name"/>;

   private <xsl:value-of select="$model/@value-types" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@field-names"/>;

   private int m_currentPage;

   private int m_maxPage;

   private int m_startPage;

   private int m_endPage;
</xsl:if>

   private Map<xsl:call-template name="lt"/>String, Map<xsl:call-template name="lt"/>String, String<xsl:call-template name="gt"/><xsl:call-template name="gt"/> m_allOptions;

   public <xsl:value-of select="@model-class"/>(Context ctx) {
      super(ctx);
   }

   @Override
   public <xsl:value-of select="@action-class"/> getDefaultAction() {
      return <xsl:value-of select="@action-class"/>.VIEW;
   }

   public Map<xsl:call-template name="lt"/>String, Map<xsl:call-template name="lt"/>String, String<xsl:call-template name="gt"/><xsl:call-template name="gt"/> getAllOptions() {
      return m_allOptions;
   }

   public <xsl:value-of select="$model/@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@get-method"/>() {
      return <xsl:value-of select="$model/@field-name"/>;
   }

   public <xsl:value-of select="$model/@value-types" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@get-methods"/>() {
      return <xsl:value-of select="$model/@field-names"/>;
   }
<xsl:if test="$policy-pagination='true'">
   public int getCurrentPage() {
      return m_currentPage;
   }

   public int getEndPage() {
      return m_endPage;
   }

   public int getMaxPage() {
      return m_maxPage;
   }

   public int getStartPage() {
      return m_startPage;
   }
</xsl:if>

   public void setAllOptions(Map<xsl:call-template name="lt"/>String, Map<xsl:call-template name="lt"/>String, String<xsl:call-template name="gt"/><xsl:call-template name="gt"/> allOptions) {
      m_allOptions = allOptions;
   }

   public void <xsl:value-of select="$model/@set-method"/>(<xsl:value-of select="$model/@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@param-name"/>) {
      <xsl:value-of select="$model/@field-name"/> = <xsl:value-of select="$model/@param-name"/>;
   }

   public void <xsl:value-of select="$model/@set-methods"/>(<xsl:value-of select="$model/@value-types" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@param-names"/>) {
      <xsl:value-of select="$model/@field-names"/> = <xsl:value-of select="$model/@param-names"/>;
   }
<xsl:if test="$policy-pagination='true'">
   public void setCurrentPage(int currentPage) {
      m_currentPage = currentPage;
   }

   public void setEndPage(int endPage) {
      m_endPage = endPage;
   }

   public void setMaxPage(int maxPage) {
      m_maxPage = maxPage;
   }

   public void setStartPage(int startPage) {
      m_startPage = startPage;
   }
}
</xsl:if>
</xsl:template>

</xsl:stylesheet>
