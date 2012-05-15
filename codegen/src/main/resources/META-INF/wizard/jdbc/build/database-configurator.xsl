<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="class"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/datasources/datasource[@name=$name]"/>
</xsl:template>

<xsl:template match="datasource">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package" />;

import java.util.ArrayList;
import java.util.List;

import <xsl:value-of select="@do-package" />._INDEX;
import com.site.dal.jdbc.configuration.AbstractJdbcResourceConfigurator;
import com.site.lookup.configuration.Component;

final class <xsl:value-of select="$class" /> extends AbstractJdbcResourceConfigurator {
   @Override
   public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
      List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();

      all.add(defineJdbcDataSourceConfigurationManagerComponent("datasources.xml"));
      all.add(defineJdbcDataSourceComponent("<xsl:value-of select="@name"/>", "<xsl:value-of select="normalize-space(jdbc-connection/driver)"/>", "<xsl:value-of select="normalize-space(jdbc-connection/url)"/>", "<xsl:value-of select="normalize-space(jdbc-connection/user)"/>", "<xsl:value-of select="normalize-space(jdbc-connection/password)"/>", "<xsl:value-of select="'&lt;![CDATA['" disable-output-escaping="yes"/><xsl:value-of select="normalize-space(jdbc-connection/properties)" disable-output-escaping="yes"/><xsl:value-of select="']]&gt;'" disable-output-escaping="yes"/>"));

      defineSimpleTableProviderComponents(all, "<xsl:value-of select="@name" />", _INDEX.getEntityClasses());
      defineDaoComponents(all, _INDEX.getDaoClasses());

      return all;
   }
}
</xsl:template>

</xsl:stylesheet>
