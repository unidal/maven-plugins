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
<xsl:value-of select="$empty"/>#import "XmlWriter.h"<xsl:value-of select="$empty-line"/>
@interface <xsl:value-of select="@xml-builder-class"/> : NSObject <xsl:call-template name="lt"/><xsl:value-of select="@visitor-class"/><xsl:call-template name="gt"/>

- (id) initWithXmlWriter:(XMLWriter *)writer;

@end
</xsl:template>

</xsl:stylesheet>
