<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../naming.xsl" />
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" xalan:indent-amount="3" xmlns:xalan="http://xml.apache.org/xslt"/>
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
<xsl:apply-templates select="/wizard/webapp" />
</xsl:template>

<xsl:template match="webapp">
   <config mode="client">
      <domain>
         <xsl:attribute name="id">
            <xsl:value-of select="@capital-name"/>
         </xsl:attribute>
      </domain>
   </config>
</xsl:template>

</xsl:stylesheet>
