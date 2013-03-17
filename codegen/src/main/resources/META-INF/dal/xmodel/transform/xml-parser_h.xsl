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
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name="parser-definition"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name="parser-delegate-definition"/>
</xsl:template>

<xsl:template name="parser-definition">
   <xsl:value-of select="$empty"/>@interface <xsl:value-of select="@xml-parser-class"/> : NSObject<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>+ (<xsl:value-of select="/model/entity[@root='true']/@class-name"/> *)parseXml:(NSData *)xml;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="parser-delegate-definition">
   <xsl:value-of select="$empty"/>@interface <xsl:value-of select="@xml-parser-delegate"/> : NSObject <xsl:call-template name="lt"/>NSXMLParserDelegate<xsl:call-template name="gt"/><xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@property (assign, nonatomic) <xsl:value-of select="/model/entity[@root='true']/@class-name"/> *_<xsl:value-of select="/model/entity[@root='true']/@param-name"/>;<xsl:value-of select="$empty-line"/>
   
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (<xsl:value-of select="@class-name"/> *)<xsl:value-of select="@build-method"/>:(NSDictionary *)attrs;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>- (void)parse:(NSString*)name attributes:(NSDictionary *)attrs;<xsl:value-of select="$empty-line"/>
   
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@parse-for-method"/>:(<xsl:value-of select="@class-name"/> *)parentObj parentTag:(NSString*)parentTag name:(NSString*)name attributes:(NSDictionary *)attrs;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>

   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>@end<xsl:value-of select="$empty-line"/>
</xsl:template>

</xsl:stylesheet>
