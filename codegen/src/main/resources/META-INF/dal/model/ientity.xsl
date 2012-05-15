<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

public interface IEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> {
   public void accept(IVisitor visitor);
<xsl:if test="not(/model/@disable-merger='true')">
   public void mergeAttributes(T other);
</xsl:if>
}
</xsl:template>

</xsl:stylesheet>
