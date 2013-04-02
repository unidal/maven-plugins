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
   <xsl:call-template name="lt"/>link href="${model.webapp}/css/<xsl:value-of select="@name"/>.css" type="text/css" rel="stylesheet"<xsl:call-template name="gt2"/>
   <xsl:call-template name="lt"/>script src="${model.webapp}/js/<xsl:value-of select="@name"/>.js" type="text/javascript"<xsl:call-template name="gt"/><xsl:call-template name="lt"/>/script<xsl:call-template name="gt2"/>

   <br/><xsl:value-of select="$empty-line"/>
   
   <form>
      <div class="input-append">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="'      '"/>
            <xsl:with-param name="name" select="'input'"/>
            <xsl:with-param name="attributes"> type="text" name="_keyword" value="${payload.searchKeyword}" placeholder="Keyword" class="span2"</xsl:with-param>
         </xsl:call-template>
         <xsl:value-of select="$empty-line"/><xsl:value-of select="'      '"/>
         <button type="submit" name="_search" class="btn">Go!</button>
      </div>
      <table class="table table-condensed table-hover table-bordered">
         <thead>
            <tr>
               <th><input id="all" type="checkbox"/></th>
               <xsl:for-each select="$model/field">
                  <th><xsl:value-of select="@title"/></th>
               </xsl:for-each>
               <th></th>
            </tr>
         </thead>
         <tbody>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="'         '"/>
            <xsl:call-template name="lt"/>c:forEach var="<xsl:value-of select="$model/@param-name"/>" items="${model.<xsl:value-of select="$model/@param-names"/>}"<xsl:call-template name="gt2"/>
            <xsl:value-of select="'         '"/>
            <tr>
               <td><input type="checkbox" name="_id" value="{$key-el}"/></td>
               <xsl:for-each select="$model/field">
                  <td>
                     <xsl:choose>
                        <xsl:when test="@key='true'">
                           <xsl:call-template name="element">
                              <xsl:with-param name="indent" select="'               '"/>
                              <xsl:with-param name="name" select="'a'"/>
                              <xsl:with-param name="attributes"> href="?op=view&amp;<xsl:value-of select="$model/field[@key='true']/@property"/>=<xsl:value-of select="$key-el"/>"</xsl:with-param>
                              <xsl:with-param name="body">
                                 <xsl:call-template name="field">
                                    <xsl:with-param name="indent" select="'                  '"/>
                                    <xsl:with-param name="entity-name" select="$model/@param-name"/>
                                 </xsl:call-template>
                              </xsl:with-param>
                              <xsl:with-param name="last" select="true()"/>
                           </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                           <xsl:call-template name="field">
                              <xsl:with-param name="indent" select="'               '"/>
                              <xsl:with-param name="entity-name" select="$model/@param-name"/>
                           </xsl:call-template>
                        </xsl:otherwise>
                     </xsl:choose>
                  </td>
               </xsl:for-each>
               <td><xsl:call-template name="lt"/>a href="?op=edit<xsl:call-template name="amp"/>id=<xsl:value-of select="$key-el"/>" class="btn btn-primary btn-small"<xsl:call-template name="gt"/>Edit<xsl:call-template name="lt"/>/a<xsl:call-template name="gt"/></td>
            </tr>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="'         '"/>
            <xsl:call-template name="lt"/>/c:forEach<xsl:call-template name="gt2"/>
            <xsl:value-of select="'         '"/>
            <tr>
               <td style="background-color: white;">
                  <xsl:attribute name="colspan"><xsl:value-of select="count($model/field)+2"/></xsl:attribute>
                  <xsl:call-template name="footer"/>
               </td>
            </tr>
        </tbody>
      </table>
   </form>
</xsl:template>

<xsl:template name="field">
   <xsl:param name="indent"/>
   <xsl:param name="entity-name"/>
   <xsl:param name="last" select="true()"/>
   
   <xsl:choose>
      <xsl:when test="option and @multiple='true'">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'c:forEach'"/>
            <xsl:with-param name="attributes"> var="option" items="${model.options.<xsl:value-of select="@property"/>}"</xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'c:forEach'"/>
                  <xsl:with-param name="attributes"> var="<xsl:value-of select="@property"/>" items="${<xsl:value-of select="$entity-name"/>.<xsl:call-template name="property"/>}"</xsl:with-param>
                  <xsl:with-param name="body">
                     <xsl:call-template name="element">
                        <xsl:with-param name="indent" select="concat($indent, '      ')"/>
                        <xsl:with-param name="name" select="'c:if'"/>
                        <xsl:with-param name="attributes"> test="${<xsl:value-of select="@property"/> eq option.key}"</xsl:with-param>
                        <xsl:with-param name="body">
                           <xsl:call-template name="element">
                              <xsl:with-param name="indent" select="concat($indent, '         ')"/>
                              <xsl:with-param name="name" select="'div'"/>
                              <xsl:with-param name="body">${option.value}</xsl:with-param>
                              <xsl:with-param name="last" select="true()"/>
                           </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="last" select="true()"/>
                     </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:when test="option">
         <xsl:call-template name="element">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="name" select="'c:forEach'"/>
            <xsl:with-param name="attributes"> var="option" items="${model.options.<xsl:value-of select="@property"/>}"</xsl:with-param>
            <xsl:with-param name="body">
               <xsl:call-template name="element">
                  <xsl:with-param name="indent" select="concat($indent, '   ')"/>
                  <xsl:with-param name="name" select="'c:if'"/>
                  <xsl:with-param name="attributes"> test="${<xsl:value-of select="$entity-name"/>.<xsl:value-of select="@property"/> eq option.key}"</xsl:with-param>
                  <xsl:with-param name="body">
                     <xsl:call-template name="element">
                        <xsl:with-param name="indent" select="concat($indent, '      ')"/>
                        <xsl:with-param name="name" select="'div'"/>
                        <xsl:with-param name="body">${option.value}</xsl:with-param>
                        <xsl:with-param name="last" select="true()"/>
                     </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="last" select="true()"/>
               </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="last" select="$last"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$empty"/>${<xsl:value-of select="$entity-name"/>.<xsl:value-of select="@property"/>}<xsl:value-of select="$empty"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="footer">
   <div style="width: 100%" class="pagination-centered">
      <xsl:value-of select="$empty-line"/><xsl:value-of select="'                  '"/>
      <xsl:call-template name="element">
         <xsl:with-param name="name" select="'a'"/>
         <xsl:with-param name="attributes"> href="#" id="remove" class="btn btn-small left" disabled</xsl:with-param>
         <xsl:with-param name="body" select="'Remove'"/>
      </xsl:call-template>
      <xsl:value-of select="$empty-line"/><xsl:value-of select="'                  '"/>
      <xsl:call-template name="element">
         <xsl:with-param name="name" select="'a'"/>
         <xsl:with-param name="attributes"> href="#" id="edit" class="btn btn-small left"</xsl:with-param>
         <xsl:with-param name="body" select="'Edit Mode'"/>
      </xsl:call-template>
      
      <xsl:call-template name="element">
         <xsl:with-param name="indent" select="'                  '"/>
         <xsl:with-param name="name" select="'span'"/>
         <xsl:with-param name="attributes"> class="<xsl:text disable-output-escaping="yes">pagination${model.maxPage &lt;= 1 ? ' hidden': ''}</xsl:text>"</xsl:with-param>
         <xsl:with-param name="body">
            <xsl:call-template name="element">
               <xsl:with-param name="indent" select="'                     '"/>
               <xsl:with-param name="name" select="'ul'"/>
               <xsl:with-param name="attributes"></xsl:with-param>
               <xsl:with-param name="body">
                  <xsl:call-template name="element">
                     <xsl:with-param name="indent" select="'                        '"/>
                     <xsl:with-param name="name" select="'li'"/>
                     <xsl:with-param name="attributes">${model.currentPage eq 1 ? ' class="active"' : ''}</xsl:with-param>
                     <xsl:with-param name="body">
                        <xsl:call-template name="element">
                           <xsl:with-param name="name" select="'a'"/>
                           <xsl:with-param name="attributes"> href="?_keyword=${payload.searchKeyword}"</xsl:with-param>
                           <xsl:with-param name="body" select="'First'"/>
                        </xsl:call-template>
                     </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="element">
                     <xsl:with-param name="indent" select="'                        '"/>
                     <xsl:with-param name="name" select="'c:forEach'"/>
                     <xsl:with-param name="attributes"> var="page" begin="${model.startPage}" end="${model.endPage}"</xsl:with-param>
                     <xsl:with-param name="body">
                        <xsl:call-template name="element">
                           <xsl:with-param name="indent" select="'                           '"/>
                           <xsl:with-param name="name" select="'c:choose'"/>
                           <xsl:with-param name="body">
                              <xsl:call-template name="element">
                                 <xsl:with-param name="indent" select="'                              '"/>
                                 <xsl:with-param name="name" select="'c:when'"/>
                                 <xsl:with-param name="attributes"> test="${page eq model.currentPage}"</xsl:with-param>
                                 <xsl:with-param name="body">
                                    <xsl:call-template name="element">
                                       <xsl:with-param name="indent" select="'                                 '"/>
                                       <xsl:with-param name="name" select="'li'"/>
                                       <xsl:with-param name="attributes"> class="active"</xsl:with-param>
                                       <xsl:with-param name="body">
                                          <xsl:call-template name="element">
                                             <xsl:with-param name="name" select="'a'"/>
                                             <xsl:with-param name="attributes"> href="#"</xsl:with-param>
                                             <xsl:with-param name="body" select="'${page}'"/>
                                          </xsl:call-template>
                                       </xsl:with-param>
                                       <xsl:with-param name="last" select="true()"/>
                                    </xsl:call-template>
                                 </xsl:with-param>
                              </xsl:call-template>
                              <xsl:call-template name="element">
                                 <xsl:with-param name="indent" select="'                              '"/>
                                 <xsl:with-param name="name" select="'c:otherwise'"/>
                                 <xsl:with-param name="body">
                                    <xsl:call-template name="element">
                                       <xsl:with-param name="indent" select="'                                 '"/>
                                       <xsl:with-param name="name" select="'li'"/>
                                       <xsl:with-param name="body">
                                          <xsl:call-template name="element">
                                             <xsl:with-param name="name" select="'a'"/>
                                             <xsl:with-param name="attributes"> href="?_keyword=${payload.searchKeyword}&amp;_page=${page}"</xsl:with-param>
                                             <xsl:with-param name="body" select="'${page}'"/>
                                          </xsl:call-template>
                                       </xsl:with-param>
                                       <xsl:with-param name="last" select="true()"/>
                                    </xsl:call-template>
                                 </xsl:with-param>
                                 <xsl:with-param name="last" select="true()"/>
                              </xsl:call-template>
                           </xsl:with-param>
                           <xsl:with-param name="last" select="true()"/>
                        </xsl:call-template>
                     </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="element">
                     <xsl:with-param name="indent" select="'                        '"/>
                     <xsl:with-param name="name" select="'li'"/>
                     <xsl:with-param name="attributes">${model.currentPage eq model.maxPage ? ' class="active"' : ''}</xsl:with-param>
                     <xsl:with-param name="body">
                        <xsl:call-template name="element">
                           <xsl:with-param name="name" select="'a'"/>
                           <xsl:with-param name="attributes"> href="?_keyword=${payload.searchKeyword}&amp;_page=${model.maxPage}"</xsl:with-param>
                           <xsl:with-param name="body" select="'Last'"/>
                        </xsl:call-template>
                     </xsl:with-param>
                     <xsl:with-param name="last" select="true()"/>
                  </xsl:call-template>
               </xsl:with-param>
               <xsl:with-param name="last" select="true()"/>
            </xsl:call-template>
         </xsl:with-param>
      </xsl:call-template>
      <xsl:value-of select="$empty-line"/><xsl:value-of select="'                  '"/>
      <a href="?op=add" class="btn btn-small right">Add</a>
   </div>
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