<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>

<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
<xsl:value-of select="$empty"/>#import <xsl:call-template name="lt"/>Foundation/Foundation.h<xsl:call-template name="gt"/><xsl:value-of select="$empty-line"/>
<xsl:call-template name="type-declaration"/>
<xsl:call-template name="ientity-definition"/>
<xsl:call-template name="base-entity-definition"/>
<xsl:call-template name="entity-definition"/>
<xsl:call-template name="ivisitor-definition"/>

</xsl:template>

<xsl:template name="type-declaration">
@protocol <xsl:value-of select="@visitor-class"/>;
<xsl:for-each select="entity">
@class <xsl:value-of select="@class-name"/>;
</xsl:for-each>
</xsl:template>

<xsl:template name="ientity-definition">

@protocol <xsl:value-of select="@entity-class"/>

- (void)accept:(NSObject<xsl:call-template name="lt"/><xsl:value-of select="@visitor-class"/><xsl:call-template name="gt"/> *)visitor;

@end
</xsl:template>

<xsl:template name="base-entity-definition">

/*
 * <xsl:value-of select="@base-entity-class"/>
 */
@interface <xsl:value-of select="@base-entity-class"/> :  NSObject <xsl:call-template name="lt"/><xsl:value-of select="@entity-class"/><xsl:call-template name="gt"/>

- (NSDate *)dateFromString:(NSString *)str format:(NSString *)format;

- (NSNumber *)numberFromString:(NSString *)str;

- (NSString *)stringFromDate:(NSDate *)date format:(NSString *)format;

@end
</xsl:template>

<xsl:template name="entity-definition">
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>/*<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/> * <xsl:value-of select="@class-name"/><xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/> */<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>@interface <xsl:value-of select="@class-name"/> : <xsl:value-of select="/model/@base-entity-class"/><xsl:value-of select="$empty-line"/>

      <xsl:for-each select="attribute | element">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>@property <xsl:value-of select="@property-modifier"/><xsl:value-of select="@value-type"/><xsl:value-of select="$space"/><xsl:if test="not(@scalar='true')">*</xsl:if><xsl:value-of select="@property"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[not(@list='true' or @map='true')]">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>@property <xsl:value-of select="@property-modifier"/><xsl:value-of select="@value-type"/> *<xsl:value-of select="@property"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      
      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@add-method"/>:(<xsl:value-of select="@value-type-element"/> *)<xsl:value-of select="@param-name-element"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:variable name="name" select="@name"/>
         <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
         <xsl:variable name="key" select="($entity/attribute|$entity/element)[@key='true']"/>
         <xsl:if test="$key">
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>- (<xsl:value-of select="@value-type-element"/> *)<xsl:value-of select="@find-method"/>:(<xsl:value-of select="@value-type-key"/><xsl:if test="not(@scalar-key='true')"> *</xsl:if>)<xsl:value-of select="$key/@param-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>- (<xsl:value-of select="@value-type"/> *)<xsl:value-of select="@get-method"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
   
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="ivisitor-definition">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>/*<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/> * <xsl:value-of select="@visitor-class"/><xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/> */<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@protocol <xsl:value-of select="@visitor-class"/><xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@visit-method"/>:(<xsl:value-of select="@class-name"/> *)<xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
</xsl:template>

</xsl:stylesheet>
