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
   <xsl:value-of select="$empty"/>public class DefaultNativeBuilder2 implements IVisitor<xsl:if test="$policy-filter='true'">, IVisitorEnabled</xsl:if> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-commons'/>
   <xsl:call-template name='method-build-descriptor'/>
   <xsl:call-template name='method-enable-visitor'/>
   <xsl:call-template name='method-visit'/>
   <xsl:call-template name='method-write-methods'/>
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

      <xsl:variable name="upper-name-element" select="@upper-name-element"/>
      <xsl:if test="generate-id(//entity/element[not(@text='true' or @render='false')][@upper-name-element=$upper-name-element][1])=generate-id()">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name-element"/>;<xsl:value-of select="$empty-line"/>
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
   <xsl:value-of select="$empty"/>import java.io.ByteArrayOutputStream;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.DataOutputStream;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.IOException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.OutputStream;<xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity[@dynamic-attributes='true'] | entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IEntity;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:if test="$policy-filter='true'">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitorEnabled;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty"/>import <xsl:value-of select="entity/any/@entity-package"/>.Any;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-commons">
   <xsl:variable name="root" select="entity[@root='true']"/>
   private IVisitor m_visitor;

   private DataOutputStream m_out;

   public DefaultNativeBuilder2(OutputStream out) {
      m_out = new DataOutputStream(out);
      m_visitor = this;
   }

   public static byte[] build(IEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> entity) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream(8192);

      build(entity, out);
      return out.toByteArray();
   }

   public static void build(IEntity<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> entity, OutputStream out) throws IOException {
      DefaultNativeBuilder2 builder = new DefaultNativeBuilder2(out);

      builder.buildDescriptor();
      entity.accept(builder);
   }
</xsl:template>

<xsl:template name="method-build-descriptor">
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   private void buildDescriptor() throws IOException {<xsl:value-of select="$empty-line"/>
   <!-- version: 1.0 -->
   <xsl:value-of select="$empty"/>      writeVarint(10);<xsl:value-of select="$empty-line"/>
   <!-- entity count -->
   <xsl:value-of select="$empty"/>      writeVarint(<xsl:value-of select="count(entity[not(@render='false')])" />);<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity[not(@render='false')]">
      <xsl:value-of select="$empty-line"/>
      <!-- entity name -->
      <xsl:value-of select="$empty"/>      writeString(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      <!-- field count -->
      <xsl:value-of select="$empty"/>      writeVarint(<xsl:value-of select="count((attribute|element|entity-ref)[not(@render='false')])" />);<xsl:value-of select="$empty-line"/>
      <xsl:for-each select="attribute[not(@render='false')]">
         <!-- attribute name -->
         <xsl:value-of select="$empty"/>      writeString(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:for-each select="element[not(@render='false')]">
         <!-- element name -->
         <xsl:value-of select="$empty"/>      writeString(<xsl:value-of select="@upper-name-element"/>);<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:for-each select="entity-ref[not(@render='false')]">
         <!-- entity name -->
         <xsl:value-of select="$empty"/>      writeString(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-enable-visitor">
   <xsl:if test="$policy-filter='true'">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>    public void enableVisitor(IVisitor visitor) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      m_visitor = visitor;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-visit">
   <xsl:if test="entity/any">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="entity/any/@visit-method"/>(Any any) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      writeTag(1, 1);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      writeString(any.getName());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (any.getValue() != null) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeTag(2, 1);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeString(any.getValue());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (!any.getAttributes().isEmpty()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeTag(33, 2);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeInt(any.getAttributes().size());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         for (Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> attribute : any.getAttributes().entrySet()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            writeString(attribute.getKey());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            writeString(attribute.getValue());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (!any.getChildren().isEmpty()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeTag(34, 2);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         writeInt(any.getChildren().size());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         for (Any child : any.getChildren()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            visitAny(child);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      writeTag(63, 3);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>

      <xsl:variable name="entity" select="."/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:for-each select="(attribute | element)[not(@render='false')]">
         <xsl:variable name="index" select="position()"/>
         
         <xsl:choose>
            <xsl:when test="@primitive='true'">
               <xsl:value-of select="$empty"/>      writeTag(<xsl:value-of select="$index"/>, 0);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      write<xsl:call-template name="get-type-name"/>(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="name()='element' and (@list='true' or @set='true')">
               <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeTag(<xsl:value-of select="$index"/>, 2);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeInt(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().size());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            write<xsl:call-template name="get-type-name"><xsl:with-param name="value-type" select="@value-type-element"/></xsl:call-template>(<xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeTag(<xsl:value-of select="$index"/>, 1);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         write<xsl:call-template name="get-type-name"/>(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:for-each select="entity-ref[not(@render='false')]">
         <xsl:variable name="index" select="position() + 32"/>
         <xsl:variable name="name" select="@name"/>
         <xsl:variable name="current" select="//entity[@name=$name]"/>
         
         <xsl:choose>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeTag(<xsl:value-of select="$index"/>, 2);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeInt(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().size());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            <xsl:value-of select="'            '"/><xsl:value-of select="@local-name-element"/>.accept(m_visitor);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@map='true'">
               <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().isEmpty()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeTag(<xsl:value-of select="$index"/>, 2);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeInt(<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().size());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         for (<xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> : <xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().values()) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            <xsl:value-of select="'            '"/><xsl:value-of select="@local-name-element"/>.accept(m_visitor);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>      if (<xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>() != null) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         writeTag(<xsl:value-of select="$index"/>, 1);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@get-method"/>().accept(m_visitor);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
         <xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:if test="@dynamic-attributes='true'">
         <xsl:value-of select="$empty"/>      if (!<xsl:value-of select="$entity/@param-name"/>.getDynamicAttributes().isEmpty()) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>         writeTag(63, 2);<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>         writeInt(<xsl:value-of select="$entity/@param-name"/>.getDynamicAttributes().size());<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>         for (Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttribute : <xsl:value-of select="$entity/@param-name"/>.getDynamicAttributes().entrySet()) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            writeString(dynamicAttribute.getKey());<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            writeString(dynamicAttribute.getValue());<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty"/>      writeTag(63, 3);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-write-methods">
<xsl:variable name="properties" select="(//entity/attribute | //entity/element)[not(@render='false')]"/>
<xsl:if test="$properties[@value-type='Boolean' or @value-type='boolean']">
   private void writeBoolean(boolean value) {
      try {
         m_out.writeByte(value ? 1 : 0);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='byte' or @value-type='Byte']">
   private void writeByte(byte value) {
      try {
         m_out.writeByte(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='char' or @value-type='Character']">
   private void writeChar(char value) {
      try {
         writeVarint(value <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0xFFFFL);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='Class&lt;?&gt;']">
   private void writeClass(Class<xsl:value-of select="'&lt;?&gt;'" disable-output-escaping="yes"/> value) {
      try {
         m_out.writeUTF(value.getName());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='date' or @value-type='java.util.Date']">
   private void writeDate(java.util.Date value) {
      try {
         writeVarint(value.getTime());
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='double' or @value-type='Double']">
   private void writeDouble(double value) {
      try {
         m_out.writeDouble(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='float' or @value-type='Float']">
   private void writeFloat(float value) {
      try {
         m_out.writeFloat(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='int' or @value-type='Integer'] | //entity/element[@list='true' or @set='true'] or //entity/entity-ref[@list='true' or @map='true'] or @dynamic-attributes='true' or //entity/any">
   private void writeInt(int value) {
      try {
         writeVarint(value <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0xFFFFFFFFL);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='long' or @value-type='Long']">
   private void writeLong(long value) {
      try {
         writeVarint(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='short' or @value-type='Short']">
   private void writeShort(short value) {
      try {
         writeVarint(value <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0xFFFFL);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
<xsl:if test="$properties[@value-type='String']">
   private void writeString(String value) {
      try {
         m_out.writeUTF(value);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
</xsl:if>
   private void writeTag(int field, int type) {
      try {
         m_out.writeByte((field <xsl:value-of select="'&lt;&lt;'" disable-output-escaping="yes"/> 2) + type);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected void writeVarint(long value) throws IOException {
      while (true) {
         if ((value <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> ~0x7FL) == 0) {
            m_out.writeByte((byte) value);
            return;
         } else {
            m_out.writeByte(((byte) value <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0x7F) | 0x80);
            value <xsl:value-of select="'&gt;&gt;&gt;'" disable-output-escaping="yes"/>= 7;
         }
      }
   }
</xsl:template>

<xsl:template name="get-type-name">
   <xsl:param name="value-type" select="@value-type"/>
   
   <xsl:choose>
      <xsl:when test="@primitive">
         <xsl:choose>
            <xsl:when test="$value-type='boolean'">Boolean</xsl:when>
            <xsl:when test="$value-type='int'">Int</xsl:when>
            <xsl:when test="$value-type='long'">Long</xsl:when>
            <xsl:when test="$value-type='short'">Short</xsl:when>
            <xsl:when test="$value-type='float'">Float</xsl:when>
            <xsl:when test="$value-type='double'">Double</xsl:when>
            <xsl:when test="$value-type='byte'">Byte</xsl:when>
            <xsl:when test="$value-type='char'">Char</xsl:when>
            <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
         </xsl:choose>
      </xsl:when>
      <xsl:when test="$value-type='Integer'">Int</xsl:when>
      <xsl:when test="$value-type='Character'">Char</xsl:when>
      <xsl:when test="$value-type='java.util.Date'">Date</xsl:when>
      <xsl:when test="$value-type='Class&lt;?&gt;'">Class</xsl:when>
      <xsl:otherwise><xsl:value-of select="$value-type"/></xsl:otherwise>
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
