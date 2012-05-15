<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="enable-json" select="/model/@enable-json-builder='true'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.Formattable;
import java.util.Formatter;

import <xsl:value-of select="@transform-package"/>.DefaultXmlBuilder;
<xsl:if test="$enable-json">
<xsl:value-of select="$empty"/>import <xsl:value-of select="@transform-package"/>.DefaultJsonBuilder;
</xsl:if>
public abstract class BaseEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> implements IEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template>, Formattable {
<xsl:if test="$enable-json">
   public static final String JSON = "%2s";

   public static final String JSON_COMPACT = "%2.0s";
</xsl:if>
   public static final String XML = "%1s";
   
   public static final String XML_COMPACT = "%1.0s";
   
   protected void assertAttributeEquals(Object instance, String entityName, String name, Object expectedValue, Object actualValue) {
      if (expectedValue == null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> actualValue != null || expectedValue != null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> !expectedValue.equals(actualValue)) {
         throw new IllegalArgumentException(String.format("Mismatched entity(%s) found! Same %s attribute is expected! %s: %s.", entityName, name, entityName, instance));
      }
   }

   @Override
   public void formatTo(Formatter formatter, int flags, int width, int precision) {<xsl:value-of select="$empty"/>
<xsl:choose>
<xsl:when test="$enable-json">
      boolean useJson = (width == 2);
      boolean compact = (precision == 0);
      
      if (useJson) {
         DefaultJsonBuilder builder = new DefaultJsonBuilder(compact);

         formatter.format(builder.buildJson(this));
      } else {
         DefaultXmlBuilder builder = new DefaultXmlBuilder(compact);

         formatter.format(builder.buildXml(this));
      }<xsl:value-of select="$empty"/>
</xsl:when>
<xsl:otherwise>
      boolean compact = (precision == 0);
      DefaultXmlBuilder builder = new DefaultXmlBuilder(compact);

      formatter.format(builder.buildXml(this));<xsl:value-of select="$empty"/>
</xsl:otherwise>
</xsl:choose>
   }
<xsl:if test="//entity/attribute[@value-type='java.util.Date' and @default-value] | //entity/element[@value-type='java.util.Date' and @default-value]">
   protected java.util.Date toDate(String format, String text) {
      try {
         return new java.text.SimpleDateFormat(format).parse(text);
      } catch (java.text.ParseException e) {
         throw new IllegalArgumentException(String.format("Error when parsing date(%s) with format(%s)!", text, format), e);
      }
   }
</xsl:if>
   @Override
   public String toString() {
      return new DefaultXmlBuilder().buildXml(this);
   }
}
</xsl:template>

</xsl:stylesheet>
