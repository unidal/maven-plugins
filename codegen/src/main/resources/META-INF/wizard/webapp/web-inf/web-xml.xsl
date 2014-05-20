<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" xalan:indent-amount="3" xmlns:xalan="http://xml.apache.org/xslt"/>
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
   <xsl:apply-templates select="/wizard" />
</xsl:template>

<xsl:template match="wizard">
<web-app xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" version="2.5">
   <filter>
      <filter-name>mvc-filter</filter-name>
      <filter-class>org.unidal.web.MVCFilter</filter-class>
      <init-param>
         <param-name>cat-client-xml</param-name>
         <param-value>/data/appdatas/cat/client.xml</param-value>
      </init-param>
      <init-param>
         <param-name>liger-home</param-name>
         <param-value>/data/appdatas/liger</param-value>
      </init-param>
      <init-param>
         <param-name>excludes</param-name>
         <param-value>/css/*,/img/*,/js/*</param-value>
      </init-param>
   </filter>
   <filter-mapping>
      <filter-name>mvc-filter</filter-name>
      <url-pattern>/*</url-pattern>
   </filter-mapping>
</web-app>
</xsl:template>

</xsl:stylesheet>
