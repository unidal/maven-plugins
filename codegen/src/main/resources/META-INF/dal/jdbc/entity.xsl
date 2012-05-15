<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="generic-do-class">
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type" select="/entities/entity[@name = $name]/@do-class"/>
   </xsl:call-template>
</xsl:variable>

<xsl:template match="/">
   <xsl:apply-templates select="/entities/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:variable name="do-package" select="@do-package"/>
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$do-package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>@Entity(logicalName = "<xsl:value-of select='@name'/>", alias = "<xsl:value-of select='@alias'/>")<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>public class <xsl:value-of select='@entity-class'/> {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name="relation-definition"/>
   <xsl:call-template name="field-definition"/>
   <xsl:call-template name="var-definition"/>
   <xsl:call-template name="readset-definition"/>
   <xsl:call-template name="updateset-definition"/>
   <xsl:call-template name="query-definition"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:call-template name="import-entity-classes"/>
   <xsl:value-of select="$empty"/>import com.site.dal.jdbc.DataField;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.QueryDef;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.QueryType;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.Readset;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.Updateset;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.annotation.Attribute;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="empty"/>import com.site.dal.jdbc.annotation.Entity;<xsl:value-of select="$empty-line"/>
   <xsl:if test="relation">
      <xsl:value-of select="empty"/>import com.site.dal.jdbc.annotation.Relation;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="readsets/readset/readset-ref">
      <xsl:value-of select="empty"/>import com.site.dal.jdbc.annotation.SubObjects;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="count(var) != 0">
      <xsl:value-of select="empty"/>import com.site.dal.jdbc.annotation.Variable;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-entity-classes">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="relation">
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="relatedEntity" select="/entities/entity[@name=$entity-name]"/>
      
      <xsl:if test="$relatedEntity/@do-package != $entity/@do-package and generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import <xsl:value-of select="$relatedEntity/@do-package"/>.<xsl:value-of select="$relatedEntity/@entity-class"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="relation-definition">
   <xsl:for-each select="relation">
      <xsl:value-of select='$empty'/>   @Relation(logicalName = "<xsl:value-of select="@entity-name"/>", alias = "<xsl:value-of select='@entity-alias'/>", join = "<xsl:value-of select="@join"/>")<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="$empty"/>   public static final DataField <xsl:value-of select="@upper-name"/> = new DataField("<xsl:value-of select="@name"/>");<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="field-definition">
   <xsl:for-each select="member">
      <xsl:value-of select='$empty'/>   @Attribute(field = "<xsl:value-of select="@field"/>"<xsl:value-of select='$empty'/>
      <xsl:if test="@nullable = 'false'">, nullable = false</xsl:if>
      <xsl:if test="@select-expr">, selectExpr = "<xsl:value-of select="@select-expr"/>"</xsl:if>
      <xsl:if test="@insert-expr">, insertExpr = "<xsl:value-of select="@insert-expr"/>"</xsl:if>
      <xsl:if test="@update-expr">, updateExpr = "<xsl:value-of select="@update-expr"/>"</xsl:if>
      <xsl:if test="@key='true'">, primaryKey = true</xsl:if>
      <xsl:if test="@auto-increment='true'">, autoIncrement = true</xsl:if>
      <xsl:value-of select='$empty'/>)<xsl:value-of select="$empty-line"/>
      <xsl:value-of select='$empty'/>   public static final DataField <xsl:value-of select='@upper-name'/> = new DataField("<xsl:value-of select='@name'/>");<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="var-definition">
   <xsl:if test="count(var) != 0">
      <xsl:for-each select="var">
         <xsl:value-of select='$empty'/>   @Variable<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty'/>   public static final DataField <xsl:value-of select='@upper-name'/> = new DataField("<xsl:value-of select='@name'/>");<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
      </xsl:for-each>
   </xsl:if>
</xsl:template>

<xsl:template name="readset-definition">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="readsets/readset">
      <xsl:choose>
         <xsl:when test="@all='true'">
            <xsl:value-of select="$empty"/>   public static final Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> READSET_<xsl:value-of select="@upper-name"/> = new Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/>(<xsl:value-of select='$empty'/>
            <xsl:for-each select="$entity/member[@all='true']">
               <xsl:if test="position() != 1">, </xsl:if><xsl:value-of select="@upper-name"/>
            </xsl:for-each>
         </xsl:when>
         <xsl:when test="readset-ref">
            <xsl:value-of select="$empty"/>   @SubObjects( { <xsl:value-of select="$empty"/>
            <xsl:for-each select="readset-ref">
               <xsl:if test="position() != 1">, </xsl:if>
			   <xsl:value-of select="$empty"/>"<xsl:value-of select="@relation-name"/>"<xsl:value-of select="$empty"/>
            </xsl:for-each>
            <xsl:value-of select="$empty"/> })<xsl:value-of select='$empty-line'/>
            <xsl:value-of select="$empty"/>   public static final Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> READSET_<xsl:value-of select="@upper-name"/> = new Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/>(<xsl:value-of select='$empty'/>
            <xsl:for-each select="readset-ref">
			   <xsl:variable name="relation-name" select="@relation-name"/>
			   <xsl:variable name="relation" select="$entity/relation[@name=$relation-name]"/>
			   <xsl:variable name="entity-ref" select="/entities/entity[@name=$relation/@entity-name]"/>
               <xsl:if test="position() != 1">, </xsl:if>
			   <xsl:choose>
				  <xsl:when test="$relation-name"><xsl:value-of select="$entity-ref/@entity-class"/></xsl:when>
				  <xsl:otherwise><xsl:value-of select="$entity/@entity-class"/></xsl:otherwise>
			   </xsl:choose>
			   <xsl:value-of select="$empty"/>.READSET_<xsl:value-of select="@upper-name"/>
            </xsl:for-each>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   public static final Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> READSET_<xsl:value-of select="@upper-name"/> = new Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/>(<xsl:value-of select='$empty'/>
            <xsl:for-each select="member">
               <xsl:if test="position() != 1">, </xsl:if><xsl:value-of select="@upper-name"/>
            </xsl:for-each>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="updateset-definition">
   <xsl:variable name="name" select="name()"/>
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="updatesets/updateset">
      <xsl:value-of select="$empty"/>   public static final Updateset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> UPDATESET_<xsl:value-of select="@name"/> = new Updateset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/>(<xsl:value-of select='$empty'/>
      <xsl:choose>
         <xsl:when test="@all='true'">
            <xsl:variable name="members" select="$entity/member[@all='true' and not(@all-update='false')]"/>
            <xsl:for-each select="$members[not(@computed='true' or @insert-expr and not(@update-expr))]">
               <xsl:if test="position() != 1">, </xsl:if><xsl:value-of select="@upper-name"/>
            </xsl:for-each>
         </xsl:when>
         <xsl:otherwise>
            <xsl:for-each select="member">
               <xsl:if test="position() != 1">, </xsl:if><xsl:value-of select="@upper-name"/>
            </xsl:for-each>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-definition">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query">
      <xsl:variable name="s1">
         <xsl:call-template name="replace">
            <xsl:with-param name="text" select="normalize-space(statement)"/>
            <xsl:with-param name="from" select="'\'"/>
            <xsl:with-param name="to" select="'\\'"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:variable name="s2">
         <xsl:call-template name="replace">
            <xsl:with-param name="text" select="$s1"/>
            <xsl:with-param name="from" select="'&quot;'"/>
            <xsl:with-param name="to" select="'\&quot;'"/>
         </xsl:call-template>
      </xsl:variable>
      <xsl:value-of select="$empty"/>   public static final QueryDef <xsl:value-of select="@upper-name"/> = new QueryDef("<xsl:value-of select="@name"/>", <xsl:value-of select="$empty"/>
      <xsl:value-of select="$entity/@entity-class"/>.class, QueryType.<xsl:value-of select="@type"/>, <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      "<xsl:value-of select="$s2" disable-output-escaping="yes"/>"<xsl:if test="@sp = 'true'">, true</xsl:if>
      <xsl:value-of select="$empty"/>);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
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

<xsl:template name="generic-type">
   <xsl:param name="type"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$type" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
</xsl:template>

</xsl:stylesheet>
