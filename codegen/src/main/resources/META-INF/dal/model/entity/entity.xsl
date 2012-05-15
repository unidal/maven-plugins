<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="package"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/model/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select="$empty"/>public class <xsl:value-of select='@entity-class'/> extends BaseEntity<xsl:value-of select="$empty"/>
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type" select="@entity-class"/>
   </xsl:call-template>
   <xsl:value-of select="$empty"/> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="declare-field-variables"/>
   <xsl:call-template name="constructor"/>
   <xsl:call-template name="method-accept"/>
   <xsl:call-template name="method-add-children"/>
   <xsl:call-template name="method-find-entity-refs"/>
   <xsl:call-template name="method-find-or-create-entity-refs"/>
   <!-- hack: for annotation only -->
   <xsl:call-template name="method-find-annotation"/>
   <xsl:call-template name="method-get-all-children-in-sequence"/>
   <xsl:call-template name="method-get-dynamic-attributes"/>
   <xsl:call-template name="method-get-dynamic-elements"/>
   <xsl:call-template name="method-get-fields"/>
   <xsl:call-template name="method-has-text"/>
   <xsl:call-template name="method-inc-fields"/>
   <!-- hack: for annotation only -->
   <xsl:call-template name="method-is-annotation-present"/>
   <xsl:call-template name="method-is-attributes"/>
   <xsl:if test="not(/model/@disable-merger='true')">
      <xsl:call-template name="method-merge-attributes"/>
   </xsl:if>
   <xsl:call-template name="method-remove-entity-refs"/>
   <xsl:call-template name="method-set-dynamic-attributes"/>
   <xsl:call-template name="method-set-dynamic-elements"/>
   <xsl:call-template name="method-set-fields"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:variable name="model-package" select="../@model-package"/>
   <xsl:if test="attribute[@key='true'] and not(/model/@disable-merger='true')">
      <xsl:for-each select="attribute[@key='true']">
         <xsl:value-of select="$empty"/>import static <xsl:value-of select="$model-package"/>.Constants.<xsl:value-of select='@upper-name'/>;<xsl:value-of select="$empty-line"/>
      </xsl:for-each>
      <xsl:value-of select="$empty"/>import static <xsl:value-of select="$model-package"/>.Constants.<xsl:value-of select='@upper-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element[@list='true' or @set='true'] or entity-ref[@list='true' or @map='true'] or @dynamic-attributes='true' or any">
      <xsl:if test="element[@list='true'] or entity-ref[@list='true'] or any">
         <xsl:value-of select="$empty"/>import java.util.ArrayList;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="entity-ref[@map='true'] or @dynamic-attributes='true'">
         <xsl:value-of select="$empty"/>import java.util.LinkedHashMap;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="element[@set='true']">
         <xsl:value-of select="$empty"/>import java.util.LinkedHashSet;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="element[@list='true'] or entity-ref[@list='true'] or any">
         <xsl:value-of select="$empty"/>import java.util.List;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="entity-ref[@map='true'] or @dynamic-attributes='true'">
         <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:if test="element[@set='true']">
         <xsl:value-of select="$empty"/>import java.util.Set;<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="$model-package"/>.BaseEntity;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>import <xsl:value-of select="$model-package"/>.IVisitor;<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="declare-field-variables">
   <xsl:if test="@all-children-in-sequence='true'">
      <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type-all-children-in-sequence' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-all-children-in-sequence'/> = new Array<xsl:value-of select='@value-type-all-children-in-sequence' disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:for-each select="attribute | element | entity-ref">
      <xsl:choose>
         <xsl:when test="@map='true'">
            <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-name'/> = new LinkedHashMap<xsl:value-of select='@value-type-generic' disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@set='true'">
            <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-name'/> = new LinkedHashSet<xsl:value-of select='@value-type-generic' disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@list='true'">
            <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-name'/> = new ArrayList<xsl:value-of select='@value-type-generic' disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@default-value">
            <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-name'/> = <xsl:call-template name="field-default-value"/>;<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>   private Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> m_dynamicAttributes = new LinkedHashMap<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="any">
      <xsl:value-of select="$empty"/>   private <xsl:value-of select="any/@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='any/@field-name'/> = new ArrayList<xsl:value-of select="any/@value-type-generic" disable-output-escaping="yes"/>();<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="constructor">
   <xsl:value-of select="$empty"/>   public <xsl:value-of select='@entity-class'/>(<xsl:value-of select="$empty"/>
   <xsl:for-each select="attribute[@key='true'] | element[@key='true']">
      <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/><xsl:value-of select="$empty"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="attribute[@key='true'] | element[@key='true']">
      <xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/> = <xsl:value-of select='@param-name'/>;<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-accept">
   <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public void accept(IVisitor visitor) {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      visitor.<xsl:value-of select="@visit-method"/>(this);<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-add-children">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="entity-ref[@list='true' or @map='true'] | element[@list='true' or @set='true']">
      <xsl:sort select="@add-method"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select='$entity/@entity-class'/><xsl:value-of select="$space"/><xsl:value-of select='@add-method'/>(<xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name-element'/>) {<xsl:value-of select="$empty-line"/>
      <xsl:if test="$entity/@all-children-in-sequence='true'">
	     	<xsl:value-of select="'      '"/><xsl:value-of select='$entity/@field-all-children-in-sequence'/>.add(<xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
      </xsl:if>
      <xsl:choose>
         <xsl:when test="@map='true'">
            <xsl:variable name="name" select="@name"/>
   			<xsl:variable name="key" select="//entity[@name=$name]/node()[name()='attribute' or name()='element'][@key]"/>
   	     	<xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/>.put(<xsl:value-of select='@param-name-element'/>.<xsl:value-of select="$key/@get-method"/>(), <xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:when test="@set='true'">
   	     	<xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/>.add(<xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
   		 </xsl:when>
         <xsl:when test="@list='true'">
   	     	<xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/>.add(<xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
   		 </xsl:when>
         <xsl:otherwise>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>      return this;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-find-entity-refs">
   <xsl:for-each select="entity-ref[@list='true' or @map='true']">
      <xsl:sort select="@find-method"/>
      
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>
      <xsl:if test="($entity/attribute | $entity/element)[@key='true']">
         <xsl:value-of select="$empty"/>   public <xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@find-method'/>(<xsl:value-of select="$empty"/>
         <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
            <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:choose>
            <xsl:when test="@map='true'">
      	     	<xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/>.get(<xsl:value-of select="($entity/attribute | $entity/element)[@key='true'][1]/@param-name"/>);<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>      for (<xsl:value-of select='@value-type-element'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name-element'/> : <xsl:value-of select='@field-name'/>) {<xsl:value-of select="$empty-line"/>
               <xsl:variable name="current" select="." />
               <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
                  <xsl:choose>
                     <xsl:when test="@nullable='true'">
                        <xsl:value-of select="$empty"/>         if (<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>() == null) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            if (<xsl:value-of select="@param-name"/> != null) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         } else if (!<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>().equals(<xsl:value-of select="@param-name"/>)) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:when test="@primitive='true'">
                        <xsl:value-of select="$empty"/>         if (<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>() != <xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$empty"/>         if (!<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>().equals(<xsl:value-of select="@param-name"/>)) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>         return <xsl:value-of select='$current/@param-name-element'/>;<xsl:value-of select="$empty-line"/>
               </xsl:for-each>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      return null;<xsl:value-of select="$empty-line"/>
   		    </xsl:when>
         </xsl:choose>
         <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-find-or-create-entity-refs">
   <xsl:for-each select="entity-ref[@find-or-create-method]">
      <xsl:sort select="@find-or-create-method"/>
      
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>
      <xsl:if test="($entity/attribute | $entity/element)[@key='true']">
         <xsl:value-of select="$empty"/>   public <xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@find-or-create-method'/>(<xsl:value-of select="$empty"/>
         <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
            <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:variable name="key" select="($entity/attribute | $entity/element)[@key='true'][1]"/>
         <xsl:choose>
            <xsl:when test="@map='true'">
               <xsl:value-of select="$empty"/>      <xsl:value-of select="'      '"/><xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@local-name-element"/> = <xsl:value-of select='@field-name'/>.get(<xsl:value-of select="$key/@param-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      if (<xsl:value-of select="@local-name-element"/> == null) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         synchronized (<xsl:value-of select="@field-name"/>) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            <xsl:value-of select="'            '"/><xsl:value-of select="@local-name-element"/> = <xsl:value-of select='@field-name'/>.get(<xsl:value-of select="$key/@param-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            if (<xsl:value-of select="@local-name-element"/> == null) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               <xsl:value-of select="'               '"/><xsl:value-of select="@local-name-element"/> = new <xsl:value-of select='@value-type-element' disable-output-escaping="yes"/>(<xsl:value-of select="$key/@param-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>               <xsl:value-of select="'               '"/><xsl:value-of select='@field-name'/>.put(<xsl:value-of select="$key/@param-name"/>, <xsl:value-of select="@local-name-element"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      return <xsl:value-of select="@local-name-element"/>;<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>      synchronized (<xsl:value-of select="@field-name"/>) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         for (<xsl:value-of select='@value-type-element'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name-element'/> : <xsl:value-of select='@field-name'/>) {<xsl:value-of select="$empty-line"/>
               <xsl:variable name="current" select="." />
               <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
                  <xsl:choose>
                     <xsl:when test="@nullable='true'">
                        <xsl:value-of select="$empty"/>            if (<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>() == null) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               if (<xsl:value-of select="@param-name"/> != null) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>                  continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               }<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            } else if (!<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>().equals(<xsl:value-of select="@param-name"/>)) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:when test="@primitive='true'">
                        <xsl:value-of select="$empty"/>            if (<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>() != <xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$empty"/>            if (!<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>().equals(<xsl:value-of select="@param-name"/>)) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>               continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            }<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="$empty-line"/>
                  <xsl:value-of select="$empty"/>            return <xsl:value-of select='$current/@param-name-element'/>;<xsl:value-of select="$empty-line"/>
               </xsl:for-each>
               <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='$current/@param-name-element'/> = new <xsl:value-of select='@value-type-element' disable-output-escaping="yes"/>(<xsl:value-of select='$key/@param-name'/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/>.add(<xsl:value-of select='$current/@param-name-element'/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         return <xsl:value-of select='$current/@param-name-element'/>;<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
             </xsl:when>
         </xsl:choose>
         <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
      </xsl:if>      
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-find-annotation">
   <xsl:if test="element[@is-annotation='true']">
      <xsl:value-of select="$empty"/>   @SuppressWarnings("unchecked")<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public <xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T extends java.lang.annotation.Annotation'"/></xsl:call-template> T findAnnotation(Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> clazz) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      for (java.lang.annotation.Annotation annotation : m_annotations) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         if (annotation.annotationType() == clazz) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>            return (T) annotation;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return null;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-get-all-children-in-sequence">
   <xsl:if test="@all-children-in-sequence='true'">
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type-all-children-in-sequence" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@method-get-all-children-in-sequence'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-all-children-in-sequence'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-get-dynamic-attributes">
   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>   public String getDynamicAttribute(String name) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return m_dynamicAttributes.get(name);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   public Map<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> getDynamicAttributes() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return m_dynamicAttributes;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-get-dynamic-elements">
   <xsl:if test="any">
      <xsl:value-of select="$empty"/>   public <xsl:value-of select='any/@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='any/@get-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='any/@field-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-get-fields">
   <xsl:for-each select="attribute | element | entity-ref">
      <xsl:sort select="@get-method"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-has-text">
   <xsl:for-each select="attribute[@text='true']">
      <xsl:value-of select="$empty"/>   public boolean hasText() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/> != null;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-is-annotation-present">
   <xsl:if test="element[@is-annotation='true']">
      <xsl:value-of select="$empty"/>   public boolean isAnnotationPresent(Class<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'? extends java.lang.annotation.Annotation'"/></xsl:call-template> clazz) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return findAnnotation(clazz) != null;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-is-attributes">
   <xsl:for-each select="(attribute | element)[@value-type='boolean' or @value-type='Boolean']">
      <xsl:sort select="@is-method"/>
      
      <xsl:value-of select="$empty"/>   public boolean <xsl:value-of select='@is-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@value-type='boolean'">
            <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/> != null <xsl:value-of select="'&amp;&amp; '" disable-output-escaping="yes"/><xsl:value-of select='@field-name'/>.booleanValue();<xsl:value-of select="$empty-line"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-merge-attributes">
   <xsl:variable name="entity" select="." />
   <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public void mergeAttributes(<xsl:value-of select='$entity/@entity-class'/> other) {<xsl:value-of select="$empty-line"/>

   <xsl:if test="$entity/attribute[@key='true']">
      <xsl:value-of select="$empty"/>      assertAttributeEquals(other, <xsl:value-of select='$entity/@upper-name'/>
      <xsl:for-each select="$entity/attribute[@key='true']">
         <xsl:value-of select="$empty"/>, <xsl:value-of select='@upper-name'/>, <xsl:value-of select='@field-name'/>, other.<xsl:value-of select='@get-method'/>()<xsl:value-of select="$empty"/>
      </xsl:for-each>
      <xsl:value-of select="$empty"/>);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>

   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>      for (Map.Entry<xsl:value-of select="'&lt;String, String&gt;'" disable-output-escaping="yes"/> e : other.getDynamicAttributes().entrySet()) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>         m_dynamicAttributes.put(e.getKey(), e.getValue());<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>

   <xsl:for-each select="$entity/attribute[not(@key='true')]">
      <xsl:choose>
      	<!-- TODO @default-value -->
      	<xsl:when test="not(@primitive='true')">
	      <xsl:value-of select="$empty"/>      if (other.<xsl:value-of select='@get-method'/>() != null) {<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/><xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/> = other.<xsl:value-of select='@get-method'/>();<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      	</xsl:when>
        <xsl:when test="@value-type='boolean'">
	      <xsl:value-of select="$empty"/>      if (other.<xsl:value-of select='@get-method'/>()) {<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/><xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/> = other.<xsl:value-of select='@get-method'/>();<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
        </xsl:when>
        <xsl:when test="@value-type='float' or @value-type='double'">
	      <xsl:value-of select="$empty"/>      if (other.<xsl:value-of select='@get-method'/>() - 1e6 <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> 0) {<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/><xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/> = other.<xsl:value-of select='@get-method'/>();<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
        </xsl:when>
      	<xsl:otherwise>
	      <xsl:value-of select="$empty"/>      if (other.<xsl:value-of select='@get-method'/>() != 0) {<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/><xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/> = other.<xsl:value-of select='@get-method'/>();<xsl:value-of select="$empty-line"/>
	      <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
      	</xsl:otherwise>
      </xsl:choose>
      <xsl:if test="position()!=last()">
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="method-remove-entity-refs">
   <xsl:for-each select="entity-ref[@list='true' or @map='true']">
      <xsl:sort select="@remove-method"/>
      
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="entity" select="//entity[@name=$name]"/>
      <xsl:if test="($entity/attribute | $entity/element)[@key='true']">
         <xsl:value-of select="$empty"/>   public boolean <xsl:value-of select='@remove-method'/>(<xsl:value-of select="$empty"/>
         <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
            <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>
         </xsl:for-each>
         <xsl:value-of select="$empty"/>) {<xsl:value-of select="$empty-line"/>
         <xsl:choose>
            <xsl:when test="@map='true'">
               <xsl:value-of select="$empty"/>      if (<xsl:value-of select='@field-name'/>.containsKey(<xsl:value-of select="($entity/attribute | $entity/element)[@key='true'][1]/@param-name"/>)) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/>.remove(<xsl:value-of select="($entity/attribute | $entity/element)[@key='true'][1]/@param-name"/>);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         return true;<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@list='true'">
               <xsl:value-of select="$empty"/>      int len = <xsl:value-of select='@field-name'/>.size();<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      for (int i = 0; i <xsl:value-of select="'&lt;'" disable-output-escaping="yes"/> len; i++) {<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select='@value-type-element'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name-element'/> = <xsl:value-of select='@field-name'/>.get(i);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty-line"/>
               <xsl:variable name="current" select="." />
               <xsl:for-each select="($entity/attribute | $entity/element)[@key='true']">
                  <xsl:choose>
                     <xsl:when test="@primitive='true'">
                        <xsl:value-of select="$empty"/>         if (<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>() != <xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                     </xsl:when>
                     <xsl:otherwise>
                        <xsl:value-of select="$empty"/>         if (!<xsl:value-of select='$current/@param-name-element'/>.<xsl:value-of select="@get-method"/>().equals(<xsl:value-of select="@param-name"/>)) {<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>            continue;<xsl:value-of select="$empty-line"/>
                        <xsl:value-of select="$empty"/>         }<xsl:value-of select="$empty-line"/>
                     </xsl:otherwise>
                  </xsl:choose>
                  <xsl:value-of select="$empty-line"/>
               </xsl:for-each>
               <xsl:value-of select="$empty"/>         <xsl:value-of select="'         '"/><xsl:value-of select='@field-name'/>.remove(i);<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>         return true;<xsl:value-of select="$empty-line"/>
               <xsl:value-of select="$empty"/>      }<xsl:value-of select="$empty-line"/>
            </xsl:when>
         </xsl:choose>
         <xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>      return false;<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
         <xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-set-dynamic-attributes">
   <xsl:if test="@dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>   public void setDynamicAttribute(String name, String value) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      m_dynamicAttributes.put(name, value);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-set-dynamic-elements">
   <xsl:if test="any">
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select='any/@set-method'/>(<xsl:value-of select='any/@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='any/@param-name'/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      <xsl:value-of select="'      '"/><xsl:value-of select='any/@field-name'/> = <xsl:value-of select='any/@param-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-set-fields">
   <xsl:variable name="entity" select="." />
   <xsl:for-each select="attribute[not(@readonly='true')] | element[not(@readonly='true' or @list='true' or @set='true')] | entity-ref[not(@list='true' or @map='true')]">
      <xsl:sort select="@set-method"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select='$entity/@entity-class'/><xsl:value-of select='$space'/><xsl:value-of select='@set-method'/>(<xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      <xsl:value-of select="'      '"/><xsl:value-of select="@field-name"/> = <xsl:value-of select="@param-name"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return this;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-inc-fields">
   <xsl:variable name="entity" select="." />
   <xsl:for-each select="(attribute | element)[@inc-method]">
      <xsl:sort select="@inc-method"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select='$entity/@entity-class'/><xsl:value-of select='$space'/><xsl:value-of select='@inc-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      <xsl:value-of select="'      '"/><xsl:value-of select="@field-name"/>++;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return this;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="field-default-value">
	<xsl:choose>
      <xsl:when test="@value-type = 'String'">"<xsl:value-of select="@default-value"/>"</xsl:when>
      <xsl:when test="@value-type = 'Time'"><xsl:value-of select="@default-value"/>L</xsl:when>
      <xsl:when test="@value-type = 'boolean'"><xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'Boolean'"><xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'byte'">(byte)<xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'Byte'">(byte)<xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'char'">'<xsl:value-of select="@default-value"/>'</xsl:when>
      <xsl:when test="@value-type = 'Character'">'<xsl:value-of select="@default-value"/>'</xsl:when>
      <xsl:when test="@value-type = 'short'">(short)<xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'Short'">(short)<xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'int'"><xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'Integer'"><xsl:value-of select="@default-value"/></xsl:when>
      <xsl:when test="@value-type = 'long'"><xsl:value-of select="@default-value"/>L</xsl:when>
      <xsl:when test="@value-type = 'Long'"><xsl:value-of select="@default-value"/>L</xsl:when>
      <xsl:when test="@value-type = 'float'"><xsl:value-of select="@default-value"/>f</xsl:when>
      <xsl:when test="@value-type = 'Float'"><xsl:value-of select="@default-value"/>f</xsl:when>
      <xsl:when test="@value-type = 'double'"><xsl:value-of select="@default-value"/>d</xsl:when>
      <xsl:when test="@value-type = 'Double'"><xsl:value-of select="@default-value"/>d</xsl:when>
      <xsl:when test="@value-type = 'java.util.Date'">toDate("<xsl:value-of select="@format"/>", "<xsl:value-of select="@default-value"/>")</xsl:when>
      <xsl:otherwise><xsl:value-of select="@default-value"/></xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
