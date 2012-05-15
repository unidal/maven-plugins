<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import <xsl:value-of select="/model/@model-package"/>.BaseEntity;
import <xsl:value-of select="/model/@model-package"/>.IVisitor;

public class Any extends BaseEntity<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> {
   private String m_name;

   private String m_value;

   private Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> m_attributes;

   private List<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> m_children;

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitAny(this);
   }

   public Any addChild(Any any) {
      children(false).add(any);
      return this;
   }

   protected Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> attributes(boolean readonly) {
      if (m_attributes == null) {
         if (readonly) {
            return Collections.emptyMap();
         }

         m_attributes = new HashMap<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/>();
      }

      return m_attributes;
   }

   protected List<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> children(boolean readonly) {
      if (m_children == null) {
         if (readonly) {
            return Collections.emptyList();
         }

         m_children = new ArrayList<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/>();
      }

      return m_children;
   }

   public List<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> getAllChildren(String name) {
      List<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> all = new ArrayList<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/>();

      for (Any child : m_children) {
         if (child.getName().equals(name)) {
            all.add(child);
         }
      }

      return all;
   }

   public String getAttribute(String name) {
      return attributes(true).get(name);
   }

   public Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> getAttributes() {
      return attributes(true);
   }

   public List<xsl:value-of select="'&lt;Any&gt;'" disable-output-escaping="yes"/> getChildren() {
      return children(true);
   }

   public Any getFirstChild(String name) {
      for (Any child : children(true)) {
         if (child.getName().equals(name)) {
            return child;
         }
      }

      return null;
   }

   public String getName() {
      return m_name;
   }

   public String getValue() {
      return m_value;
   }

   public boolean hasValue() {
      return m_value != null;
   }

   @Override
   public void mergeAttributes(Any other) {
      attributes(false).putAll(other.getAttributes());
   }

   public Any setAttribute(String name, String value) {
      attributes(false).put(name, value);
      return this;
   }

   public Any setName(String name) {
      m_name = name;
      return this;
   }

   public Any setValue(String value) {
      m_value = value;
      return this;
   }

   @Override
   public String toString() {
      if (m_value != null) {
         return String.format("<xsl:value-of select="'&lt;%s&gt;%s&lt;/%1$s&gt;'" disable-output-escaping="yes"/>", m_name,m_value);
      } else {
         return super.toString();
      }
   }
}
</xsl:template>

</xsl:stylesheet>
