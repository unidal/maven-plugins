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
   <xsl:value-of select="$empty"/>public class VisitorChain implements IVisitor {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-common'/>
   <xsl:call-template name='method-visit'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:value-of select="$empty"/>import java.util.Arrays;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.List;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.Stack;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.concurrent.atomic.AtomicInteger;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity[@all-children-in-sequence='true']">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.BaseEntity;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IFilter;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitorEnabled;<xsl:value-of select="$empty-line"/>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.Any;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-common">
   private IVisitor m_visitor;

   private List<xsl:call-template name="lt"/>IFilter<xsl:call-template name="gt"/> m_filters;

   private Stack<xsl:call-template name="lt"/>Object<xsl:call-template name="gt"/> m_objs = new Stack<xsl:call-template name="lt"/>Object<xsl:call-template name="gt"/>();

   private Stack<xsl:call-template name="lt"/>AtomicInteger<xsl:call-template name="gt"/> m_filterIndexes = new Stack<xsl:call-template name="lt"/>AtomicInteger<xsl:call-template name="gt"/>();

   public VisitorChain(IVisitor visitor, IFilter... filters) {
      m_visitor = visitor;
      m_filters = Arrays.asList(filters);

      if (m_visitor instanceof IVisitorEnabled) {
         ((IVisitorEnabled) m_visitor).enableVisitor(this);
      } else {
         System.err.println(String.format("%s should implement the %s!", m_visitor.getClass(), IVisitorEnabled.class));
      }
   }

   IFilter getNextFilter(Object current) {
      Object last = m_objs.isEmpty() ? null : m_objs.peek();
      AtomicInteger index;

      if (last == current) {
         index = m_filterIndexes.peek();
      } else {
         if (last != null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> last.getClass() == current.getClass()) {
            m_objs.pop();
            m_filterIndexes.pop();
         }

         index = new AtomicInteger();

         m_objs.push(current);
         m_filterIndexes.push(index);
      }

      int value = index.getAndIncrement();

      if (value <xsl:call-template name="lt"/> m_filters.size()) {
         return m_filters.get(value);
      } else {
         m_objs.pop();
         m_filterIndexes.pop();

         return null;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(1024);
      boolean first = true;

      sb.append("VisitorChain[");

      for (IFilter filter : m_filters) {
         if (first) {
            first = false;
         } else {
            sb.append("--<xsl:call-template name="gt"/>");
         }

         sb.append(filter.getClass().getName());
      }

      if (m_visitor != null) {
         sb.append("-<xsl:call-template name="gt"/>");
         sb.append(m_visitor.getClass().getName());
      }

      sb.append("]");
      return sb.toString();
   }
</xsl:template>

<xsl:template name="method-visit">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="entity/any/@visit-method"/>(Any any) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>
      
      <xsl:variable name="entity" select="."/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@all-children-in-sequence='true'">
            <xsl:value-of select="$empty"/>      for (BaseEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> child : <xsl:value-of select="@param-name"/>.<xsl:value-of select="@method-get-all-children-in-sequence"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         child.accept(this);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="entity-ref">
            <xsl:value-of select="$empty"/>      IFilter next = getNextFilter(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      if (next != null) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         next.<xsl:value-of select="@visit-method"/>(<xsl:value-of select="@param-name"/>, this);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      } else {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         m_visitor.<xsl:value-of select="@visit-method"/>(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
      </xsl:choose>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:if test="position()!=last()">
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

</xsl:stylesheet>
