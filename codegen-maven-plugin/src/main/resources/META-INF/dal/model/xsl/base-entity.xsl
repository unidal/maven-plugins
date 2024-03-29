<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="with-xml-builder" select="/model[@enable-xml='true' or @enable-xml-builder='true']"/>
<xsl:variable name="with-xml-parser" select="/model[@enable-xml='true' or @enable-xml-parser='true']"/>
<xsl:variable name="with-json-builder" select="/model[@enable-json='true' or @enable-json-builder='true']"/>
<xsl:variable name="with-json-parser" select="/model[@enable-json='true' or @enable-json-parser='true']"/>
<xsl:variable name="with-native-builder" select="/model[@enable-native='true' or @enable-native-builder='true']"/>
<xsl:variable name="with-native-parser" select="/model[@enable-native='true' or @enable-native-parser='true']"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
<xsl:value-of select="$empty"/>/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */<xsl:value-of select="$empty-line"/>
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;
<xsl:value-of select="$empty-line"/>
<xsl:value-of select="$empty"/>import java.util.Formattable;<xsl:value-of select="$empty-line"/>

<xsl:if test="$with-json-builder">
   <xsl:value-of select="$empty"/>import java.util.FormattableFlags;<xsl:value-of select="$empty-line"/>
</xsl:if>

<xsl:value-of select="$empty"/>import java.util.Formatter;<xsl:value-of select="$empty-line"/>
<xsl:value-of select="$empty"/>import <xsl:value-of select="@transform-package"/>.DefaultXmlBuilder;<xsl:value-of select="$empty-line"/>

<xsl:if test="$with-json-builder">
   <xsl:value-of select="$empty"/>import <xsl:value-of select="@transform-package"/>.DefaultJsonBuilder;<xsl:value-of select="$empty-line"/>
</xsl:if>

<xsl:value-of select="$empty"/>public abstract class BaseEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> implements IEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template>, Formattable {<xsl:value-of select="$empty-line"/>
<xsl:if test="$with-json-builder">
   public static final String JSON = "%#.3s";

   public static final String JSON_COMPACT = "%#s";
</xsl:if>
<xsl:if test="$with-xml-builder">
   public static final String XML = "%.3s";
   
   public static final String XML_COMPACT = "%s";
</xsl:if>
   protected void assertAttributeEquals(Object instance, String entityName, String name, Object expectedValue, Object actualValue) {
      if (expectedValue == null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> actualValue != null || expectedValue != null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> !expectedValue.equals(actualValue)) {
         throw new IllegalArgumentException(String.format("Mismatched entity(%s) found! Same %s attribute is expected! %s: %s.", entityName, name, entityName, instance));
      }
   }

   protected boolean equals(Object o1, Object o2) {
      if (o1 == null) {
         return o2 == null;
      } else if (o2 == null) {
         return false;
      } else {
         return o1.equals(o2);
      }
   }

   @Override
   public void formatTo(Formatter formatter, int flags, int width, int precision) {<xsl:value-of select="$empty"/>

<xsl:choose>
   <xsl:when test="$with-json-builder">
      boolean useJson = (flags <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> FormattableFlags.ALTERNATE) == FormattableFlags.ALTERNATE;
      boolean compact = (precision <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>= 0);
      
      if (useJson) {
         DefaultJsonBuilder builder = new DefaultJsonBuilder(compact);

         formatter.format("%s", builder.build(this));
      } else {
         DefaultXmlBuilder builder = new DefaultXmlBuilder(compact);

         formatter.format("%s", builder.build(this));
      }<xsl:value-of select="$empty-line"/>
   </xsl:when>
   <xsl:otherwise>
      boolean compact = (precision == 0);
      DefaultXmlBuilder builder = new DefaultXmlBuilder(compact);

      formatter.format("%s", builder.build(this));<xsl:value-of select="$empty-line"/>
   </xsl:otherwise>
</xsl:choose>

<xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
<xsl:if test="//entity/attribute[@value-type='java.util.Date' and @default-value] | //entity/element[@value-type='java.util.Date' and @default-value]">
   protected java.util.Date toDate(String format, String text) {
      try {
         return new java.text.SimpleDateFormat(format).parse(text);
      } catch (java.text.ParseException e) {
         throw new IllegalArgumentException(String.format("Error when parsing date(%s) with format(%s)!", text, format), e);
      }
   }
</xsl:if>
<xsl:if test="$with-xml-builder">
   @Override
   public String toString() {
      return new DefaultXmlBuilder().build(this);
   }
</xsl:if>
}
</xsl:template>

</xsl:stylesheet>
