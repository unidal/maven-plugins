<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$name]"/>
</xsl:template>

<xsl:template match="module">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import org.unidal.lookup.annotation.Named;
import org.unidal.web.mvc.AbstractModule;
import org.unidal.web.mvc.Module;
import org.unidal.web.mvc.annotation.ModuleMeta;
import org.unidal.web.mvc.annotation.ModulePagesMeta;

@ModuleMeta(name = "<xsl:value-of select="@path"/>", defaultInboundAction = "<xsl:value-of select="page[@default='true']/@name"/>", defaultTransition = "default", defaultErrorAction = "default")
@ModulePagesMeta({
<xsl:for-each select="page">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="@package"/>.<xsl:value-of select="@handler-class"/>.class,<xsl:value-of select="$empty-line"/>
</xsl:for-each>
})
@Named(type = Module.class, value = "<xsl:value-of select="$package"/>.<xsl:value-of select="@module-class"/>")
public class <xsl:value-of select="@module-class"/> extends AbstractModule {

}
</xsl:template>

</xsl:stylesheet>
