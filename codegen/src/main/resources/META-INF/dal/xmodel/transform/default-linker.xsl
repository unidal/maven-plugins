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
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class DefaultLinker implements ILinker {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='declare-field-variables'/>
   <xsl:call-template name='method-on-event'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:value-of select="$empty"/>import java.util.ArrayList;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.List;<xsl:value-of select="$empty-line"/>

   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="declare-field-variables">
<xsl:if test="not(//entity/entity-ref/@map='true')">
   <xsl:value-of select="$empty"/>   @SuppressWarnings("unused")
</xsl:if>
   <xsl:value-of select="$empty"/>   private boolean m_deferrable;

   private List<xsl:value-of select="'&lt;Runnable&gt;'" disable-output-escaping="yes"/> m_deferedJobs = new ArrayList<xsl:value-of select="'&lt;Runnable&gt;'" disable-output-escaping="yes"/>();

   public DefaultLinker(boolean deferrable) {
      m_deferrable = deferrable;
   }

   public void finish() {
      for (Runnable job : m_deferedJobs) {
         job.run();
      }
   }
</xsl:template>

<xsl:template name="method-on-event">
   <xsl:for-each select="//entity/entity-ref">
      <xsl:sort select="@on-event-method"/>

      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public boolean <xsl:value-of select="@on-event-method"/>(final <xsl:value-of select="../@entity-class"/> parent, final <xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$entity/@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@list='true'">
            <xsl:value-of select="$empty"/>      parent.<xsl:value-of select="@add-method"/>(<xsl:value-of select="$entity/@param-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@map='true'">
            <xsl:value-of select="$empty"/>      if (m_deferrable) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         m_deferedJobs.add(new Runnable() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            @Override<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            public void run() {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>               parent.<xsl:value-of select="@add-method"/>(<xsl:value-of select="$entity/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         });<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      } else {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         parent.<xsl:value-of select="@add-method"/>(<xsl:value-of select="$entity/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      parent.<xsl:value-of select="@set-method"/>(<xsl:value-of select="$entity/@param-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>      return true;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
