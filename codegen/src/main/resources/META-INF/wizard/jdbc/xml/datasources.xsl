<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" xalan:indent-amount="3" xmlns:xalan="http://xml.apache.org/xslt"/>
<xsl:param name="package"/>
<xsl:param name="class"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/jdbc"/>
</xsl:template>

<xsl:template match="jdbc">
	<xsl:value-of select="$empty-line"/>
	<data-sources>
		<data-source id="{@name}">
			<xsl:for-each select="datasource">
				<maximum-pool-size>3</maximum-pool-size>
				<connection-timeout>1s</connection-timeout>
				<idle-timeout>1m</idle-timeout>
				<statement-cache-size>1000</statement-cache-size>
				<properties>
					<driver><xsl:value-of select="normalize-space(driver)" disable-output-escaping="yes"/></driver>
					<url><xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>![CDATA[<xsl:value-of select="normalize-space(url)" disable-output-escaping="yes"/>]]<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/></url>
					<user><xsl:value-of select="normalize-space(user)" disable-output-escaping="yes"/></user>
					<password><xsl:value-of select="normalize-space(password)" disable-output-escaping="yes"/></password>
					<connectionProperties><xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>![CDATA[<xsl:value-of select="normalize-space(properties)"/>]]<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/></connectionProperties>
				</properties>
			</xsl:for-each>
		</data-source>
	</data-sources>
</xsl:template>

</xsl:stylesheet>
