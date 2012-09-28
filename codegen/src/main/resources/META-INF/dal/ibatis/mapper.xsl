<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" xalan:indent-amount="3"
      xmlns:xalan="http://xml.apache.org/xslt" />
   <xsl:param name="name" />
   <xsl:variable name="space" select="' '" />
   <xsl:variable name="empty" select="''" />
   <xsl:variable name="empty-line" select="'&#x0A;'" />

   <xsl:template match="/">
      <xsl:apply-templates select="/entities/entity[@name = $name]" />
   </xsl:template>

   <xsl:template match="entity">
      <xsl:variable name="entity" select="."/>
      <xsl:variable name="package" select="@do-package" />
      <xsl:variable name="parameter-type">
         <xsl:value-of select="@do-package" />
         <xsl:text>.</xsl:text>
         <xsl:value-of select="@do-class" />
      </xsl:variable>
      <mapper>
         <xsl:attribute name="namespace"><xsl:value-of select="$parameter-type" />Mapper</xsl:attribute>

         <xsl:for-each select="query-defs/query[@type='INSERT']">
            <insert id="{@name}" parameterType="{$parameter-type}">
               <xsl:value-of select="$empty-line" />      INSERT INTO <xsl:value-of select="$entity/@table"/> (<xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member">
                  <xsl:value-of select="@field"/>
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty" />)<xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      VALUES (<xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member">
                  <xsl:value-of select="$empty" />#{<xsl:value-of select="@field"/>}<xsl:value-of select="$empty" />
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty" />)<xsl:value-of select="$empty-line" />
               <xsl:text>   </xsl:text>
            </insert>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='SELECT']">
            <select id="{@name}" parameterType="{$parameter-type}">
               SELECT FROM
            </select>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='DELETE']">
            <delete id="{@name}" parameterType="{$parameter-type}">
               SELECT FROM
            </delete>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='UPDATE']">
            <update id="{@name}" parameterType="{$parameter-type}">
               SELECT FROM
            </update>
         </xsl:for-each>
      </mapper>
   </xsl:template>

</xsl:stylesheet>
