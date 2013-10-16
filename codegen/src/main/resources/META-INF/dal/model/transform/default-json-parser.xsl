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
   <xsl:value-of select="$empty"/>public class DefaultJsonParser {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="declare-field-variables"/>
   <xsl:call-template name='method-static-parse'/>
   <xsl:call-template name='method-convert-value'/>
   <xsl:call-template name='method-on-array-begin'/>
   <xsl:call-template name='method-on-array-end'/>
   <xsl:call-template name='method-on-name'/>
   <xsl:call-template name='method-on-object-begin'/>
   <xsl:call-template name='method-on-object-end'/>
   <xsl:call-template name='method-on-value'/>
   <xsl:call-template name='method-parse'/>
   <xsl:call-template name='method-parse-children'/>
   <xsl:call-template name='method-to-class'/>
   <xsl:call-template name='method-to-date'/>
   <xsl:call-template name='class-json-reader'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:for-each select="(entity/attribute | entity/element | entity/entity-ref)[not(@render='false')]">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/entity-ref[not(@map='true' or @list='true' or @render='false')][@upper-name=$upper-name][1])=generate-id()">
         <xsl:if test="@upper-name != @upper-names">
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:if>
      <xsl:if test="generate-id((//entity/attribute | //entity/element | //entity/entity-ref)[not(@render='false')][@upper-name=$upper-name][1])=generate-id()">
         <xsl:choose>
            <xsl:when test="name()='entity-ref' and (@map='true' or @list='true')">
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-names"/>;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.EOFException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.IOException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.InputStream;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.InputStreamReader;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.Reader;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.StringReader;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.util.Stack;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="declare-field-variables">
   private DefaultLinker m_linker = new DefaultLinker(true);

   private Stack<xsl:value-of select="'&lt;String&gt;'" disable-output-escaping="yes"/> m_tags = new Stack<xsl:value-of select="'&lt;String&gt;'" disable-output-escaping="yes"/>();

   private Stack<xsl:value-of select="'&lt;Object&gt;'" disable-output-escaping="yes"/> m_objs = new Stack<xsl:value-of select="'&lt;Object&gt;'" disable-output-escaping="yes"/>();

   private <xsl:value-of select="entity[@root='true']/@entity-class"/> m_root;
<xsl:if test="//entity/element[@list='true' or @set='true'][not(@render='false')]">
   private boolean m_inElements = false;
</xsl:if>
</xsl:template>

<xsl:template name="method-static-parse">
   <xsl:variable name="current" select="."/>
   <xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity[@root='true']">
      <xsl:value-of select="$empty"/>   public static <xsl:value-of select="@entity-class"/> parse(InputStream in) throws IOException {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return parse(new InputStreamReader(in, "utf-8"));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public static <xsl:value-of select="@entity-class"/> parse(Reader reader) throws IOException {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return new DefaultJsonParser().parse(new JsonReader(reader));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public static <xsl:value-of select="@entity-class"/> parse(String json) throws IOException {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return parse(new StringReader(json));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-parse">
   <xsl:variable name="current" select="."/>
   <xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity[@root='true']">
      <xsl:value-of select="$empty"/>   private <xsl:value-of select="@entity-class"/> parse(JsonReader reader) throws IOException {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      try {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         reader.parse(this);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      } catch (EOFException e) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         if (!m_objs.isEmpty()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            throw new EOFException(String.format("Unexpected end while parsing %s", m_tags));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      m_linker.finish();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return m_root;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-parse-children">
   <xsl:for-each select="entity">
      <xsl:sort select="@build-method"/>

      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@parse-for-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="' '"/><xsl:value-of select="@param-name"/>, String tag, String value) {<xsl:value-of select="$empty-line"/>
      <xsl:call-template name="parse-children">
         <xsl:with-param name="indent" select="'      '"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="parse-children">
   <xsl:param name="indent"/>
   
   <xsl:variable name="current" select="."/>
   <xsl:variable name="children" select="(attribute | element | entity-ref)[not(@render='false')]"/>
   <xsl:if test="$children">
      <xsl:value-of select="$indent"/>
      <xsl:variable name="entity-refs" select="entity-ref[not(@render='false')]"/>
      <xsl:if test="$entity-refs">
         <xsl:value-of select="$empty"/>if (<xsl:value-of select="$empty"/>
         <xsl:for-each select="$entity-refs">
            <xsl:if test="position()!=1"> || </xsl:if>
            <xsl:value-of select="$empty"/><xsl:value-of select="@upper-names"/>.equals(tag)<xsl:value-of select="$empty"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>   // do nothing here<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>} else <xsl:value-of select="$empty"/>
      </xsl:if>
      <xsl:for-each select="(attribute | element)[not(@render='false')]">
         <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-name"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/><xsl:value-of select="'   '"/><xsl:value-of select="$current/@param-name"/>.<xsl:value-of select="$empty"/>
         <xsl:choose>
            <xsl:when test="@list='true' or @set='true'">
               <xsl:value-of select="@add-method"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="@set-method"/>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:value-of select="$empty"/>(<xsl:value-of select="$empty"/>
         <xsl:call-template name="convert-type">
            <xsl:with-param name="value" select="'value'"/>
         </xsl:call-template>
         <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>} else <xsl:value-of select="$empty"/>
      </xsl:for-each>
      <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="$current/@dynamic-attributes='true'">
            <xsl:value-of select="$indent"/><xsl:value-of select="'   '"/><xsl:value-of select="$current/@param-name"/>.setDynamicAttribute(tag, value);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$indent"/>   throw new RuntimeException(String.format("Unknown tag(%s) of %s under %s!", tag, <xsl:value-of select="$current/@param-name"/>, m_tags));<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="convert-type">
   <xsl:param name="value-type" select="@value-type"/>
   <xsl:param name="enum" select="@enum"/>
   <xsl:param name="value" select="@param-name"/>
   
   <xsl:choose>
      <xsl:when test="$enum='true'"><xsl:value-of select="$value-type"/>.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='String'"><xsl:value-of select="$value"/></xsl:when>
      <xsl:when test="$value-type='java.util.Date'">toDate(<xsl:value-of select="$value"/>, "<xsl:value-of select="@format"/>")</xsl:when>
      <xsl:when test="@format">
         <xsl:value-of select="$empty"/>toNumber(<xsl:value-of select="$value"/>, "<xsl:value-of select="@format"/>").<xsl:value-of select="$empty"/>
         <xsl:choose>
            <xsl:when test="$value-type='int'">intValue()</xsl:when>
            <xsl:when test="$value-type='Integer'">intValue()</xsl:when>
            <xsl:when test="$value-type='long'">longValue()</xsl:when>
            <xsl:when test="$value-type='Long'">longValue()</xsl:when>
            <xsl:when test="$value-type='short'">shortValue()</xsl:when>
            <xsl:when test="$value-type='Short'">shortValue()</xsl:when>
            <xsl:when test="$value-type='float'">floatValue()</xsl:when>
            <xsl:when test="$value-type='Float'">floatValue()</xsl:when>
            <xsl:when test="$value-type='double'">doubleValue()</xsl:when>
            <xsl:when test="$value-type='Double'">doubleValue()</xsl:when>
            <xsl:when test="$value-type='byte'">byteValue()</xsl:when>
            <xsl:when test="$value-type='Byte'">byteValue()</xsl:when>
            <xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
         </xsl:choose>
      </xsl:when>
      <xsl:when test="$value-type='boolean'">convert(Boolean.class, <xsl:value-of select="$value"/>, false)</xsl:when>
      <xsl:when test="$value-type='Boolean'">convert(Boolean.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='int'">convert(Integer.class, <xsl:value-of select="$value"/>, 0)</xsl:when>
      <xsl:when test="$value-type='Integer'">convert(Integer.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='long'">convert(Long.class, <xsl:value-of select="$value"/>, 0L)</xsl:when>
      <xsl:when test="$value-type='Long'">convert(Long.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='short'">convert(Short.class, <xsl:value-of select="$value"/>, (short) 0)</xsl:when>
      <xsl:when test="$value-type='Short'">convert(Short.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='float'">convert(Float.class, <xsl:value-of select="$value"/>, 0.0f)</xsl:when>
      <xsl:when test="$value-type='Float'">convert(Float.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='double'">convert(Double.class, <xsl:value-of select="$value"/>, 0.0)</xsl:when>
      <xsl:when test="$value-type='Double'">convert(Double.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='byte'">convert(Byte.class, <xsl:value-of select="$value"/>, (byte) 0)</xsl:when>
      <xsl:when test="$value-type='Byte'">convert(Byte.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='char'">convert(Character.class, <xsl:value-of select="$value"/>, (char) 0)</xsl:when>
      <xsl:when test="$value-type='Character'">convert(Character.class, <xsl:value-of select="$value"/>, null)</xsl:when>
      <xsl:when test="$value-type='Class&lt;?&gt;'">toClass(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="method-on-array-begin">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   protected void onArrayBegin() {<xsl:value-of select="$empty-line"/>
   <xsl:variable name="entities" select="entity[element[@list='true' or @set='true'] or entity-ref[@list='true']][ not(@render='false')]"/>
   <xsl:if test="$entities">
      <xsl:variable name="indent" select="'      '"/>
      <xsl:value-of select="$empty"/>      Object parent = m_objs.peek();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      String tag = m_tags.peek();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>
      <xsl:for-each select="$entities">
         <xsl:value-of select="$empty"/>if (parent instanceof <xsl:value-of select="@entity-class"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/><xsl:value-of select="'   '"/>
         <xsl:for-each select="element[(@list='true' or @set='true')][not(@render='false')]">
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-name"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_objs.push(parent);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_inElements = true;<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
         </xsl:for-each>
         <xsl:for-each select="entity-ref[@list='true'][not(@render='false')]">
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-names"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_objs.push(parent);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>      throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>} else <xsl:value-of select="$empty"/>
      </xsl:for-each>
      <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>   throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-on-array-end">
   protected void onArrayEnd() {
      m_objs.pop();
      m_tags.pop();
<xsl:if test="//entity/element[@list='true' or @set='true'][not(@render='false')]">
      m_inElements = false;
</xsl:if>
   }
</xsl:template>

<xsl:template name="method-on-name">
   <xsl:value-of select="$empty"/>   protected void onName(String name) {<xsl:value-of select="$empty-line"/>
      <xsl:variable name="elements" select="//entity/element[@list='true' or @set='true'][not(@render='false')]"/>
      <xsl:choose>
         <xsl:when test="$elements">
            <xsl:variable name="indent" select="'      '"/>
            <xsl:value-of select="$indent"/>if (m_inElements) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   Object parent = m_objs.peek();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   String tag = m_tags.peek();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/><xsl:value-of select="'   '"/>
            <xsl:for-each select="//entity[element[@list='true' or @set='true'][not(@render='false')]]">
               <xsl:variable name="entity" select="."/>
               <xsl:value-of select="$empty"/>if (parent instanceof <xsl:value-of select="$entity/@entity-class"/>) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/><xsl:value-of select="'      '"/>
               <xsl:for-each select="element[(@list='true' or @set='true')][not(@render='false')]">
                  <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-name"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>         ((<xsl:value-of select="$entity/@entity-class"/>) parent).<xsl:value-of select="@add-method"/>(name);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      } else <xsl:value-of select="$empty"/>
               </xsl:for-each>
               <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/>         throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/>      }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
            </xsl:for-each>
            <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>} else {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   m_tags.push(name);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      m_tags.push(name);<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-on-object-begin">
   <xsl:variable name="root" select="entity[@root='true']"/>
   <xsl:variable name="indent" select="'         '"/>
   protected void onObjectBegin() {
      if (m_objs.isEmpty()) { // root
         m_root = new <xsl:value-of select="$root/@entity-class"/>();
         m_objs.push(m_root);
         m_tags.push("");
      } else {
         Object parent = m_objs.peek();
         String tag = m_tags.peek();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity[entity-ref[not(@render='false')]]">
      <xsl:variable name="current" select="."/>
      <xsl:choose>
         <xsl:when test="position()=1"><xsl:value-of select="$indent"/></xsl:when>
         <xsl:otherwise> else </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>if (parent instanceof <xsl:value-of select="@entity-class"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:if test="entity-ref[not(@render='false')]">
         <xsl:value-of select="$indent"/><xsl:value-of select="'   '"/>
         <xsl:if test="entity-ref[@map='true' and not(@render='false')]">
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="$empty"/>
            <xsl:for-each select="entity-ref[@map='true' and not(@render='false')]">
               <xsl:if test="position()!=1"> || </xsl:if>
               <xsl:value-of select="@upper-names"/>.equals(tag)<xsl:value-of select="$empty"/>
            </xsl:for-each>
            <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_objs.push(parent);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
         </xsl:if>
         <xsl:for-each select="entity-ref[@list='true' and not(@render='false')]">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="entity" select="//entity[@name=$name]"/>
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-names"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/><xsl:value-of select="'      '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> = new <xsl:value-of select="$entity/@entity-class"/>();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_linker.<xsl:value-of select="@on-event-method"/>((<xsl:value-of select="$current/@entity-class"/>) parent, <xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_objs.push(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_tags.push("");<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
         </xsl:for-each>
         <xsl:for-each select="entity-ref[not(@list='true' or @map='true') and not(@render='false')]">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="entity" select="//entity[@name=$name]"/>
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-name"/>.equals(tag)) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/><xsl:value-of select="'      '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> = new <xsl:value-of select="$entity/@entity-class"/>();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_linker.<xsl:value-of select="@on-event-method"/>((<xsl:value-of select="$current/@entity-class"/>) parent, <xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>      m_objs.push(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>   } else <xsl:value-of select="$empty"/>
         </xsl:for-each>
      </xsl:if>
      <xsl:choose>
         <xsl:when test="entity-ref[not(@render='false')]">
            <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
            <xsl:choose>
               <xsl:when test="entity-ref[@list='true' and not(@xml-indent='true') and not(@render='false')]">
                  <xsl:call-template name="on-object-begin">
                     <xsl:with-param name="current" select="$current"/>
                     <xsl:with-param name="indent" select="'         '"/>
                  </xsl:call-template>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty"/>
               </xsl:when>
               <xsl:when test="entity-ref[@map='true' and not(@render='false')]">
                  <xsl:call-template name="on-object-begin">
                     <xsl:with-param name="current" select="$current"/>
                     <xsl:with-param name="indent" select="'      '"/>
                  </xsl:call-template>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="$indent"/>      throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$indent"/>      throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>} else <xsl:value-of select="$empty"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="position()=last()">
         <xsl:value-of select="$empty"/> else {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>   throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="on-object-begin">
   <xsl:param name="current"/>
   <xsl:param name="indent"/>

   <xsl:choose>
      <xsl:when test="entity-ref[@map='true' and not(@render='false')]">
         <xsl:value-of select="$indent"/>         String parentTag = m_tags.size() <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>= 2 ? m_tags.get(m_tags.size() - 2) : null;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/><xsl:value-of select="'         '"/>
         <xsl:for-each select="entity-ref[@map='true' and not(@render='false')]">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="entity" select="//entity[@name=$name]"/>
            <xsl:value-of select="$empty"/>if (<xsl:value-of select="@upper-names"/>.equals(parentTag)) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/><xsl:value-of select="'            '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> = new <xsl:value-of select="$entity/@entity-class"/>();<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>            m_linker.<xsl:value-of select="@on-event-method"/>((<xsl:value-of select="$current/@entity-class"/>) parent, <xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>            m_objs.push(<xsl:value-of select="@param-name"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$indent"/>         } else <xsl:value-of select="$empty"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>            throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>         }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>      }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:value-of select="$indent"/>      throw new RuntimeException(String.format("Unknown tag(%s) found at %s!", tag, m_tags));<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty"/>
      </xsl:otherwise>
   </xsl:choose>   
</xsl:template>

<xsl:template name="method-on-object-end">
   protected void onObjectEnd() {
      m_objs.pop();
      m_tags.pop();
   }
</xsl:template>

<xsl:template name="method-on-value">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   protected void onValue(String value) {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      Object parent = m_objs.peek();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      String tag = m_tags.pop();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="'      '"/>
   <xsl:for-each select="entity">
      <xsl:value-of select="$empty"/>if (parent instanceof <xsl:value-of select="@entity-class"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="'         '"/><xsl:value-of select="@parse-for-method"/>((<xsl:value-of select="@entity-class"/>) parent, tag, value);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      } else <xsl:value-of select="$empty"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>{<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>         throw new RuntimeException(String.format("Unknown tag(%s) under %s!", tag, parent));<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-to-class">
<xsl:if test="//entity/attribute[@value-type='Class&lt;?&gt;']">
   private Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'?'"/></xsl:call-template> toClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }
</xsl:if>
</xsl:template>

<xsl:template name="method-convert-value">
   @SuppressWarnings("unchecked")
   protected <xsl:value-of select="'&lt;T&gt;'" disable-output-escaping="yes"/> T convert(Class<xsl:value-of select="'&lt;T&gt;'" disable-output-escaping="yes"/> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }
</xsl:template>

<xsl:template name="method-to-date">
<xsl:if test="(//entity/attribute | //entity/element)[@value-type='java.util.Date'][not(@render='false')]">
   protected java.util.Date toDate(String str, String format) {
      try {
         return new java.text.SimpleDateFormat(format).parse(str);
      } catch (java.text.ParseException e) {
         throw new RuntimeException(String.format("Unable to parse date(%s) in format(%s)!", str, format), e);
      }
   }
</xsl:if>
<xsl:if test="(//entity/attribute | //entity/element)[@format and not(@value-type='java.util.Date')][not(@render='false')]">
   protected Number toNumber(String str, String format) {
      try {
         return new java.text.DecimalFormat(format).parse(str);
      } catch (java.text.ParseException e) {
         throw new RuntimeException(String.format("Unable to parse number(%s) in format(%s)!", str, format), e);
      }
   }
</xsl:if>
</xsl:template>

<xsl:template name="class-json-reader">
   static class JsonReader {
      private Reader m_reader;

      private char[] m_buffer = new char[2048];

      private int m_size;

      private int m_index;

      public JsonReader(Reader reader) {
         m_reader = reader;
      }

      private char next() throws IOException {
         if (m_index <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/>= m_size) {
            m_size = m_reader.read(m_buffer);
            m_index = 0;

            if (m_size == -1) {
               throw new EOFException();
            }
         }

         return m_buffer[m_index++];
      }

      public void parse(DefaultJsonParser parser) throws IOException {
         StringBuilder sb = new StringBuilder();
         boolean flag = false;

         while (true) {
            char ch = next();

            switch (ch) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
               break;
            case '{':
               parser.onObjectBegin();
               flag = false;
               break;
            case '}':
               if (flag) { // have value
                  parser.onValue(sb.toString());
                  sb.setLength(0);
               }

               parser.onObjectEnd();
               flag = false;
               break;
            case '\'':
            case '"':
               while (true) {
                  char ch2 = next();

                  if (ch2 != ch) {
                     if (ch2 == '\\') {
                        char ch3 = next();

                        sb.append(ch3);
                     } else {
                        sb.append(ch2);
                     }
                  } else {
                     if (!flag) {
                        parser.onName(sb.toString());
                     } else {
                        parser.onValue(sb.toString());
                        flag = false;
                     }

                     sb.setLength(0);
                     break;
                  }
               }

               break;
            case ':':
               if (sb.length() != 0) {
                  parser.onName(sb.toString());
                  sb.setLength(0);
               }

               flag = true;
               break;
            case ',':
               if (sb.length() != 0) {
                  if (!flag) {
                     parser.onName(sb.toString());
                  } else {
                     parser.onValue(sb.toString());
                  }

                  sb.setLength(0);
               }

               flag = false;
               break;
            case '[':
               parser.onArrayBegin();
               flag = false;
               break;
            case ']':
               parser.onArrayEnd();
               flag = false;
               break;
            default:
               sb.append(ch);
               break;
            }
         }
      }
   }
</xsl:template>

</xsl:stylesheet>
