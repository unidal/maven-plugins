<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="*[@param-name or @param-name-element or @local-name or @local-name-element]">
	<xsl:copy>
		<xsl:copy-of select="@*" />

		<!-- attribute definition -->
		<xsl:variable name="param-name">
			<xsl:value-of select="' '"/>
			<xsl:value-of select="@param-name"/>
			<xsl:value-of select="' '"/>
		</xsl:variable>
		<xsl:variable name="param-name-element">
			<xsl:value-of select="' '"/>
			<xsl:value-of select="@param-name-element"/>
			<xsl:value-of select="' '"/>
		</xsl:variable>
		<xsl:variable name="local-name">
			<xsl:value-of select="' '"/>
			<xsl:value-of select="@local-name"/>
			<xsl:value-of select="' '"/>
		</xsl:variable>
		<xsl:variable name="local-name-element">
			<xsl:value-of select="' '"/>
			<xsl:value-of select="@local-name-element"/>
			<xsl:value-of select="' '"/>
		</xsl:variable>
		
		<xsl:if test="contains(' abstract default if private this boolean do implements protected throw break double import public throws byte else instanceof return transient case extends int short try catch final interface static void char finally long strictfp volatile class float native super while const for new switch continue goto package synchronized ', $param-name)">
			<xsl:attribute name="param-name">
				<xsl:value-of select="'_'"/>
				<xsl:value-of select="@param-name"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="contains(' abstract default if private this boolean do implements protected throw break double import public throws byte else instanceof return transient case extends int short try catch final interface static void char finally long strictfp volatile class float native super while const for new switch continue goto package synchronized ', $param-name-element)">
			<xsl:attribute name="param-name-element">
				<xsl:value-of select="'_'"/>
				<xsl:value-of select="@param-name-element"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="contains(' abstract default if private this boolean do implements protected throw break double import public throws byte else instanceof return transient case extends int short try catch final interface static void char finally long strictfp volatile class float native super while const for new switch continue goto package synchronized ', $local-name)">
			<xsl:attribute name="local-name">
				<xsl:value-of select="@local-name"/>
				<xsl:value-of select="'_'"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="contains(' abstract default if private this boolean do implements protected throw break double import public throws byte else instanceof return transient case extends int short try catch final interface static void char finally long strictfp volatile class float native super while const for new switch continue goto package synchronized ', $local-name-element)">
			<xsl:attribute name="local-name-element">
				<xsl:value-of select="@local-name-element"/>
				<xsl:value-of select="'_'"/>
			</xsl:attribute>
		</xsl:if>
		
		<xsl:if test="node()">
		   <xsl:apply-templates />
		</xsl:if>
	</xsl:copy>
</xsl:template>

<xsl:template match="@*|node()">
	<xsl:copy>
		<xsl:copy-of select="@*" />
		
		<xsl:if test="node()">
			<xsl:apply-templates />
		</xsl:if>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>