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
   <xsl:value-of select="$empty"/>public class DefaultXmlBuilder implements IVisitor {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-commons'/>
   <xsl:call-template name='method-date-to-string'/>
   <xsl:call-template name='method-visit'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:for-each select="entity/attribute[not(@text='true' or @render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/attribute[not(@text='true' or @render='false')][@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:for-each select="entity/element[not(@text='true' or @render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:variable name="upper-name-element" select="@upper-name-element"/>
      <xsl:if test="generate-id(//entity/element[not(@text='true' or @render='false')][@upper-name=$upper-name or @upper-name-element=$upper-name-element][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name-element"/>;<xsl:value-of select="$empty-line"/>
         <xsl:if test="(@list='true' or @set='true') and @xml-indent='true'">
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:if>
   </xsl:for-each>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.<xsl:value-of select='entity/any/@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity | entity/entity-ref[@xml-indent='true' and not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id((//entity | //entity/entity-ref[@xml-indent='true' and not(@render='false')])[@upper-name=$upper-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity[@all-children-in-sequence='true']">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.BaseEntity;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IEntity;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-commons">
   private int m_level;

   private StringBuilder m_sb = new StringBuilder(4096);

   private boolean m_compact;

   public DefaultXmlBuilder() {
      this(false);
   }

   public DefaultXmlBuilder(boolean compact) {
      m_compact = compact;
   }

   public String buildXml(IEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> entity) {
      m_sb.setLength(0);
      entity.accept(this);
      return m_sb.toString();
   }

   protected void endTag(String name) {
      m_level--;

      indent();
      m_sb.append("<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>/").append(name).append("<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>\r\n");
   }

   protected String escape(Object value) {
      return escape(value, false);
   }
   
   protected String escape(Object value, boolean text) {
      if (value == null) {
         return null;
      }

      String str = value.toString();
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i++) {
         final char ch = str.charAt(i);

         switch (ch) {
         case '<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>':
            sb.append("&lt;");
            break;
         case '<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>':
            sb.append("&gt;");
            break;
         case '<xsl:value-of select="'&amp;'" disable-output-escaping="yes"/>':
            sb.append("&amp;");
            break;
         case '"':
            if (!text) {
               sb.append("<xsl:value-of select="'&amp;'" disable-output-escaping="yes"/>quot;");
               break;
            }
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }

   protected void indent() {
      if (!m_compact) {
         for (int i = m_level - 1; i <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>= 0; i--) {
            m_sb.append("   ");
         }
      }
   }

   protected void startTag(String name) {
      startTag(name, false, null);
   }
   
   protected void startTag(String name, boolean closed, java.util.Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes, Object... nameValues) {
      startTag(name, null, closed, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, java.util.Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes, Object... nameValues) {
      startTag(name, null, false, dynamicAttributes, nameValues);
   }

   protected void startTag(String name, Object text, boolean closed, java.util.Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes, Object... nameValues) {
      indent();

      m_sb.append('<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      if (dynamicAttributes != null) {
         for (java.util.Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> e : dynamicAttributes.entrySet()) {
            m_sb.append(' ').append(e.getKey()).append("=\"").append(escape(e.getValue())).append('"');
         }
      }

      if (text != null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> closed) {
         m_sb.append('<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>');
         m_sb.append(escape(text, true));
         m_sb.append("<xsl:value-of select="'&lt;/'" disable-output-escaping="yes"/>").append(name).append("<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>\r\n");
      } else {
         if (closed) {
            m_sb.append('/');
         } else {
            m_level++;
         }
   
         m_sb.append("<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>\r\n");
      }
   }
<xsl:if test="//entity/element[not(@render='false' or @text='true')]">
   private void tagWithText(String name, String text, Object... nameValues) {
      if (text == null) {
         return;
      }
      
      indent();

      m_sb.append('<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>').append(name);

      int len = nameValues.length;

      for (int i = 0; i + 1 <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i += 2) {
         Object attrName = nameValues[i];
         Object attrValue = nameValues[i + 1];

         if (attrValue != null) {
            m_sb.append(' ').append(attrName).append("=\"").append(escape(attrValue)).append('"');
         }
      }

      m_sb.append("<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>");
      m_sb.append(escape(text, true));
      m_sb.append("<xsl:value-of select="'&lt;'" disable-output-escaping="yes"/>/").append(name).append("<xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>\r\n");
   }
</xsl:if>
</xsl:template>

<xsl:template name="method-date-to-string">
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
</xsl:template>

<xsl:template name="method-visit">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="entity/any/@visit-method"/>(<xsl:value-of select="entity/any/@entity-class"/> any) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (any.getChildren().isEmpty()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         if (any.hasValue()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            tagWithText(any.getName(), any.getValue());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         } else {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            startTag(any.getName(), true, any.getAttributes());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      } else {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         startTag(any.getName(), false, any.getAttributes());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         for (Any child : any.getChildren()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            visitAny(child);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         endTag(any.getName());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@all-children-in-sequence='true'">
            <xsl:value-of select="$empty"/>      startTag(<xsl:value-of select="@upper-name"/>, <xsl:call-template name="get-dynamic-attributes"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      for (BaseEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> child : <xsl:value-of select="@param-name"/>.<xsl:value-of select="@method-get-all-children-in-sequence"/>()) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         child.accept(this);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      endTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="(entity-ref | element[not(@text='true')])[not(@render='false')] | any">
            <xsl:value-of select="$empty"/>      startTag(<xsl:value-of select="@upper-name"/>, <xsl:call-template name="get-dynamic-attributes"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
            <xsl:if test="(attribute|element)[@text='true']">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      if (<xsl:value-of select="@param-name"/>.<xsl:value-of select="(attribute|element)[@text='true']/@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         m_sb.append(<xsl:value-of select="@param-name"/>.<xsl:value-of select="(attribute|element)[@text='true']/@get-method"/>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:call-template name="visit-children"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      endTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="(attribute | element)[@text='true']">
            <xsl:value-of select="$empty"/>      startTag(<xsl:value-of select="@upper-name"/>, <xsl:value-of select="@param-name"/>.<xsl:value-of select="(attribute | element)[@text='true']/@get-method"/>(), true, <xsl:call-template name="get-dynamic-attributes"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="attribute[not(@render='false')]">
            <xsl:value-of select="$empty"/>      startTag(<xsl:value-of select="@upper-name"/>, true, <xsl:call-template name="get-dynamic-attributes"/><xsl:call-template name="tag-fields"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
      </xsl:choose>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="visit-children">
   <xsl:variable name="current" select="."/>
   <xsl:for-each select="element[not(@text='true')][not(@render='false')]">
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@list='true' or @map='true' or @set='true'">
            <xsl:variable name="suffix">
               <xsl:choose>
                  <xsl:when test="@map='true'">.values()</xsl:when>
                  <xsl:otherwise></xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
            <xsl:if test="@xml-indent='true'">
               <xsl:value-of select="$empty"/>         startTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>()<xsl:value-of select="$suffix"/>) {<xsl:value-of select="$empty-line"/>
            <xsl:choose>
               <xsl:when test="@value-type-element='String'">
                  <xsl:value-of select="$empty"/>            tagWithText(<xsl:value-of select="@upper-name-element"/>, <xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="$empty"/>            tagWithText(<xsl:value-of select="@upper-name-element"/>, <xsl:value-of select="@local-name-element"/> == null ? null : String.valueOf(<xsl:value-of select="@local-name-element"/>));<xsl:value-of select="$empty-line"/>
               </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
            <xsl:if test="@xml-indent='true'">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         endTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:choose>
               <xsl:when test="@value-type-element='String'">
                  <xsl:value-of select="$empty"/>      tagWithText(<xsl:value-of select="@upper-name-element"/>, <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="$empty"/>      tagWithText(<xsl:value-of select="@upper-name-element"/>, <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>() == null ? null : String.valueOf(<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>()));<xsl:value-of select="$empty-line"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
   <xsl:for-each select="entity-ref[not(@render='false')]">
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@list='true' or @map='true' or @set='true'">
            <xsl:variable name="suffix">
               <xsl:choose>
                  <xsl:when test="@map='true'">.values()</xsl:when>
                  <xsl:otherwise></xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
            <xsl:if test="@xml-indent='true'">
               <xsl:value-of select="$empty"/>         startTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:value-of select="$empty"/>         for (<xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>()<xsl:value-of select="$suffix"/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>            <xsl:value-of select="'            '"/><xsl:value-of select="$entity/@visit-method"/>(<xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
            <xsl:if test="@xml-indent='true'">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         endTag(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            </xsl:if>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$entity/@visit-method"/>(<xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
   <xsl:if test="any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      for (Any any : <xsl:value-of select="@param-name"/>.<xsl:value-of select="any/@get-method"/>()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         visitAny(any);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="tag-fields">
   <xsl:param name="entity" select="."/>
   
   <xsl:for-each select="attribute[not(@text='true' or @render='false')]">
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

</xsl:stylesheet>
