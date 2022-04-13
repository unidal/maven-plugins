<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model/entity[@root='true']"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:value-of select="$empty"/>/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class <xsl:value-of select="@class-name"/>Helper {<xsl:value-of select="$empty-line"/>
   public static String asXml(IEntity<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'?'"/></xsl:call-template> entity) {
      return new DefaultXmlBuilder().buildXml(entity);
   }

   public static <xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T extends IEntity&lt;T&gt;'"/></xsl:call-template> T fromXml(Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> entityType, InputStream in) throws IOException {
      return new DefaultXmlParser().parse(entityType, new InputSource(withoutBom(in)));
   }

   public static <xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T extends IEntity&lt;T&gt;'"/></xsl:call-template> T fromXml(Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> entityType, String xml) throws IOException {
      return new DefaultXmlParser().parse(entityType, new InputSource(new StringReader(xml)));
   }

   public static TenantReport fromXml(InputStream in) throws IOException {
      return fromXml(<xsl:value-of select="@class-name"/>.class, in);
   }

   public static TenantReport fromXml(String xml) throws IOException {
      return fromXml(<xsl:value-of select="@class-name"/>.class, xml);
   }

   /**
    * removes Byte Order Mark(BOM) at the head of windows UTF-8 file.
    */
   private static InputStream withoutBom(InputStream in) throws IOException {
      if (!(in instanceof BufferedInputStream)) {
         in = new BufferedInputStream(in);
      }

      in.mark(3);

      /** UTF-8, with BOM **/
      if (in.read() != 0xEF || in.read() != 0xBB || in.read() != 0xBF) {
         in.reset();
      }

      return in;
   }
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import <xsl:value-of select="$package"/>.entity.<xsl:value-of select="@class-name"/>;
import <xsl:value-of select="$package"/>.transform.DefaultXmlBuilder;
import <xsl:value-of select="$package"/>.transform.DefaultXmlParser;
import org.xml.sax.InputSource;
</xsl:template>

</xsl:stylesheet>
