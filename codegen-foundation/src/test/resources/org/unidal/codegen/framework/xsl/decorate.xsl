<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="no" media-type="text/xml" encoding="utf-8" />

   <xsl:template match="/">
      <xsl:apply-templates />
   </xsl:template>

   <xsl:template match="model">
      <xsl:copy>
         <xsl:copy-of select="@*" />

         <xsl:attribute name="decorated">true</xsl:attribute>

         <xsl:apply-templates />
      </xsl:copy>
   </xsl:template>

   <xsl:template match="@*|node()">
      <xsl:copy>
         <xsl:copy-of select="@*" />

         <xsl:if test="node()">
            <xsl:apply-templates />
         </xsl:if>
      </xsl:copy>
   </xsl:template>

</xsl:stylesheet>