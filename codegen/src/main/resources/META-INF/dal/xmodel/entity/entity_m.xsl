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
   <xsl:value-of select="$empty"/>#import "Entity.h"<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>#import "XmlBuilder.h"<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="base-entity-implementation"/>
   <xsl:call-template name="entity-implementation"/>
</xsl:template>

<xsl:template name="base-entity-implementation">

/*
 * <xsl:value-of select="@base-entity-class"/>
 */
@implementation <xsl:value-of select="@base-entity-class"/>  

- (void)accept:(NSObject<xsl:call-template name="lt"/><xsl:value-of select="@visitor-class"/><xsl:call-template name="gt"/> *)visitor {
    // to be overriden
}

- (NSString *)description {
    XMLWriter *writer = [[XMLWriter alloc] init];
    <xsl:value-of select="@xml-builder-class"/> *builder = [[<xsl:value-of select="@xml-builder-class"/> alloc] initWithXmlWriter:writer];
    
    [self accept:builder];
    
    NSString *xml = [[writer toString] retain];
    
    [builder release];
    [writer release];
    return xml;
}


- (NSString *)stringFromDate:(NSDate *)date format:(NSString *)format {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    
    [formatter setDateFormat:format];
    
    NSString *str = [formatter stringFromDate:date];
    
    [formatter release];
    return str;
}

- (NSDate *)dateFromString:(NSString *)str format:(NSString *)format {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    
    [formatter setDateFormat:format];
    
    NSDate *date = [formatter dateFromString:str];
    
    [formatter release];
    return date;
}

- (NSNumber *)numberFromString:(NSString *)str {
    NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
    
    [formatter setNumberStyle:NSNumberFormatterDecimalStyle];
    
    NSNumber *num = [formatter numberFromString:str];
    
    [formatter release];
    return num;
}

@end
</xsl:template>

<xsl:template name="entity-implementation">
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>/*<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/> * <xsl:value-of select="@class-name"/><xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/> */<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>@implementation <xsl:value-of select="@class-name"/> {<xsl:value-of select="$empty-line"/>
      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:value-of select="'   '"/><xsl:value-of select="@value-type"/> *<xsl:value-of select="@field-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>

      <xsl:for-each select="attribute | element">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>@synthesize <xsl:value-of select="@property"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[not(@list='true' or @map='true')]">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>@synthesize <xsl:value-of select="@property"/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:if test="entity-ref[@list='true' or @map='true']">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>- (id)init {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>    if (self = [super init]) {<xsl:value-of select="$empty-line"/>
         <xsl:for-each select="entity-ref[@list='true' or @map='true']">
            <xsl:sort select="@name"/>
            <xsl:value-of select="$empty"/>       self-<xsl:call-template name="gt"/><xsl:value-of select="@field-name"/> = [[<xsl:value-of select="@value-type"/> alloc] init];<xsl:value-of select="$empty-line"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>    return self;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
      </xsl:if>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (void)accept:(NSObject<xsl:call-template name="lt"/><xsl:value-of select="/model/@visitor-class"/><xsl:call-template name="gt"/> *)visitor {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    [visitor <xsl:value-of select="@visit-method"/>:self];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>

      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@add-method"/>:(<xsl:value-of select="@value-type-element"/> *)<xsl:value-of select="@param-name-element"/> {<xsl:value-of select="$empty-line"/>
         <xsl:choose>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>   [<xsl:value-of select="@field-name"/> addObject:<xsl:value-of select="@param-name-element"/>];<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@map='true'">
               <xsl:variable name="name" select="@name"/>
               <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
               <xsl:variable name="key" select="($entity/attribute|$entity/element)[@key='true']"/>
               
               <xsl:choose>
                  <xsl:when test="$key/@value-type='NSInteger'">
                     <xsl:value-of select="$empty"/>    NSString *key = [NSNumber numberWithInteger:<xsl:value-of select="@param-name-element"/>.<xsl:value-of select="$key/@property"/>].stringValue;<xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty-line"/>
                     <xsl:value-of select="$empty"/>    [<xsl:value-of select="@field-name"/> setValue:<xsl:value-of select="@param-name-element"/> forKey:key];<xsl:value-of select="$empty-line"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="$empty"/>    [<xsl:value-of select="@field-name"/> setValue:<xsl:value-of select="@param-name-element"/> forKey:<xsl:value-of select="@param-name-element"/>.<xsl:value-of select="$key/@property"/>];<xsl:value-of select="$empty-line"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:when>
         </xsl:choose>
         <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:variable name="name" select="@name"/>
         <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
         <xsl:variable name="key" select="($entity/attribute | $entity/element)[@key='true']"/>
         <xsl:if test="$key">
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>- (<xsl:value-of select="@value-type-element"/> *)<xsl:value-of select="@find-method"/>:(<xsl:value-of select="@value-type-key"/><xsl:if test="not(@scalar-key='true')"> *</xsl:if>)<xsl:value-of select="$key/@param-name"/> {<xsl:value-of select="$empty-line"/>
            <xsl:choose>
               <xsl:when test="@list='true'">
                  <xsl:value-of select="$empty"/>    for (<xsl:value-of select="@value-type-element"/> *<xsl:value-of select="@param-name-element"/> in <xsl:value-of select="@field-name"/>) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>       if (<xsl:value-of select="$empty"/>
                  <xsl:call-template name="compare-key">
                     <xsl:with-param name="key" select="$key"/>
                  </xsl:call-template>
                  <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>          return <xsl:value-of select="@param-name-element"/>;<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>       }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>    return nil;<xsl:value-of select="$empty-line"/>
               </xsl:when>
               <xsl:when test="@map='true'">
                  <xsl:choose>
                     <xsl:when test="$key/@value-type='NSInteger'">
                        <xsl:value-of select="$empty"/>    NSString *key = [NSNumber numberWithInteger:<xsl:value-of select="$key/@param-name"/>].stringValue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>    return [<xsl:value-of select="@field-name"/> objectForKey:key];<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:when test="$key/@value-type='NSNumber'">
                        <xsl:value-of select="$empty"/>    return [<xsl:value-of select="@field-name"/> objectForKey:<xsl:value-of select="$key/@param-name"/>.stringValue];<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$empty"/>    return [<xsl:value-of select="@field-name"/> objectForKey:<xsl:value-of select="$key/@param-name"/>];<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
               </xsl:when>
            </xsl:choose>
            <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[@list='true' or @map='true']">
         <xsl:sort select="@name"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>- (<xsl:value-of select="@value-type"/> *)<xsl:value-of select="@get-method"/> {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   return <xsl:value-of select="@field-name"/>;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
   
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="compare-key">
   <xsl:param name="obj" select="."/>
   <xsl:param name="key"/>
   
   <xsl:choose>
      <xsl:when test="$key/@primitive='true'">
         <xsl:value-of select="$obj/@param-name-element"/>.<xsl:value-of select="$key/@param-name"/> == <xsl:value-of select="$key/@param-name"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$empty"/>[<xsl:value-of select="$obj/@param-name-element"/>.<xsl:value-of select="$key/@param-name"/> isEqualToString:<xsl:value-of select="$key/@param-name"/>]<xsl:value-of select="$empty"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="lt">
   <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>
</xsl:template>

<xsl:template name="gt">
   <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>
</xsl:template>

</xsl:stylesheet>
