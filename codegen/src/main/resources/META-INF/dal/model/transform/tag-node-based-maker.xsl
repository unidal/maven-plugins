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
   <xsl:value-of select="$empty"/>public class TagNodeBasedMaker implements IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'ITagNode'"/></xsl:call-template> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-build-children'/>
   <xsl:call-template name='method-get-text'/>
   <xsl:call-template name='method-to-class'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="entity/attribute[not(@text='true' or @render='false')]">
      <xsl:for-each select="entity/attribute[not(@text='true' or @render='false')]">
         <xsl:sort select="@upper-name"/>
   
         <xsl:variable name="name" select="@name"/>
         <xsl:if test="generate-id(//entity/attribute[@name=$name][1])=generate-id()">
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="entity/element[not(@list='true' or @render='false')][not(@text='true')]">
      <xsl:for-each select="entity/element[not(@list='true' or @render='false')][not(@text='true')]">
         <xsl:sort select="@upper-name"/>
   
         <xsl:variable name="name" select="@name"/>
         <xsl:if test="generate-id(//entity/element[@name=$name][1])=generate-id()">
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="@upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="entity[@dynamic-attributes='true']">
      <xsl:value-of select="$empty"/>import java.util.LinkedHashMap;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="//entity/attribute[@text='true'] | entity/element[not(@render='false')]">
      <xsl:value-of select="$empty"/>import com.ebay.webres.dom.INode;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import com.ebay.webres.dom.ITagNode;<xsl:value-of select="$empty-line"/>
   <xsl:if test="//entity/attribute[@text='true'] | entity/element[not(@render='false')]">
      <xsl:value-of select="$empty"/>import com.ebay.webres.dom.NodeType;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import com.ebay.webres.dom.TextNode;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity">
      <xsl:sort select="@entity-class"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-build-children">
   <xsl:for-each select="entity | entity/element[@list='true' and not(@render='false')]">
      <xsl:sort select="@build-method"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="name()='element'">
            <xsl:variable name="build-method" select="@build-method"/>
            
            <xsl:if test="generate-id(//entity/element[@build-method=$build-method][@list='true' and not(@render='false')][1])=generate-id()">
               <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(ITagNode node) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      return <xsl:value-of select="$empty"/>
               <xsl:call-template name="convert-type">
                  <xsl:with-param name="value-type" select="@value-type-element"/>
                  <xsl:with-param name="value" select="'getText(node)'"/>
               </xsl:call-template>
               <xsl:value-of select="$empty"/>;<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@build-method"/>(ITagNode node) {<xsl:value-of select="$empty-line"/>
            <xsl:call-template name="define-variable-from-attributes"/>
            <xsl:value-of select="'      '"/><xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> = <xsl:call-template name="create-entity-instance"/>
            <xsl:call-template name="set-optional-fields"/>
            <xsl:call-template name="set-dynamic-attributes"/>
            <xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      return <xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-get-text">
<xsl:if test="//entity/attribute[@text='true'] | //entity/element[not(@render='false')]">
   private String getText(ITagNode node) {
      if (node != null) {
         StringBuilder sb = new StringBuilder();

         for (INode child : node.getChildNodes()) {
            if (child.getNodeType() == NodeType.TEXT) {
               sb.append(((TextNode) child).getNodeValue());
            }
         }

         if (sb.length() != 0) {
            return sb.toString();
         }
      }
      
      return null;
   }
</xsl:if>
</xsl:template>

<xsl:template name="define-variable-from-attributes">
   <xsl:if test="(attribute | element)[not(@text='true' or @list='true' or @render='false')]">
      <xsl:for-each select="(attribute | element)[not(@text='true' or @list='true' or @render='false')]">
         <xsl:choose>
            <xsl:when test="name()='attribute'">
               <xsl:value-of select="$empty"/>      String <xsl:value-of select="@param-name"/> = node.getAttribute(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:value-of select="$empty"/>      String <xsl:value-of select="@param-name"/> = getText(node.getChildTagNode(<xsl:value-of select="@upper-name"/>));<xsl:value-of select="$empty-line"/>
            </xsl:otherwise>
         </xsl:choose>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="set-dynamic-attributes">
   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> dynamicAttributes = new LinkedHashMap<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/>(node.getAttributes());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:if test="attribute[not(@text='true' or @render='false')]">
         <xsl:for-each select="attribute[not(@text='true' or @render='false')]">
            <xsl:value-of select="$empty"/>      dynamicAttributes.remove(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
         </xsl:for-each>
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty"/>      for (Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> e : dynamicAttributes.entrySet()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="@param-name"/>.setDynamicAttribute(e.getKey(), e.getValue());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      
   </xsl:if>
</xsl:template>

<xsl:template name="set-optional-fields">
   <xsl:param name="entity" select="."/>
   
   <xsl:for-each select="(attribute | element)[not(@key='true' or @render='false' or @list='true')]">
      <xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@text='true'">
            <xsl:value-of select="'      '"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@set-method"/>(<xsl:value-of select="$empty"/>
            <xsl:call-template name="convert-type">
               <xsl:with-param name="value" select="'getText(node)'"/>
            </xsl:call-template>
            <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      if (<xsl:value-of select="@param-name"/> != null) {<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="$entity/@param-name"/>.<xsl:value-of select="@set-method"/>(<xsl:value-of select="$empty"/>
            <xsl:call-template name="convert-type"/>
            <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
            <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:for-each>
</xsl:template>

<xsl:template name="convert-type">
   <xsl:param name="value-type" select="@value-type"/>
   <xsl:param name="enum-value-type" select="@enum-value-type"/>
   <xsl:param name="value" select="@param-name"/>
   
   <xsl:choose>
      <xsl:when test="$enum-value-type='true'"><xsl:value-of select="$value-type"/>.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='String'"><xsl:value-of select="$value"/></xsl:when>
      <xsl:when test="$value-type='boolean'">Boolean.parseBoolean(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Boolean'">Boolean.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='int'">Integer.parseInt(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Integer'">Integer.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='long'">Long.parseLong(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Long'">Long.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='short'">Short.parseShort(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Short'">Short.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='float'">Float.parseFloat(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Float'">Float.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='double'">Double.parseDouble(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Double'">Double.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='byte'">Byte.parseByte(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Byte'">Byte.valueOf(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:when test="$value-type='Class&lt;?&gt;'">toClass(<xsl:value-of select="$value"/>)</xsl:when>
      <xsl:otherwise><xsl:value-of select="$value"/></xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="create-entity-instance">
   <xsl:value-of select="$empty"/>new <xsl:value-of select="@entity-class"/>(<xsl:value-of select="$empty"/>
   <xsl:for-each select="(attribute | element)[@key='true']">
      <xsl:call-template name="convert-type"/>
      <xsl:if test="position()!=last()">, </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-to-class">
<xsl:if test="//entity/attribute[@value-type='Class&lt;?&gt;'] | //entity/element[@value-type-element='Class&lt;?&gt;']">
   private Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'?'"/></xsl:call-template> toClass(String className) {
      try {
         return Class.forName(className);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }
</xsl:if>
</xsl:template>

</xsl:stylesheet>
