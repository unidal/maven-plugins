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

import java.io.IOException;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import <xsl:value-of select="../@package"></xsl:value-of>.<xsl:value-of select="../@page-class"/>;
import <xsl:value-of select="$model/@package"></xsl:value-of>.<xsl:value-of select="$model/@class-name"/>;

public class <xsl:value-of select="@handler-class"/> implements PageHandler<xsl:call-template name="generic-type"><xsl:with-param name="type" select="@context-class"/></xsl:call-template> {
   @Inject
   private <xsl:value-of select="@jsp-viewer-class"/> m_jspViewer;

   @Inject
   private <xsl:value-of select="@delegate-class"/> m_delegate;

   @Override
   @PayloadMeta(<xsl:value-of select="@payload-class"/>.class)
   @InboundActionMeta(name = "<xsl:value-of select="@path"/>")
   public void handleInbound(<xsl:value-of select="@context-class"/> ctx) throws ServletException, IOException {
      if (!ctx.hasErrors()) {
         Payload payload = ctx.getPayload();
         Action action = payload.getAction();

         switch (action) {<xsl:value-of select="$empty"/>
<xsl:if test="$policy-delete='true'">
         case DELETE:
            try {
               m_delegate.delete(payload.<xsl:value-of select="$model/field[@key='true']/@get-method"/>());
            } catch (Exception e) {
               ctx.addError("delete", e);
            }
            break;<xsl:value-of select="$empty"/>
</xsl:if>
<xsl:if test="$policy-add='true'">
         case ADD:
            if (payload.isSave()) {
               <xsl:value-of select="$model/@class-name"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@param-name"/> = new <xsl:value-of select="$model/@class-name"/>();<xsl:value-of select="$empty-line"/>
               
               <xsl:value-of select="$empty-line"/>
               <xsl:for-each select="$model/field[not(@key='true')]">
                  <xsl:choose>
                     <xsl:when test="@multiple='true'">
                        <xsl:value-of select="'               '"/><xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@get-method"/>().addAll(payload.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="'               '"/><xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@set-method"/>(payload.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:for-each>
               try {
                  m_delegate.save(<xsl:value-of select="$model/@param-name"/>, true);
               } catch (Exception e) {
                  ctx.addError("add", e);
               }

               ctx.redirect(ctx.getRequestContext().getActionUri(<xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>.getName()));
            }

            break;<xsl:value-of select="$empty"/>
</xsl:if>
<xsl:if test="$policy-edit='true'">
         case EDIT:
            if (payload.isSave()) {
               <xsl:value-of select="$model/@class-name"/><xsl:value-of select="$space"/><xsl:value-of select="$model/@param-name"/> = new <xsl:value-of select="$model/@class-name"/>();<xsl:value-of select="$empty-line"/>
               
               <xsl:value-of select="$empty-line"/>
               <xsl:for-each select="$model/field">
                  <xsl:choose>
                     <xsl:when test="@multiple='true'">
                        <xsl:value-of select="'               '"/><xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@get-method"/>().addAll(payload.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="'               '"/><xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@set-method"/>(payload.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:for-each>
               try {
                  m_delegate.save(<xsl:value-of select="$model/@param-name"/>, false);
               } catch (Exception e) {
                  ctx.addError("edit", e);
               }

               ctx.redirect(ctx.getRequestContext().getActionUri(<xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>.getName()));
            }

            break;<xsl:value-of select="$empty"/>
</xsl:if>
         }
      }
   }

   @Override
   @OutboundActionMeta(name = "<xsl:value-of select="@path"/>")
   public void handleOutbound(<xsl:value-of select="@context-class"/> ctx) throws ServletException, IOException {
      <xsl:value-of select="@model-class"/> model = new <xsl:value-of select="@model-class"/>(ctx);
      <xsl:value-of select="@payload-class"/> payload = ctx.getPayload();
      Action action = payload.getAction();

      switch (action) {
      case LIST:<xsl:value-of select="$empty"/>
<xsl:choose>
   <xsl:when test="$policy-search='true' and $policy-pagination='true'">
         int pageNo = payload.isSearchSubmit() ? 1 : payload.getPageNo();

         m_delegate.list(model, payload.getSearchKeyword(), pageNo, payload.getPageSize());<xsl:value-of select="$empty"/>
   </xsl:when>
   <xsl:when test="$policy-pagination='true'">
         m_delegate.list(model, payload.getPageNo(), payload.getPageSize());<xsl:value-of select="$empty"/>
   </xsl:when>
   <xsl:when test="$policy-search='true'">
         m_delegate.list(model, payload.getSearchKeyword());<xsl:value-of select="$empty"/>
   </xsl:when>
   <xsl:otherwise>
         m_delegate.list(model);<xsl:value-of select="$empty"/>
   </xsl:otherwise>
</xsl:choose>
         break;<xsl:value-of select="$empty"/>
<xsl:if test="$policy-add='true'">
      case ADD:<xsl:value-of select="$empty"/>
</xsl:if>
<xsl:if test="$policy-edit='true'">
      case EDIT:<xsl:value-of select="$empty"/>
</xsl:if>
      case VIEW:
         m_delegate.view(model, payload.<xsl:value-of select="$model/field[@key='true']/@get-method"/>());
         break;
      }

      model.setAction(Action.VIEW);
      model.setPage(<xsl:value-of select="../@page-class"/>.<xsl:value-of select="@upper-name"/>);
      m_jspViewer.view(ctx, model);
   }
}
</xsl:template>

</xsl:stylesheet>
