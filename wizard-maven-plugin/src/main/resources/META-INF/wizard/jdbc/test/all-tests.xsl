<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl" />
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8" />
<xsl:param name="package" />
<xsl:param name="class" />
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
<xsl:apply-templates select="/wizard" />
</xsl:template>

<xsl:template match="wizard">
<xsl:value-of select="$empty" />package <xsl:value-of select="$package" />;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

// add test classes here

})
public class <xsl:value-of select="$class" /> {

}
</xsl:template>

</xsl:stylesheet>
