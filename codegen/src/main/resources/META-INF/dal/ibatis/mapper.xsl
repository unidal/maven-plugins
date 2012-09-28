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

         <xsl:for-each select="query-defs/query[@type='SELECT']">
            <xsl:variable name="type">
               <xsl:choose>
                  <xsl:when test="contains(@name, 'ByPK')"><xsl:value-of select="$entity/member[@key='true']/@value-type"/></xsl:when>
                  <xsl:otherwise><xsl:value-of select="$parameter-type"/></xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <select id="{@name}" parameterType="{$type}" returnType="{$parameter-type}">
               <xsl:value-of select="$empty-line" />      SELECT <xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member">
                  <xsl:value-of select="@field"/>
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      FROM <xsl:value-of select="$entity/@table"/><xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      WHERE <xsl:value-of select="$empty" />
               <xsl:choose>
                  <xsl:when test="contains(@name, 'ByPK')"><xsl:value-of select="$entity/member[@key='true']/@field"/> = #{<xsl:value-of select="$entity/member[@key='true']/@field"/>}</xsl:when>
                  <xsl:otherwise>1 = 0</xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$empty-line" />
               <xsl:text>   </xsl:text>
            </select>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='INSERT']">
            <insert id="{@name}" parameterType="{$parameter-type}">
               <xsl:value-of select="$empty-line" />      INSERT INTO <xsl:value-of select="$entity/@table"/> (<xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member[not(@auto-increment='true')]">
                  <xsl:value-of select="@field"/>
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty" />)<xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      VALUES (<xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member[not(@auto-increment='true')]">
                  <xsl:choose>
                     <xsl:when test="@insert-expr">
                        <xsl:value-of select="@insert-expr"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$empty" />#{<xsl:value-of select="@field"/>}<xsl:value-of select="$empty" />
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty" />)<xsl:value-of select="$empty-line" />
               <xsl:text>   </xsl:text>
            </insert>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='DELETE']">
            <xsl:variable name="type">
               <xsl:choose>
                  <xsl:when test="contains(@name, 'ByPK')"><xsl:value-of select="$entity/member[@key='true']/@value-type"/></xsl:when>
                  <xsl:otherwise><xsl:value-of select="$parameter-type"/></xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <delete id="{@name}" parameterType="{$type}">
               <xsl:value-of select="$empty-line" />      DELETE FROM <xsl:value-of select="$entity/@table"/><xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      WHERE <xsl:value-of select="$empty" />
               <xsl:choose>
                  <xsl:when test="contains(@name, 'ByPK')"><xsl:value-of select="$entity/member[@key='true']/@field"/> = #{<xsl:value-of select="$entity/member[@key='true']/@field"/>}</xsl:when>
                  <xsl:otherwise>1 = 0</xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$empty-line" />
               <xsl:text>   </xsl:text>
            </delete>
         </xsl:for-each>

         <xsl:for-each select="query-defs/query[@type='UPDATE']">
            <update id="{@name}" parameterType="{$parameter-type}">
               <xsl:value-of select="$empty-line" />      UPDATE <xsl:value-of select="$entity/@table"/><xsl:value-of select="$empty" />
               <xsl:value-of select="$empty-line" />      SET <xsl:value-of select="$empty" />
               <xsl:for-each select="$entity/member[not(@auto-increment='true')][not(@insert-expr and not(@update-expr))]">
                  <xsl:choose>
                     <xsl:when test="@update-expr">
                        <xsl:value-of select="@field"/> = <xsl:value-of select="@update-expr"/><xsl:value-of select="$empty" />
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="@field"/> = #{<xsl:value-of select="@field"/>}<xsl:value-of select="$empty" />
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:if test="position()!=last()">, </xsl:if>
               </xsl:for-each>
               <xsl:value-of select="$empty-line" />
               <xsl:value-of select="$empty" />      WHERE <xsl:value-of select="$empty" />
               <xsl:choose>
                  <xsl:when test="contains(@name, 'ByPK')"><xsl:value-of select="$entity/member[@key='true']/@field"/> = #{<xsl:value-of select="$entity/member[@key='true']/@field"/>}</xsl:when>
                  <xsl:otherwise>1 = 0</xsl:otherwise>
               </xsl:choose>
               <xsl:value-of select="$empty-line" />
               <xsl:text>   </xsl:text>
            </update>
         </xsl:for-each>
      </mapper>
   </xsl:template>

</xsl:stylesheet>
