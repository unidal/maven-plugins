<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="class"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard"/>
</xsl:template>

<xsl:template match="wizard">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package" />;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import org.unidal.lookup.configuration.Component;

final class DatabaseConfigurator extends AbstractJdbcResourceConfigurator {
   @Override
   public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
      List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
<xsl:for-each select="jdbc">
   <xsl:for-each select="group">
      defineSimpleTableProviderComponents(all, "<xsl:value-of select="../@name" />", <xsl:value-of select="@package" />._INDEX.getEntityClasses());<xsl:value-of select="$empty-line"/>
      defineDaoComponents(all, <xsl:value-of select="@package" />._INDEX.getDaoClasses());<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:for-each>
      return all;
   }
}
</xsl:template>

</xsl:stylesheet>
