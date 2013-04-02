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
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;
<xsl:if test="single/model/field[@multiple='true']">
import java.util.List;
</xsl:if>
import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
<xsl:variable name="type">
   <xsl:value-of select="../@page-class"/>
   <xsl:value-of select="', '"/>
   <xsl:value-of select="@action-class"/>
</xsl:variable>
public class <xsl:value-of select="@payload-class"/> implements ActionPayload<xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> {
   private <xsl:value-of select="../@page-class"/> m_page;

   @FieldMeta("op")
   private <xsl:value-of select="@action-class"/> m_action;<xsl:value-of select="$empty-line"/>
<xsl:if test="$policy-search='true'">
   @FieldMeta("_keyword")
   private String m_searchKeyword;

   @FieldMeta("_search")
   private boolean m_searchSubmit;<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:if test="$policy-pagination='true'">
   @FieldMeta("_page")
   private int m_pageNo;

   @FieldMeta("_pageSize")
   private int m_pageSize;<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:if test="$policy-add='true' or $policy-edit='true'">
   @FieldMeta("_edit")
   private boolean m_editMode;
   
   @FieldMeta("_save")
   private boolean m_save;

   @FieldMeta("_cancel")
   private boolean m_cancel;<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:if test="$policy-delete='true'">
   @FieldMeta("_id")
   private int[] m_keyIds;<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:for-each select="single/model/field">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   @FieldMeta("<xsl:value-of select="@name"/>")<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   private <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@field-name"/>;<xsl:value-of select="$empty-line"/>
</xsl:for-each>
   @Override
   public <xsl:value-of select="@action-class"/> getAction() {
      return m_action;
   }

   @Override
   public <xsl:value-of select="../@page-class"/> getPage() {
      return m_page;
   }
<xsl:for-each select="single/model/field">
   <xsl:sort select="@name"/>
   
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@get-method"/>() {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      return <xsl:value-of select="@field-name"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:for-each>
<xsl:if test="$policy-delete='true'">
   public int[] getKeyIds() {
      return m_keyIds;
   }<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:if test="$policy-pagination='true'">
   public int getPageNo() {
      return m_pageNo;
   }

   public int getPageSize() {
      return m_pageSize;
   }<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:if test="$policy-search='true'">
   public String getSearchKeyword() {
      return m_searchKeyword;
   }

   public boolean isSearchSubmit() {
      return m_searchSubmit;
   }<xsl:value-of select="$empty-line"/>
</xsl:if>
<xsl:for-each select="single/model/field[@value-type='Boolean']">
   <xsl:sort select="@name"/>
   
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public boolean <xsl:value-of select="@is-method"/>() {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      return <xsl:value-of select="@field-name"/> == null ? false : <xsl:value-of select="@field-name"/>.booleanValue();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:for-each>
<xsl:if test="$policy-add='true' or $policy-edit='true'">
   public boolean isCancel() {
      return m_cancel;
   }

   public boolean isEditMode() {
      return m_editMode;
   }

   public boolean isSave() {
      return m_save;
   }<xsl:value-of select="$empty-line"/>
</xsl:if>
   public void setAction(String action) {
      m_action = <xsl:value-of select="@action-class"/>.getByName(action, <xsl:value-of select="@action-class"/>.VIEW);
   }

   @Override
   public void setPage(String page) {
      m_page = <xsl:value-of select="../@page-class"/>.getByName(page, <xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>);
   }

   @Override
   public void validate(ActionContext<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'?'"/></xsl:call-template> ctx) {
      if (m_action == null) {
         m_action = <xsl:value-of select="@action-class"/>.LIST;
      }

      if (m_pageNo <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>= 0) {
         m_pageNo = 1;
      }

      if (m_pageSize <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>= 0) {
         m_pageSize = <xsl:value-of select="single/model/@page-size"/>;
      }
   }
}
</xsl:template>

</xsl:stylesheet>
