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
   <xsl:value-of select="$empty"/>public class DefaultSaxMaker implements IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'Attributes'"/></xsl:call-template> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-build-children'/>
   <xsl:call-template name='method-convert-value'/>
   <xsl:call-template name='method-to-class'/>
   <xsl:call-template name='method-to-date'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="entity/attribute[not(@text='true' or @render='false')]">
      <xsl:for-each select="entity/attribute[not(@text='true' or @render='false')]">
         <xsl:sort select="@upper-name"/>
   
         <xsl:variable name="name" select="@name"/>
         <xsl:if test="generate-id(//entity/attribute[not(@text='true' or @render='false')][@name=$name][1])=generate-id()">
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="entity[any and entity-ref[not(@render='false')]]">
      <xsl:value-of select="$empty"/>import java.util.Arrays;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import java.util.HashSet;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="entity[@dynamic-attributes='true'] | entity/any">
      <xsl:if test="entity[@dynamic-attributes='true']">
         <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:if>
   <xsl:value-of select="$empty"/>import org.xml.sax.Attributes;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.<xsl:value-of select='entity/any/@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-build-children">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:call-template name="method-build-any"/>
   </xsl:if>
   <xsl:for-each select="entity | entity/element[(@list='true' or @set='true') and not(@render='false')]">
      <xsl:sort select="@build-method"/>

      <xsl:choose>
         <xsl:when test="name()='element'">
            <xsl:variable name="build-method" select="@build-method"/>
         
            <xsl:if test="generate-id(//entity/element[@build-method=$build-method][(@list='true' or @set='true') and not(@render='false')][1])=generate-id()">
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(Attributes attributes) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      throw new UnsupportedOperationException();<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(Attributes attributes) {<xsl:value-of select="$empty-line"/>
            <xsl:call-template name="define-variable-from-attributes"/>
            <xsl:value-of select="'      '"/><xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name"/> = <xsl:call-template name="create-entity-instance"/>
            <xsl:call-template name="set-optional-fields"/>
            <xsl:call-template name="set-dynamic-attributes"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return <xsl:value-of select="@local-name"/>;<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-build-any">
   <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public Any <xsl:value-of select="entity/any/@build-method"/>(Attributes attributes) {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      throw new UnsupportedOperationException("Not needed!");<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="define-variable-from-attributes">
   <xsl:if test="attribute[not(@text='true' or @list='true' or @set='true' or @render='false')]">
      <xsl:for-each select="attribute[not(@text='true' or @list='true' or @set='true' or @render='false')]">
         <xsl:value-of select="$empty"/>      String <xsl:value-of select="@param-name"/> = attributes.getValue(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
   </xsl:if>
</xsl:template>

<xsl:template name="set-dynamic-attributes">
   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes = <xsl:value-of select="@param-name"/>.getDynamicAttributes();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      int length = attributes == null ? 0 : attributes.getLength();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      for (int i = 0; i <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> length; i++) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         String name = attributes.getQName(i);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         String value = attributes.getValue(i);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         dynamicAttributes.put(name, value);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:if test="attribute[not(@text='true' or @render='false')]">
         <xsl:for-each select="attribute[not(@text='true' or @render='false')]">
            <xsl:value-of select="$empty"/>      dynamicAttributes.remove(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:for-each>
      </xsl:if>
   </xsl:if>
</xsl:template>

<xsl:template name="set-optional-fields">
   <xsl:param name="entity" select="."/>
   
   <xsl:for-each select="attribute[not(@key='true' or @text='true' or @render='false' or @list='true' or @set='true')]">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (<xsl:value-of select="@param-name"/> != null) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$entity/@local-name"/>.<xsl:value-of select="@set-method"/>(<xsl:value-of select="$empty"/>
      <xsl:call-template name="convert-type"/>
      <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="convert-type">
   <xsl:param name="value-type" select="@value-type"/>
   <xsl:param name="enum-value-type" select="@enum-value-type"/>
   <xsl:param name="value" select="@param-name"/>
   
   <xsl:choose>
      <xsl:when test="$enum-value-type='true'"><xsl:value-of select="$value-type"/>.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='String'"><xsl:value-of select="$value"/></xsl:when>
      <xsl:when test="$value-type='java.util.Date'">toDate(<xsl:value-of select="$value"/>, "<xsl:value-of select="@format"/>", null)</xsl:when>
      <xsl:when test="@format">
         <xsl:value-of select="$empty"/>toNumber(<xsl:value-of select="$value"/>, "<xsl:value-of select="@format"/>", 0).<xsl:value-of select="$empty"/>
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

<xsl:template name="create-entity-instance">
   <xsl:value-of select="$empty"/>new <xsl:value-of select="@entity-class"/>(<xsl:value-of select="$empty"/>
   <xsl:for-each select="(attribute | element)[@key='true']">
      <xsl:choose>
         <xsl:when test="name()='element'">null</xsl:when>
         <xsl:when test="@value-type='String'">
            <xsl:value-of select="@param-name"/>
         </xsl:when>
         <xsl:when test="@primitive='true'">
            <xsl:value-of select="@param-name"/> == null ? 0 : <xsl:call-template name="convert-type"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="@param-name"/> == null ? null : <xsl:call-template name="convert-type"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="position()!=last()">, </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-convert-value">
   @SuppressWarnings("unchecked")
   protected <xsl:value-of select="'&lt;T&gt;'" disable-output-escaping="yes"/> T convert(Class<xsl:value-of select="'&lt;T&gt;'" disable-output-escaping="yes"/> type, String value, T defaultValue) {
      if (value == null) {
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

<xsl:template name="method-to-date">
<xsl:if test="(//entity/attribute | //entity/element)[@value-type='java.util.Date'][not(@render='false')]">
   protected java.util.Date toDate(String str, String format, java.util.Date defaultValue) {
      if (str == null || str.length() == 0) {
         return defaultValue;
      }

      try {
         return new java.text.SimpleDateFormat(format).parse(str);
      } catch (java.text.ParseException e) {
         throw new RuntimeException(String.format("Unable to parse date(%s) in format(%s)!", str, format), e);
      }
   }
</xsl:if>
<xsl:if test="(//entity/attribute | //entity/element)[@format and not(@value-type='java.util.Date')][not(@render='false')]">
   protected Number toNumber(String str, String format, Number defaultValue) {
      if (str == null || str.length() == 0) {
         return defaultValue;
      }

      try {
         return new java.text.DecimalFormat(format).parse(str);
      } catch (java.text.ParseException e) {
         throw new RuntimeException(String.format("Unable to parse number(%s) in format(%s)!", str, format), e);
      }
   }
</xsl:if>
</xsl:template>

</xsl:stylesheet>
