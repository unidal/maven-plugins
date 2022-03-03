<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:n="org.unidal.maven.plugin.codegen.function.Normalizer" extension-element-prefixes="n">

<xsl:template name="normalize-value-type">
   <xsl:param name="value-type"/>
   <xsl:param name="is-ref" select="0"/>
   
   <xsl:choose>
      <xsl:when test="$is-ref">
         <xsl:choose>
            <xsl:when test="$value-type = 'Date'">Ref&#x3c;java.util.Date&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'Time'">Ref&#x3c;Long&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'boolean'">Ref&#x3c;Boolean&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'byte'">Ref&#x3c;Byte&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'char'">Ref&#x3c;Character&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'short'">Ref&#x3c;Short&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'int'">Ref&#x3c;Integer&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'long'">Ref&#x3c;Long&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'float'">Ref&#x3c;Float&#x3e;</xsl:when>
            <xsl:when test="$value-type = 'double'">Ref&#x3c;Double&#x3e;</xsl:when>
            <xsl:otherwise>Ref&#x3c;<xsl:value-of select="$value-type"/>&#x3e;</xsl:otherwise>
         </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
         <xsl:choose>
            <xsl:when test="$value-type = 'Date'">java.util.Date</xsl:when>
            <xsl:when test="$value-type = 'Time'">long</xsl:when>
            <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
         </xsl:choose>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="normalize-key-type">
   <xsl:param name="value-type"/>
   
   <xsl:choose>
      <xsl:when test="$value-type = 'Date'">java.util.Date</xsl:when>
      <xsl:when test="$value-type = 'Time'">Long</xsl:when>
      <xsl:when test="$value-type = 'boolean'">Boolean</xsl:when>
      <xsl:when test="$value-type = 'byte'">Byte</xsl:when>
      <xsl:when test="$value-type = 'char'">Character</xsl:when>
      <xsl:when test="$value-type = 'short'">Short</xsl:when>
      <xsl:when test="$value-type = 'int'">Integer</xsl:when>
      <xsl:when test="$value-type = 'long'">Long</xsl:when>
      <xsl:when test="$value-type = 'float'">Float</xsl:when>
      <xsl:when test="$value-type = 'double'">Double</xsl:when>
      <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="normalize">
   <xsl:param name="source"/>
   <xsl:param name="pos" select="1"/>

   <xsl:value-of select="n:normalize($source)" />
</xsl:template>

<xsl:template name="replace">
   <xsl:param name="text"/>
   <xsl:param name="from"/>
   <xsl:param name="to"/>

   <xsl:choose>
      <xsl:when test="contains($text,$from)">
         <xsl:value-of select="substring-before($text,$from)"/>
         <xsl:value-of select="$to"/>
         <xsl:call-template name="replace">
            <xsl:with-param name="text" select="substring-after($text,$from)"/>
            <xsl:with-param name="from" select="$from"/>
            <xsl:with-param name="to" select="$to"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$text"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>  

<xsl:template name="lt">
   <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template name="gt">
   <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template name="generic-type">
   <xsl:param name="type"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$type" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
</xsl:template>

</xsl:stylesheet>