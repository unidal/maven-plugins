<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" xalan:indent-amount="3" xmlns:xalan="http://xml.apache.org/xslt" />
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
   <xsl:apply-templates select="/wizard" />
</xsl:template>

<xsl:template match="wizard">
   <taglib xmlns="http://java.sun.com/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd" version="2.1">
      <description>Application specific JSP tag library</description>
      <display-name>app</display-name>
      <tlib-version>1.2</tlib-version>
      <short-name>a</short-name>
      <tag-file>
         <name>layout</name>
         <path><xsl:value-of select="webapp/@layout-tag"/></path>
      </tag-file>
   </taglib>
</xsl:template>

</xsl:stylesheet>
