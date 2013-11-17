<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="policy-filter">
   <xsl:call-template name="model-policy">
      <xsl:with-param name="name" select="'filter'"/>
   </xsl:call-template>
</xsl:variable>

<xsl:template match="/">
   <xsl:apply-templates select="/model"/>
</xsl:template>

<xsl:template match="model">
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class DefaultJsonBuilder implements IVisitor<xsl:if test="$policy-filter='true'">, IVisitorEnabled</xsl:if> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-commons'/>
   <xsl:call-template name='method-visit'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:for-each select="entity/attribute[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/attribute[@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:for-each select="entity/element[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/element[@upper-name=$upper-name][1])=generate-id()">
         <xsl:choose>
            <xsl:when test="@json-list='true' or @set='true'">
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name-element"/>;<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:for-each>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.<xsl:value-of select='entity/any/@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity/entity-ref[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name">
         <xsl:choose>
            <xsl:when test="@json-list='true' or @json-map='true'"><xsl:value-of select="@upper-names"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="@upper-name"/></xsl:otherwise>
         </xsl:choose>
      </xsl:variable>
      <xsl:if test="generate-id((//entity/entity-ref[not(@render='false')])[(@json-list='true' or @json-map='true') and @upper-names=$upper-name or not (@json-list='true' or @json-map='true') and @upper-name=$upper-name][1])=generate-id()">
         <xsl:choose>
            <xsl:when test="@json-list='true' or @json-map='true'">
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-names"/>;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.Collection;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.List;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity[@all-children-in-sequence='true']">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.BaseEntity;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IEntity;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:if test="$policy-filter='true'">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitorEnabled;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-commons">
   private IVisitor m_visitor;

   private int m_level;

   private StringBuilder m_sb;

   private boolean m_compact;

   public DefaultJsonBuilder() {
      this(false);
   }

   public DefaultJsonBuilder(boolean compact) {
      this(compact, new StringBuilder(4096));
   }

   public DefaultJsonBuilder(boolean compact, StringBuilder sb) {
      m_compact = compact;
      m_sb = sb;
      m_visitor = this;
   }

   protected void arrayBegin(String name) {
      indent();
      m_sb.append('"').append(name).append(m_compact ? "\":[" : "\": [\r\n");
      m_level++;
   }

   protected void arrayEnd(String name) {
      m_level--;

      trimComma();
      indent();
      m_sb.append("],").append(m_compact ? "" : "\r\n");
   }

   protected void attributes(Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes, Object... nameValues) {
      int len = nameValues.length;

      for (int i = 0; i + 1 <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            if (attrValue instanceof List) {
               @SuppressWarnings("unchecked")
               List<xsl:value-of select="'&lt;Object&gt;'" disable-output-escaping="yes"/> list = (List<xsl:value-of select="'&lt;Object&gt;'" disable-output-escaping="yes"/>) attrValue;

               if (!list.isEmpty()) {
                  indent();
                  m_sb.append('"').append(attrName).append(m_compact ? "\":[" : "\": [");

                  for (Object item : list) {
                     m_sb.append(' ');
                     toString(m_sb, item);
                     m_sb.append(',');
                  }

                  m_sb.setLength(m_sb.length() - 1);
                  m_sb.append(m_compact ? "]," : " ],\r\n");
               }
            } else {
               if (m_compact) {
                  m_sb.append('"').append(attrName).append("\":");
                  toString(m_sb, attrValue);
                  m_sb.append(",");
               } else {
                  indent();
                  m_sb.append('"').append(attrName).append("\": ");
                  toString(m_sb, attrValue);
                  m_sb.append(",\r\n");
               }
            }
         }
      }

      if (dynamicAttributes != null) {
         for (Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> e : dynamicAttributes.entrySet()) {
            if (m_compact) {
               m_sb.append('"').append(e.getKey()).append("\":");
               toString(m_sb, e.getValue());
               m_sb.append(",");
            } else {
               indent();
               m_sb.append('"').append(e.getKey()).append("\": ");
               toString(m_sb, e.getValue());
               m_sb.append(",\r\n");
            }
         }
      }
   }

   public String build(Collection<xsl:value-of select="'&lt;?'" disable-output-escaping="yes"/> extends IEntity<xsl:value-of select="'&lt;?&gt;&gt;'" disable-output-escaping="yes"/> entities) {
      m_sb.append('[');

      if (entities != null) {
         for (IEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> entity : entities) {
            objectBegin(null);
            entity.accept(this);
            objectEnd(null);
         }

         trimComma();
      }

      m_sb.append(']');

      return m_sb.toString();
   }

   public String build(IEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> entity) {
      objectBegin(null);
      entity.accept(this);
      objectEnd(null);
      trimComma();

      return m_sb.toString();
   }
<xsl:if test="$policy-filter='true'">
   @Override
   public void enableVisitor(IVisitor visitor) {
      m_visitor = visitor;
   }
</xsl:if>
   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void objectBegin(String name) {
      indent();

      if (name == null) {
         m_sb.append("{").append(m_compact ? "" : "\r\n");
      } else {
         m_sb.append('"').append(name).append(m_compact ? "\":{" : "\": {\r\n");
      }

      m_level++;
   }

   protected void objectEnd(String name) {
      m_level--;

      trimComma();
      indent();
      m_sb.append(m_compact ? "}," : "},\r\n");
   }
<xsl:if test="(//entity/attribute | //entity/element)[@value-type='java.util.Date'][not(@render='false')]">
   protected String toString(java.util.Date date, String format) {
      if (date != null) {
         return new java.text.SimpleDateFormat(format).format(date);
      } else {
         return null;
      }
   }
</xsl:if>
<xsl:if test="(//entity/attribute | //entity/element)[@format and not(@value-type='java.util.Date')][not(@render='false')]">
   protected String toString(Number number, String format) {
      if (number != null) {
         return new java.text.DecimalFormat(format).format(number);
      } else {
         return null;
      }
   }
</xsl:if>
   protected void toString(StringBuilder sb, Object value) {
      if (value == null) {
         sb.append("null");
      } else if (value instanceof Boolean || value instanceof Number) {
         sb.append(value);
      } else {
         String val = value.toString();
         int len = val.length();

         sb.append('"');

         for (int i = 0; i <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i++) {
            char ch = val.charAt(i);

            switch (ch) {
            case '"':
               sb.append("\\\"");
               break;
            case '\t':
               sb.append("\\t");
               break;
            case '\r':
               sb.append("\\r");
               break;
            case '\n':
               sb.append("\\n");
               break;
            default:
               sb.append(ch);
               break;
            }
         }

         sb.append('"');
      }
   }

   protected void trimComma() {
      int len = m_sb.length();

      if (m_compact) {
         if (len <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/> 1 <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> m_sb.charAt(len - 1) == ',') {
            m_sb.replace(len - 1, len, "");
         }
      } else {
         if (len <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/> 3 <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> m_sb.charAt(len - 3) == ',') {
            m_sb.replace(len - 3, len - 2, "");
         }
      }
   }
</xsl:template>

<xsl:template name="method-visit">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="entity/any/@visit-method"/>(<xsl:value-of select="entity/any/@entity-class"/> any) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      objectBegin(any.getName());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      attributes(any.getAttributes());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      for (Any child : any.getChildren()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         visitAny(child);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      objectEnd(any.getName());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:if test="@root='true'">
         <xsl:value-of select="$empty"/>      objectBegin(null);<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:choose>
         <xsl:when test="@all-children-in-sequence='true'">
            <xsl:value-of select="$empty"/>      objectBegin(<xsl:value-of select="@upper-name"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      for (BaseEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> child : <xsl:value-of select="@param-name"/>.<xsl:value-of select="@method-get-all-children-in-sequence"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         child.accept(this);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      objectEnd(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:if test="(attribute | element)[not(@render='false' or @json-list='true' or @set='true')]">
               <xsl:value-of select="$empty"/>      attributes(<xsl:call-template name="get-dynamic-attributes"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:if test="element[not(@render='false') and (@json-list='true' or @set='true')]">
               <xsl:variable name="entity" select="."/>
               <xsl:for-each select="element[not(@render='false') and (@json-list='true' or @set='true')]">
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>         arrayBegin(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-element" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>            indent();<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>            m_sb.append('"').append(<xsl:value-of select="@param-name-element"/>).append(m_compact ? "\"," : "\",\r\n");<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>         arrayEnd(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
               </xsl:for-each>
            </xsl:if>
            <xsl:call-template name="visit-children"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="@root='true'">
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>      objectEnd(null);<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>      trimComma();<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="visit-children">
   <xsl:variable name="current" select="."/>
   <xsl:for-each select="entity-ref[not(@render='false')]">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@json-map='true'">
            <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         objectBegin(<xsl:value-of select="@upper-names"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-entry" disable-output-escaping="yes"/> e : <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().entrySet()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            String key = String.valueOf(e.getKey());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            objectBegin(key);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            e.getValue().accept(m_visitor);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            objectEnd(key);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         objectEnd(<xsl:value-of select="@upper-names"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@json-list='true'">
            <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         arrayBegin(<xsl:value-of select="@upper-names"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         for (<xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>()<xsl:if test="@map='true'">.values()</xsl:if>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            objectBegin(null);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            <xsl:value-of select="'            '"/><xsl:value-of select="@local-name-element"/>.accept(m_visitor);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            objectEnd(null);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         arrayEnd(<xsl:value-of select="@upper-names"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         objectBegin(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().accept(m_visitor);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         objectEnd(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="tag-fields">
   <xsl:param name="entity" select="."/>
   
   <xsl:for-each select="(attribute | element)[not(@render='false' or @json-list='true' or @set='true')]">
      <xsl:choose>
         <xsl:when test="@value-type='Class&lt;?&gt;'">
            <xsl:value-of select="$empty"/>, <xsl:value-of select="@upper-name"/>, <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>() == null ? null : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().getName()<xsl:value-of select="$empty"/>
         </xsl:when>
         <xsl:when test="@value-type='boolean'">
            <xsl:value-of select="$empty"/>, <xsl:value-of select="@upper-name"/>, <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@is-method"/>()<xsl:value-of select="$empty"/>
         </xsl:when>
         <xsl:when test="@format">
            <xsl:value-of select="$empty"/>, <xsl:value-of select="@upper-name"/>, toString(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>(), "<xsl:value-of select="@format"/>")<xsl:value-of select="$empty"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>, <xsl:value-of select="@upper-name"/>, <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()<xsl:value-of select="$empty"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="get-dynamic-attributes">
   <xsl:param name="entity" select="."/>
   
   <xsl:choose>
      <xsl:when test="@dynamic-attributes='true'">
         <xsl:value-of select="$entity/@param-name"/>.getDynamicAttributes()<xsl:value-of select="$empty"/>
      </xsl:when>
      <xsl:otherwise>null</xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="model-policy">
   <xsl:param name="name"/>
   <xsl:param name="default" select="'false'"/>
   
   <xsl:variable name="model" select="/model"/>
   <xsl:variable name="enable-policy" select="$model/attribute::*[name()=concat('enable-', $name)]"/>
   <xsl:variable name="disable-policy" select="$model/attribute::*[name()=concat('disable-', $name)]"/>
   <xsl:choose>
      <xsl:when test="$disable-policy">
         <xsl:value-of select="not($disable-policy='true')"/>
      </xsl:when>
      <xsl:when test="$enable-policy">
         <xsl:value-of select="$enable-policy='true'"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$default"/>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
