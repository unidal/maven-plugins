<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" omit-xml-declaration="yes" encoding="utf-8" xalan:indent-amount="3" xmlns:xalan="http://xml.apache.org/xslt"/>
<xsl:param name="package"/>
<xsl:param name="module"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="model" select="/wizard/webapp/module[@name=$module]/page[@name=$name]/single/model"/>
<xsl:variable name="view" select="/wizard/webapp/module[@name=$module]/page[@name=$name]/single/view"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$module]/page[@name=$name]"/>
</xsl:template>

<xsl:template match="page">
   <xsl:call-template name="directive"><xsl:with-param name="body">page contentType="text/html; charset=utf-8"</xsl:with-param></xsl:call-template>
   <xsl:if test="/wizard/webapp/@layout='bootstrap'">
      <xsl:call-template name="directive"><xsl:with-param name="body">taglib prefix="a" uri="/WEB-INF/app.tld"</xsl:with-param></xsl:call-template>
      <xsl:call-template name="directive"><xsl:with-param name="body">taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"</xsl:with-param></xsl:call-template>
      <xsl:call-template name="directive"><xsl:with-param name="body">taglib prefix="w" uri="http://www.unidal.org/web/core"</xsl:with-param></xsl:call-template>
   </xsl:if>
   <xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="ctx" type="<xsl:value-of select="$package"/>.Context" scope="request"</xsl:with-param></xsl:call-template>
   <xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="payload" type="<xsl:value-of select="$package"/>.Payload" scope="request"</xsl:with-param></xsl:call-template>
   <xsl:call-template name="tag"><xsl:with-param name="body">jsp:useBean id="model" type="<xsl:value-of select="$package"/>.Model" scope="request"</xsl:with-param></xsl:call-template>
   
   <xsl:value-of select="$empty-line"/>
   <xsl:choose>
      <xsl:when test="/wizard/webapp/@layout='bootstrap'">
         <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>a:layout<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/><xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:call-template name="body"/><xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>/a:layout<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="body"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="body">
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
   <xsl:variable name="key-el">
      <xsl:value-of select="'${'"/>
      <xsl:value-of select="$model/@param-name"/>
      <xsl:value-of select="'.'"/>
      <xsl:value-of select="$model/field[@key='true']/@property"/>
      <xsl:value-of select="'}'"/>
   </xsl:variable>

   <xsl:call-template name="element">
      <xsl:with-param name="name" select="'c:set'"/>
      <xsl:with-param name="attributes"> var="<xsl:value-of select="$model/@param-name"/>" value="${model.<xsl:value-of select="$model/@param-name"/>}"</xsl:with-param>
   </xsl:call-template>
   <xsl:value-of select="$empty-line"/>

   <br/><xsl:value-of select="$empty-line"/><xsl:value-of select="$empty-line"/>
   
   <form>
      <xsl:call-template name="element">
         <xsl:with-param name="indent" select="'   '"/>
         <xsl:with-param name="name" select="'input'"/>
         <xsl:with-param name="attributes"> type="hidden" name="op" value="${payload.action.name}"</xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="element">
         <xsl:with-param name="indent" select="'   '"/>
         <xsl:with-param name="name" select="'input'"/>
         <xsl:with-param name="attributes"> type="hidden" name="<xsl:value-of select="$model/field[@key='true']/@property"/>" value="${payload.<xsl:value-of select="$model/field[@key='true']/@property"/>}"</xsl:with-param>
      </xsl:call-template>
      <xsl:value-of select="$empty-line"/><xsl:value-of select="'   '"/>

      <fieldset>
         <xsl:for-each select="$view/node()[name()]">
            <xsl:call-template name="node">
               <xsl:with-param name="indent" select="'      '"/>
               <xsl:with-param name="last" select="position()=last()" />
            </xsl:call-template>
         </xsl:for-each>
         <xsl:call-template name="footer"/>
      </fieldset>
   </form>
</xsl:template>

<xsl:template name="node">
   <xsl:param name="indent"/>
   <xsl:param name="last" select="false()"/>
   
   <xsl:choose>
      <xsl:when test="name()='group'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'div'"/>
            <xsl:with-param name="attributes">
               <xsl:text> class="control-group"</xsl:text>
               <xsl:call-template name="attributes"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="children">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="name()='title'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'label'"/>
            <xsl:with-param name="attributes">
               <xsl:call-template name="attributes"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'strong'"/>
                  <xsl:with-param name="body" select="normalize-space(text())"/>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="name()='text'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'input'"/>
            <xsl:with-param name="attributes">
               <xsl:value-of select="$empty"/> type="text" value="${<xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@name"/>}"<xsl:value-of select="$empty"/>
               <xsl:call-template name="attributes"/>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="name()='checkbox'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'c:forEach'"/>
            <xsl:with-param name="attributes">
                <xsl:value-of select="$empty"/> var="option" items="${model.options.<xsl:value-of select="@name"/>}"<xsl:value-of select="$empty"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'label'"/>
                  <xsl:with-param name="attributes"> class="checkbox"</xsl:with-param>
                  <xsl:with-param name="body">
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '      ')"/>
                     <xsl:call-template name="lt"/>input type="checkbox" value="${option.key}"<xsl:value-of select="$empty"/>
                     <xsl:call-template name="attributes"/>
                     <xsl:value-of select="$empty"/>${w:in(<xsl:value-of select="$model/@param-name"/>.<xsl:call-template name="property"/>, option.key) ? ' checked' : ''}<xsl:value-of select="$empty"/>
                     <xsl:call-template name="gt"/> ${option.value}<xsl:value-of select="$empty"/>
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '   ')"/>
                  </xsl:with-param>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="name()='radio'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'c:forEach'"/>
            <xsl:with-param name="attributes">
                <xsl:value-of select="$empty"/> var="option" items="${model.options.<xsl:value-of select="@name"/>}"<xsl:value-of select="$empty"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'label'"/>
                  <xsl:with-param name="attributes"> class="radio"</xsl:with-param>
                  <xsl:with-param name="body">
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '      ')"/>
                     <xsl:call-template name="lt"/>input type="radio" value="${option.key}"<xsl:value-of select="$empty"/>
                     <xsl:call-template name="attributes"/>
                     <xsl:value-of select="$empty"/>${w:in(<xsl:value-of select="$model/@param-name"/>.<xsl:value-of select="@name"/>, option.key) ? ' checked' : ''}<xsl:value-of select="$empty"/>
                     <xsl:call-template name="gt"/> ${option.value}<xsl:value-of select="$empty"/>
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '   ')"/>
                  </xsl:with-param>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="name()='select'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'select'"/>
            <xsl:with-param name="attributes">
                <xsl:value-of select="$empty"/><xsl:call-template name="attributes"/><xsl:value-of select="$empty"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'c:forEach'"/>
                  <xsl:with-param name="attributes"> var="option" items="${model.options.<xsl:value-of select="@name"/>}"</xsl:with-param>
                  <xsl:with-param name="body">
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '      ')"/>
                     <xsl:call-template name="lt"/>option value="${option.key}"<xsl:value-of select="$empty"/>
                     <xsl:value-of select="$empty"/>${w:in(<xsl:value-of select="$model/@param-name"/>.<xsl:call-template name="property"/>, option.key) ? ' selected' : ''}<xsl:value-of select="$empty"/>
                     <xsl:call-template name="gt"/>${option.value}<xsl:value-of select="$empty"/>
                     <xsl:value-of select="$empty-line"/><xsl:value-of select="concat($indent, '   ')"/>
                  </xsl:with-param>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="name()"/>
            <xsl:with-param name="attributes">
               <xsl:call-template name="attributes"/>
            </xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="children">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="children">
   <xsl:param name="indent"/>

   <xsl:for-each select="node()[name()]">
      <xsl:call-template name="node">
         <xsl:with-param name="indent" select="$indent"/>
         <xsl:with-param name="last" select="position()=last()" />
      </xsl:call-template>
   </xsl:for-each>
</xsl:template>

<xsl:template name="attributes">
   <xsl:for-each select="@*">
      <xsl:value-of select="$space"/><xsl:value-of select="name()"/>="<xsl:value-of select="."/>"<xsl:value-of select="$empty"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="footer">
   <xsl:call-template name="element">
      <xsl:with-param name="indent" select="'      '"/>
      <xsl:with-param name="name" select="'c:if'"/>
      <xsl:with-param name="attributes"> test="${payload.action.name eq 'add' or payload.action.name eq 'edit' }"</xsl:with-param>
      <xsl:with-param name="body">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="'         '"/>
            <xsl:with-param name="name" select="'button'"/>
            <xsl:with-param name="attributes"> type="submit" name="_save" value="1" class="btn btn-primary"</xsl:with-param>
            <xsl:with-param name="body" select="'Save'"/>
         </xsl:call-template>
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="'         '"/>
            <xsl:with-param name="name" select="'a'"/>
            <xsl:with-param name="attributes"> href="${model.pageUri}" class="btn"</xsl:with-param>
            <xsl:with-param name="body" select="'Cancel'"/>
            <xsl:with-param name="last" select="true()"/>
         </xsl:call-template>
      </xsl:with-param>
   </xsl:call-template>
   <xsl:call-template name="element">
      <xsl:with-param name="indent" select="'      '"/>
      <xsl:with-param name="name" select="'c:if'"/>
      <xsl:with-param name="attributes"> test="${payload.action.name eq 'view' }"</xsl:with-param>
      <xsl:with-param name="body">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="'         '"/>
            <xsl:with-param name="name" select="'a'"/>
            <xsl:with-param name="attributes"> href="${model.pageUri}" class="btn"</xsl:with-param>
            <xsl:with-param name="body" select="'Back'"/>
            <xsl:with-param name="last" select="true()"/>
         </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="last" select="true()"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="property">
   <xsl:variable name="name" select="@name"/>
   <xsl:variable name="field" select="$model/field[@name=$name]"/>

   <xsl:choose>
      <xsl:when test="$field/@multiple='true' and $field/@names"><xsl:value-of select="$field/@names"/></xsl:when>
      <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>