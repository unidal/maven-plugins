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
<xsl:value-of select="$empty"/>#import "XmlBuilder.h"<xsl:value-of select="$empty-line"/>
@implementation <xsl:value-of select="@xml-builder-class"/> {
    XMLWriter *m_writer;
}

- (id) initWithXmlWriter:(XMLWriter *)writer {
    if (self = [super init]) {
        self-<xsl:call-template name="gt"/>m_writer = [writer retain];
    }

    return self;
}

- (void) dealloc {
    [m_writer release];
    
    [super dealloc];
}<xsl:value-of select="$empty-line"/>
<xsl:call-template name="visit-method"/>
<xsl:value-of select="$empty-line"/>
@end
</xsl:template>

<xsl:template name="visit-method">
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>- (void)<xsl:value-of select="@visit-method"/>:(<xsl:value-of select="@class-name"/> *)<xsl:value-of select="@param-name"/> {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    [m_writer writeStartElement:@"<xsl:value-of select="@tag-name"/>"];<xsl:value-of select="$empty-line"/>

      <xsl:for-each select="attribute[not(@render='false')]">
         <xsl:value-of select="$empty"/>    [m_writer writeAttribute:@"<xsl:value-of select="@tag-name"/>" value:<xsl:call-template name="to-string-value"/>];<xsl:value-of select="$empty-line"/>
      </xsl:for-each>

      <xsl:for-each select="entity-ref[not(@render='false')]">
         <xsl:variable name="name" select="@name"/>
         <xsl:variable name="entity" select="/model/entity[@name=$name]"/>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:choose>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>    for (id <xsl:value-of select="@param-name-element"/> in [<xsl:value-of select="../@param-name"/><xsl:value-of select="$space"/><xsl:value-of select="@get-method"/>]) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>       [self <xsl:value-of select="$entity/@visit-method"/>:<xsl:value-of select="@param-name-element"/>];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@map='true'">
               <xsl:value-of select="$empty"/>    for (id <xsl:value-of select="@param-name-element"/> in [[<xsl:value-of select="../@param-name"/><xsl:value-of select="$space"/><xsl:value-of select="@get-method"/>] objectEnumerator]) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>       [self <xsl:value-of select="$entity/@visit-method"/>:<xsl:value-of select="@param-name-element"/>];<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>    }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>    [self <xsl:value-of select="$entity/@visit-method"/>:<xsl:value-of select="@param-name"/>];<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    [m_writer writeEndElement];<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="to-string-value">
   <xsl:param name="entity" select=".."/>
   
   <xsl:variable name="property">
      <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@property"/>
   </xsl:variable>
   <xsl:choose>
      <xsl:when test="@primitive='true'">[[NSNumber numberWithInt:<xsl:value-of select="$property"/>] stringValue]</xsl:when>
      <xsl:when test="@value-type='NSNumber'">[<xsl:value-of select="$property"/> stringValue]</xsl:when>
      <xsl:when test="@value-type='NSDate'">[<xsl:value-of select="$entity/@param-name"/> stringFromDate:<xsl:value-of select="$property"/> format:@"<xsl:value-of select="@format"/>"]</xsl:when>
      <xsl:otherwise><xsl:value-of select="$property"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
