<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>

<xsl:template name="upper-name">
   <xsl:param name="name"/>

   <xsl:value-of select="translate($name,'abcdefghijklmnopqrstuvwxyz-','ABCDEFGHIJKLMNOPQRSTUVWXYZ_')"/>
</xsl:template>

<xsl:template name="lower-name">
   <xsl:param name="name"/>

   <xsl:value-of select="translate($name,'ABCDEFGHIJKLMNOPQRSTUVWXYZ-','abcdefghijklmnopqrstuvwxyz_')"/>
</xsl:template>

<xsl:template name="capital-name">
   <xsl:param name="name"/>

   <xsl:value-of select="translate(substring($name,1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
   <xsl:value-of select="substring($name,2)"/>
</xsl:template>

</xsl:stylesheet>