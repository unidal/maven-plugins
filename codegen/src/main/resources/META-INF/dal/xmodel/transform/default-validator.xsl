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
   <xsl:value-of select="$empty"/>public class DefaultValidator implements IVisitor {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-commons'/>
   <xsl:call-template name='method-visit'/>
   <xsl:call-template name='class-path'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:for-each select="entity/attribute[@required='true' and not(@render='false')]">
      <xsl:sort select="@upper-name"/>
   
      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/attribute[@required='true' and not(@render='false')][@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:for-each select="entity[entity-ref | attribute[@required='true' and not(@render='false')]] | entity/entity-ref[@xml-indent='true']">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id((//entity | //entity/entity-ref[@xml-indent='true'])[@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.Stack;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-commons">
   private Path m_path = new Path();
   
   protected void assertRequired(String name, Object value) {
      if (value == null) {
         throw new RuntimeException(String.format("%s at path(%s) is required!", name, m_path));
      }
   }
</xsl:template>

<xsl:template name="method-visit">
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>
      
      <xsl:variable name="entity" select="."/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:if test="entity-ref | attribute[@required='true' and not(@render='false')]">
         <xsl:value-of select="$empty"/>      m_path.down(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         <xsl:for-each select="attribute[@required='true' and not(@nullable='true') and not(@render='false')]">
            <xsl:if test="position()=1">
               <xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:value-of select="$empty"/>      assertRequired(<xsl:value-of select="@upper-name"/>, <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
         </xsl:for-each>
         <xsl:if test="entity-ref">
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="'      '"/><xsl:value-of select="@visit-children-method"/>(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:if>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>      m_path.up(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      
      <xsl:if test="entity-ref">
         <xsl:call-template name="method-visit-children"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-visit-children">
   <xsl:variable name="entity" select="."/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   protected void <xsl:value-of select="@visit-children-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity-ref">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="current" select="//entity[@name=$name]"/>
      <xsl:choose>
         <xsl:when test="@list='true' and @xml-indent='true'">
            <xsl:value-of select="$empty"/>      m_path.down(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$current/@param-name"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$current/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_path.up(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@list='true'">
            <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$current/@param-name"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$current/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@map='true' and @xml-indent='true'">
            <xsl:value-of select="$empty"/>      m_path.down(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$current/@param-name"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().values()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$current/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      m_path.up(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@map='true'">
            <xsl:value-of select="$empty"/>      for (<xsl:value-of select="$current/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$current/@param-name"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().values()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$current/@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@visit-method"/>(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="position()!=last()">
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="class-path">
   static class Path {
      private Stack<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'String'"/></xsl:call-template> m_sections = new Stack<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'String'"/></xsl:call-template>();

      public Path down(String nextSection) {
         m_sections.push(nextSection);

         return this;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder();

         for (String section : m_sections) {
            sb.append('/').append(section);
         }

         return sb.toString();
      }

      public Path up(String currentSection) {
         if (m_sections.isEmpty() || !m_sections.peek().equals(currentSection)) {
            throw new RuntimeException("INTERNAL ERROR: stack mismatched!");
         }

         m_sections.pop();
         return this;
      }
   }
</xsl:template>

</xsl:stylesheet>
