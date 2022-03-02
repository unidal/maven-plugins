<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

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

   <xsl:variable name="ch" select="substring($source,$pos,1)"/>
   <xsl:choose>
      <xsl:when test="$ch='-' or $ch='_' or $ch=':'">
         <xsl:call-template name="normalize">
            <xsl:with-param name="source">
               <xsl:value-of select="substring($source,1,$pos - 1)"/>
               <xsl:value-of select="translate(substring($source,$pos + 1,1),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')"/>
               <xsl:value-of select="substring($source,$pos + 2)"/>
            </xsl:with-param>
            <xsl:with-param name="pos" select="$pos + 1"/>
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
         <xsl:choose>
            <xsl:when test="$pos = 1">
               <xsl:call-template name="normalize">
                  <xsl:with-param name="source">
                     <xsl:value-of select="translate(substring($source,$pos,1),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
                     <xsl:value-of select="substring($source,$pos + 1)"/>
                  </xsl:with-param>
                  <xsl:with-param name="pos" select="$pos + 1"/>
               </xsl:call-template>
            </xsl:when>
            <xsl:when test="$pos &lt;= string-length($source)">
               <xsl:call-template name="normalize">
                  <xsl:with-param name="source" select="$source"/>
                  <xsl:with-param name="pos" select="$pos + 1"/>
               </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$source"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="generic-type">
   <xsl:param name="type"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$type" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
</xsl:template>

</xsl:stylesheet>