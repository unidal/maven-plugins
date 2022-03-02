<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp"/>
</xsl:template>

<xsl:template match="webapp">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;
import org.unidal.web.mvc.model.ModuleRegistry;

class WebComponentConfigurator extends AbstractResourceConfigurator {
   @Override
   public List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> defineComponents() {
      List<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Component&gt;'" disable-output-escaping="yes"/>();
<xsl:choose>
  <xsl:when test="module[@default='true']">
      all.add(A(ModuleRegistry.class).config(E("default-module").value(<xsl:value-of select="module[@default='true']/@package"/>.<xsl:value-of select="module[@default='true']/@module-class"/>.class.getName())));
  </xsl:when>
  <xsl:when test="module">
      all.add(A(ModuleRegistry.class).config(E("default-module").value(<xsl:value-of select="module/@package"/>.<xsl:value-of select="module/@module-class"/>.class.getName())));
  </xsl:when>
  <xsl:otherwise>
      all.add(A(ModuleRegistry.class));
  </xsl:otherwise>
</xsl:choose>

<xsl:for-each select="module">
      all.add(A(<xsl:value-of select="@package"/>.<xsl:value-of select="@module-class"/>.class));
<xsl:for-each select="page">
      all.add(A(<xsl:value-of select="@package"/>.<xsl:value-of select="@handler-class"/>.class));
      <xsl:if test="//webapp/@language='jsp'">
      all.add(A(<xsl:value-of select="@package"/>.<xsl:value-of select="@jsp-viewer-class"/>.class));
      </xsl:if>
</xsl:for-each>
</xsl:for-each>
      return all;
   }
}
</xsl:template>

</xsl:stylesheet>
