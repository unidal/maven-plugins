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
   <xsl:value-of select="$empty"/>public class TagNodeBasedParser implements IParser<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'ITagNode'"/></xsl:call-template> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name='method-parse'/>
   <xsl:call-template name='method-parse-children'/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="entity/element[@list='true' and not(@render='false')]">
      <xsl:for-each select="entity/element[@list='true' and not(@render='false')]">
         <xsl:sort select="@upper-name"/>
   
         <xsl:variable name="upper-name" select="@upper-name"/>
         <xsl:if test="generate-id(//entity/element[@upper-name=$upper-name][1])=generate-id()">
            <xsl:variable name="upper-name">
               <xsl:choose>
                  <xsl:when test="@xml-indent='true'">
                     <xsl:value-of select="@upper-name"/>
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="@upper-name-element"/>
                  </xsl:otherwise>
               </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="$upper-name"/>;<xsl:value-of select="$empty-line"/>
         </xsl:if>
      </xsl:for-each>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="entity/entity-ref[not(@render='false')] | entity[@root='true']">
      <xsl:sort select="@upper-name"/>

      <xsl:variable name="upper-name" select="@upper-name"/>
      <xsl:if test="generate-id(//entity/entity-ref[@upper-name=$upper-name][1])=generate-id() or @root='true'">
         <xsl:variable name="upper-name">
            <xsl:choose>
               <xsl:when test="@xml-indent='true'">
                  <xsl:value-of select="@upper-name"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:variable name="name" select="@name"/>
                  <xsl:value-of select="//entity[@name=$name]/@upper-name"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:variable>
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="/model/@model-package"/>.Constants.<xsl:value-of select="$upper-name"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import java.io.IOException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import org.xml.sax.SAXException;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import com.ebay.webres.dom.ITagNode;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import com.ebay.webres.tag.core.TagXmlParser;<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="entity">
      <xsl:sort select="@name"/>

      <xsl:value-of select="$empty"/>import <xsl:value-of select="@entity-package"/>.<xsl:value-of select='@entity-class'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-parse">
   <xsl:variable name="current" select="."/>
   <xsl:for-each select="entity[@root='true']">
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/> parse(ITagNode node) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return parse(new TagNodeBasedMaker(), new DefaultLinker(), node);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/> parse(String xml) throws SAXException, IOException {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      ITagNode doc = new TagXmlParser().parse(xml);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      ITagNode rootNode = doc.getChildTagNode(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (rootNode == null) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         throw new RuntimeException(String.format("<xsl:value-of select="@tag-name"/> element(%s) is expected!", <xsl:value-of select="@upper-name"/>));<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return new TagNodeBasedParser().parse(new TagNodeBasedMaker(), new DefaultLinker(), rootNode);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-parse-children">
   <xsl:for-each select="entity[@root='true']">
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@entity-class"/> parse(IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'ITagNode'"/></xsl:call-template> maker, ILinker linker, ITagNode node) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      <xsl:value-of select="'      '"/><xsl:value-of select="@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> = maker.<xsl:value-of select="@build-method"/>(node);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      if (node != null) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select="@entity-class"/> parent = <xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:call-template name="parse-children">
         <xsl:with-param name="indent" select="'         '"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:for-each select="entity[not(@root='true')]">
      <xsl:sort select="@build-method"/>

      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public void parseFor<xsl:value-of select="@entity-class"/>(IMaker<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'ITagNode'"/></xsl:call-template> maker, ILinker linker, <xsl:value-of select="@entity-class"/> parent, ITagNode node) {<xsl:value-of select="$empty-line"/>
      <xsl:call-template name="parse-children">
         <xsl:with-param name="indent" select="'      '"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="parse-children">
   <xsl:param name="indent"/>
   
   <xsl:variable name="current" select="."/>
   <xsl:for-each select="element[@list='true' and not(@render='false')]">
      <xsl:value-of select="$indent"/>for (ITagNode child : node.<xsl:value-of select="$empty"/>
      <xsl:choose>
         <xsl:when test="@xml-indent='true'">getGrandchildTagNodes(<xsl:value-of select="@upper-name"/>)</xsl:when>
         <xsl:otherwise>getChildTagNodes(<xsl:value-of select="@upper-name-element"/>)</xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>   <xsl:value-of select="'   '"/><xsl:value-of select="@value-type-element"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> = maker.<xsl:value-of select="@build-method"/>(child);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>   parent.<xsl:value-of select="@add-method"/>(<xsl:value-of select="@param-name-element"/>);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
      <xsl:if test="position()!=last()">
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:if test="element[@list='true' and not(@render='false')] and entity-ref">
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:choose>
      <xsl:when test="@all-children-in-sequence='true'">
         <xsl:value-of select="$indent"/>for (ITagNode child : node.getChildTagNodes()) {<xsl:value-of select="$empty-line"/>
         <xsl:for-each select="entity-ref[not(@render='false')]">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="entity" select="//entity[@name=$name]"/>
            <xsl:choose>
               <xsl:when test="position()=1"><xsl:value-of select="$indent"/><xsl:value-of select="'   '"/></xsl:when>
               <xsl:otherwise> else </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
               <xsl:when test="@list='true' or @map='true'">
                  <xsl:value-of select="$empty"/>if (child.getNodeName().equals(<xsl:value-of select="$entity/@upper-name"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   <xsl:value-of select="'      '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> = maker.<xsl:value-of select="$entity/@build-method"/>(child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      if (linker.<xsl:value-of select="@on-event-method"/>(parent, <xsl:value-of select="@param-name-element"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>         parseFor<xsl:value-of select="$entity/@entity-class"/>(maker, linker, <xsl:value-of select="@param-name-element"/>, child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="$empty"/>if (child.getNodeName().equals(<xsl:value-of select="$entity/@upper-name"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   <xsl:value-of select="'      '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> = maker.<xsl:value-of select="$entity/@build-method"/>(child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      if (linker.<xsl:value-of select="@on-event-method"/>(parent, <xsl:value-of select="@param-name-element"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>         parseFor<xsl:value-of select="$entity/@entity-class"/>(maker, linker, <xsl:value-of select="@param-name-element"/>, child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty"/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
         <xsl:value-of select="$empty-line"/><xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:for-each select="entity-ref[not(@render='false')]">
            <xsl:variable name="name" select="@name"/>
            <xsl:variable name="entity" select="//entity[@name=$name]"/>
            <xsl:choose>
               <xsl:when test="@list='true' or @map='true'">
                  <xsl:value-of select="$indent"/>for (ITagNode child : node.<xsl:value-of select="$empty"/>
                  <xsl:choose>
                     <xsl:when test="@xml-indent='true'">getGrandchildTagNodes(<xsl:value-of select="@upper-name"/>)</xsl:when>
                     <xsl:otherwise>getChildTagNodes(<xsl:value-of select="$entity/@upper-name"/>)</xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   <xsl:value-of select="'   '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> = maker.<xsl:value-of select="$entity/@build-method"/>(child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   if (linker.<xsl:value-of select="@on-event-method"/>(parent, <xsl:value-of select="@param-name-element"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      parseFor<xsl:value-of select="$entity/@entity-class"/>(maker, linker, <xsl:value-of select="@param-name-element"/>, child);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select="$indent"/>ITagNode <xsl:value-of select="@param-name"/>Node = node.getChildTagNode(<xsl:value-of select="@upper-name"/>);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>if (<xsl:value-of select="@param-name"/>Node != null) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   <xsl:value-of select="'   '"/><xsl:value-of select="$entity/@entity-class"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name-element"/> = maker.<xsl:value-of select="$entity/@build-method"/>(<xsl:value-of select="@param-name"/>Node);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   if (linker.<xsl:value-of select="@on-event-method"/>(parent, <xsl:value-of select="@param-name-element"/>)) {<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>      parseFor<xsl:value-of select="$entity/@entity-class"/>(maker, linker, <xsl:value-of select="@param-name-element"/>, <xsl:value-of select="@param-name"/>Node);<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>   }<xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$indent"/>}<xsl:value-of select="$empty-line"/>
               </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position()!=last()">
               <xsl:value-of select="$empty-line"/>
            </xsl:if>
         </xsl:for-each>
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>
