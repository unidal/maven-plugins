<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>

<xsl:param name="package"/>
<xsl:param name="module"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/wizard/webapp/module[@name=$module]/page[@name=$name]"/>
</xsl:template>

<xsl:template match="page">
<xsl:variable name="policy-add">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'add'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-delete">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'delete'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:variable name="policy-edit">
   <xsl:call-template name="single-policy">
      <xsl:with-param name="name" select="'edit'"/>
   </xsl:call-template>
</xsl:variable>
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;

public enum <xsl:value-of select="@action-class"/> implements org.unidal.web.mvc.Action {
   LIST("list"),
<xsl:if test="$policy-add='true'">
   ADD("add"),
</xsl:if>
<xsl:if test="$policy-delete='true'">
   DELETE("delete"),
</xsl:if>
<xsl:if test="$policy-edit='true'">
   EDIT("edit"),
</xsl:if>
   VIEW("view");

   private String m_name;

   private <xsl:value-of select="@action-class"/>(String name) {
      m_name = name;
   }

   public static <xsl:value-of select="@action-class"/> getByName(String name, <xsl:value-of select="@action-class"/> defaultAction) {
      for (<xsl:value-of select="@action-class"/> action : <xsl:value-of select="@action-class"/>.values()) {
         if (action.getName().equals(name)) {
            return action;
         }
      }

      return defaultAction;
   }

   @Override
   public String getName() {
      return m_name;
   }
}
</xsl:template>

</xsl:stylesheet>
