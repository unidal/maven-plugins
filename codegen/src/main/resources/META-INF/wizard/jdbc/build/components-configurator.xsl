<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="class"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/datasources"/>
</xsl:template>

<xsl:template match="datasources">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;

import com.site.lookup.configuration.AbstractResourceConfigurator;
import com.site.lookup.configuration.Component;

public class <xsl:value-of select="$class"/> extends AbstractResourceConfigurator {
   @Override
   public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
      List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();
<xsl:for-each select="datasource[@name]">
      all.addAll(new <xsl:value-of select="@configurator-class"/>().defineComponents());
</xsl:for-each>

      return all;
   }

   public static void main(String[] args) {
      generatePlexusComponentsXmlFile(new <xsl:value-of select="$class"/>());
   }
}
</xsl:template>

</xsl:stylesheet>
