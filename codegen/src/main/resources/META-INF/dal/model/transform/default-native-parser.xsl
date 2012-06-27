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
   <xsl:value-of select="$empty"/>public class DefaultNativeParser implements IVisitor {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-commons'/>
   <xsl:call-template name='method-visit'/>
   <xsl:call-template name='method-read-methods'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:value-of select="$empty"/>import java.io.DataInputStream;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.IOException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.InputStream;<xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity[@dynamic-attributes='true']">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="/model/@model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-commons">
   <xsl:variable name="root" select="entity[@root='true']"/>
   private DefaultLinker m_linker = new DefaultLinker(true);

   private DataInputStream m_in;

   public DefaultNativeParser(InputStream in) {
      m_in = new DataInputStream(in);
   }

   public static <xsl:value-of select="$root/@entity-class"/> parse(InputStream in) {
      DefaultNativeParser parser = new DefaultNativeParser(in);
      <xsl:value-of select="$root/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$root/@param-name"/> = new <xsl:value-of select="entity[@root='true']/@entity-class"/>();

      try {
         <xsl:value-of select="$root/@param-name"/>.accept(parser);
      } catch (RuntimeException e) {
         if (e.getCause() !=null <xsl:value-of select="'&amp;&amp;'" disable-output-escaping="yes"/> e.getCause() instanceof java.io.EOFException) {
            // ignore it
         } else {
            throw e;
         }
      }
      
      parser.m_linker.finish();
      return <xsl:value-of select="$root/@param-name"/>;
   }
</xsl:template>

<xsl:template name="method-visit">
   <xsl:for-each select="entity">
      <xsl:sort select="@visit-method"/>

      <xsl:variable name="entity" select="."/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select="@visit-method"/>(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      byte tag;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      while ((tag = readTag()) != -1) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="@visit-method"/>Children(<xsl:value-of select="@param-name"/>, (tag <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0xFF) <xsl:value-of select="'&gt;&gt;'" disable-output-escaping="yes"/> 2, tag <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0x3);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   protected void <xsl:value-of select="@visit-method"/>Children(<xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>, int _field, int _type) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      switch (_field) {<xsl:value-of select="$empty-line"/>
      <xsl:variable name="indent" select="'            '"/>
      <xsl:for-each select="(attribute | element)[not(@render='false')]">
         <xsl:variable name="index" select="position()"/>
         
         <xsl:choose>
            <xsl:when test="@primitive='true'">
               <xsl:value-of select="$empty"/>         case <xsl:value-of select="$index"/>:<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            <xsl:value-of select="$indent"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@set-method"/>(read<xsl:call-template name="get-type-name"/>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="name()='element' and (@list='true' or @set='true')">
               <xsl:value-of select="$empty"/>         case <xsl:value-of select="$index"/>:<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            if (_type == 1) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               <xsl:value-of select="'                  '"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@add-method"/>(read<xsl:call-template name="get-type-name"><xsl:with-param name="value-type" select="@value-type-element"/></xsl:call-template>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            } else if (_type == 2) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               for (int i = readInt(); i <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/> 0; i--) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               <xsl:value-of select="'                  '"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@add-method"/>(read<xsl:call-template name="get-type-name"><xsl:with-param name="value-type" select="@value-type-element"/></xsl:call-template>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>         case <xsl:value-of select="$index"/>:<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            <xsl:value-of select="$indent"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@set-method"/>(read<xsl:call-template name="get-type-name"/>());<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>
      <xsl:for-each select="entity-ref[not(@render='false')]">
         <xsl:variable name="index" select="position() + 32"/>
         <xsl:variable name="name" select="@name"/>
         <xsl:variable name="current" select="//entity[@name=$name]"/>
         
         <xsl:choose>
            <xsl:when test="@list='true' or @map='true'">
               <xsl:value-of select="$empty"/>         case <xsl:value-of select="$index"/>:<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            if (_type == 1) {<xsl:value-of select="$empty-line"/>
               <xsl:call-template name="visit-child">
                  <xsl:with-param name="entity" select="$current"/>
                  <xsl:with-param name="parent-param-name" select="$entity/@param-name"/>
                  <xsl:with-param name="indent" select="'              '"/>
                  <xsl:with-param name="param-name" select="@param-name-element"/>
               </xsl:call-template>
               <xsl:value-of select="$empty"/>            } else if (_type == 2) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               for (int i = readInt(); i <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/> 0; i--) {<xsl:value-of select="$empty-line"/>
               <xsl:call-template name="visit-child">
                  <xsl:with-param name="entity" select="$current"/>
                  <xsl:with-param name="parent-param-name" select="$entity/@param-name"/>
                  <xsl:with-param name="indent" select="'                 '"/>
                  <xsl:with-param name="param-name" select="@param-name-element"/>
               </xsl:call-template>
               <xsl:value-of select="$empty"/>               }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>         case <xsl:value-of select="$index"/>:<xsl:value-of select="$empty-line"/>
               <xsl:call-template name="visit-child">
                  <xsl:with-param name="entity" select="$current"/>
                  <xsl:with-param name="parent-param-name" select="$entity/@param-name"/>
                  <xsl:with-param name="indent" select="'            '"/>
               </xsl:call-template>
               <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>
      <xsl:if test="@dynamic-attributes='true'">
         <xsl:value-of select="$empty"/>         case 63:<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttribute = <xsl:value-of select="$entity/@param-name"/>.getDynamicAttributes();<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            for (int i = readInt(); i <xsl:value-of select="'&gt;'" disable-output-escaping="yes"/> 0; i--) {<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>               String key = readString();<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>               String value = readString();<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>               dynamicAttribute.put(key, value);<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>            break;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="visit-child">
   <xsl:param name="entity"/>
   <xsl:param name="indent"/>
   <xsl:param name="parent-param-name"/>
   <xsl:param name="param-name" select="@param-name"/>
   
   <xsl:value-of select="$indent"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="$param-name"/> = new <xsl:value-of select="$entity/@entity-class"/>();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$indent"/><xsl:value-of select="$entity/@visit-method"/>(<xsl:value-of select="$param-name"/>);<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$indent"/>m_linker.<xsl:value-of select="@on-event-method"/>(<xsl:value-of select="$parent-param-name"/>, <xsl:value-of select="$param-name"/>);<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-read-methods">
   private boolean readBoolean() {
      try {
         return m_in.readByte() == 1 ? Boolean.TRUE : Boolean.FALSE;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private byte readByte() {
      try {
         return m_in.readByte();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private char readChar() {
      try {
         return (char) readVarint(16);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private java.util.Date readDate() {
      try {
         return new java.util.Date(readVarint(64));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private double readDouble() {
      try {
         return m_in.readDouble();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private float readFloat() {
      try {
         return m_in.readFloat();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private int readInt() {
      try {
         return (int) readVarint(32);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private long readLong() {
      try {
         return readVarint(64);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private short readShort() {
      try {
         return (short) readVarint(16);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private String readString() {
      try {
         return m_in.readUTF();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private byte readTag() {
      try {
         return m_in.readByte();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected long readVarint(final int length) throws IOException {
      int shift = 0;
      long result = 0;

      while (shift <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> length) {
         final byte b = m_in.readByte();
         result |= (long) (b <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0x7F) <xsl:value-of select="'&lt;&lt;'" disable-output-escaping="yes"/> shift;
         if ((b <xsl:value-of select="'&amp;'" disable-output-escaping="yes"/> 0x80) == 0) {
            return result;
         }
         shift += 7;
      }

      throw new RuntimeException("Malformed variable int " + length + "!");
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

</xsl:stylesheet>
