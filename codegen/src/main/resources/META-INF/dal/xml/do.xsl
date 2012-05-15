<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl"/>
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/root/element[@name = $name]"/>
</xsl:template>

<xsl:template match="element">
   <xsl:variable name="package" select="@do-package"/>
   <xsl:if test="$package">
      <xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:call-template name='import-list'/>
   <xsl:call-template name='declare-class-annotation'/>
   <xsl:value-of select="$empty"/>public class <xsl:value-of select='@do-class'/><xsl:if test="@has-dynamic-attributes"> implements DynamicAttributes</xsl:if> {<xsl:value-of select="$empty-line"/>
   <xsl:call-template name="declare-field-variables"/>
   <xsl:call-template name="method-add-fields"/>
   <xsl:call-template name="method-get-fields"/>
   <xsl:call-template name="method-get-dynamic-attributes"/>
   <xsl:call-template name="method-set-fields"/>
   <xsl:call-template name="method-set-dynamic-attribute"/>
   <xsl:call-template name="method-to-string"/>
   <xsl:value-of select="$empty"/>}<xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="element[@list='true' and @list-type='list'] or element-ref[@list='true' and @list-type='list']">
      <xsl:value-of select="$empty"/>import java.util.ArrayList;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element-ref[@list='true' and @list-type='map']">
      <xsl:value-of select="$empty"/>import java.util.HashMap;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element[@list='true' and @list-type='list'] or element-ref[@list='true' and @list-type='list']">
      <xsl:value-of select="$empty"/>import java.util.List;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element-ref[@list='true' and @list-type='map']">
      <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="@has-dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>import java.util.HashMap;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>import java.util.Map;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty-line"/>
   <xsl:if test="attribute">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlAttribute;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="not(@root='true') or element[not(@value='true')] or element-ref">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlElement;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element[@list='true' and @list-name] or element-ref[@list='true' and @list-name]">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlElementWrapper;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="@root='true'">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlRootElement;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element[@list='true' and not(@list-name)] or element-ref[@list='true' and not(@list-name)]">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlElements;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="element[@value='true']">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.annotation.XmlValue;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:if test="@has-dynamic-attributes='true'">
      <xsl:value-of select="$empty"/>import com.site.dal.xml.dynamic.DynamicAttributes;<xsl:value-of select="$empty-line"/>
   </xsl:if>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="declare-class-annotation">
   <xsl:choose>
     <xsl:when test="@root='true' and @root-name">
        <xsl:value-of select="$empty"/>@XmlRootElement(name = "<xsl:value-of select='@root-name'/>")<xsl:value-of select="$empty-line"/>
     </xsl:when>
     <xsl:when test="@root='true'">
        <xsl:value-of select="$empty"/>@XmlRootElement(name = "<xsl:value-of select='@name'/>")<xsl:value-of select="$empty-line"/>
     </xsl:when>
     <xsl:otherwise>
        <xsl:value-of select="$empty"/>@XmlElement(name = "<xsl:value-of select='@name'/>")<xsl:value-of select="$empty-line"/>
     </xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="declare-field-variables">
   <xsl:for-each select="attribute | element | element-ref">
      <xsl:call-template name="declare-field-annotation"/>
      <xsl:value-of select="$empty"/>   private <xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/>
      <xsl:value-of select='@field-name'/>
      <xsl:choose>
         <xsl:when test="@list='true' and @list-type='list'"> = new ArrayList<xsl:value-of select='@value-type-generic' disable-output-escaping="yes"/>()</xsl:when>
         <xsl:when test="@list='true' and @list-type='map'"> = new HashMap<xsl:value-of select='@value-type-generic' disable-output-escaping="yes"/>()</xsl:when>
         <xsl:when test="@list='true'"> = new <xsl:value-of select='@value-type-element'/>[0]</xsl:when>
      </xsl:choose>
      <xsl:value-of select="$empty"/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:if test="@has-dynamic-attributes">
      <xsl:value-of select="$empty"/>   private Map<xsl:call-template name="generic-string-string-type"/> m_dynamicAttributes = new HashMap<xsl:call-template name="generic-string-string-type"/>();<xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="declare-field-annotation">
   <xsl:choose>
     <xsl:when test="name()='attribute'">
        <xsl:value-of select="$empty"/>   @XmlAttribute(name = "<xsl:value-of select='@name'/>"<xsl:value-of select="$empty"/>
        <xsl:if test="@format">, format = "<xsl:value-of select='@format'/>"</xsl:if>
        <xsl:value-of select="$empty"/>)<xsl:value-of select="$empty-line"/>
     </xsl:when>
     <xsl:when test="name()='element'">
		<xsl:choose>
			<xsl:when test="@value='true'">
				<xsl:value-of select="$empty"/>   @XmlValue<xsl:value-of select="$empty-line"/>
			</xsl:when>
            <xsl:when test="@list='true' and @list-name">
                <xsl:value-of select="$empty"/>   @XmlElementWrapper(name = "<xsl:value-of select='@list-name'/>")<xsl:value-of select="$empty-line"/>
                <xsl:value-of select="$empty"/>   @XmlElement(name = "<xsl:value-of select='@name'/>")<xsl:value-of select="$empty-line"/>
            </xsl:when>
            <xsl:when test="@list='true' and not(@list-name)">
              <xsl:value-of select="$empty"/>   @XmlElements(@XmlElement(name = "<xsl:value-of select='@name'/>", type = <xsl:value-of select='@value-type-element'/>.class))<xsl:value-of select="$empty-line"/>
            </xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$empty"/>   @XmlElement(name = "<xsl:value-of select='@name'/>"<xsl:value-of select="$empty"/>
				<xsl:if test="@format">, format = "<xsl:value-of select='@format'/>"</xsl:if>
				<xsl:value-of select="$empty"/>)<xsl:value-of select="$empty-line"/>
			</xsl:otherwise>
		 </xsl:choose>
     </xsl:when>
     <xsl:when test="name()='element-ref'">
        <xsl:choose>
           <xsl:when test="@list='true' and @list-name">
              <xsl:value-of select="$empty"/>   @XmlElementWrapper(name = "<xsl:value-of select='@list-name'/>")<xsl:value-of select="$empty-line"/>
              <xsl:value-of select="$empty"/>   @XmlElement(name = "<xsl:value-of select='@name'/>")<xsl:value-of select="$empty-line"/>
           </xsl:when>
           <xsl:when test="@list='true' and not(@list-name)">
			  <xsl:variable name="element-name" select="@name"/>
			  <xsl:variable name="element" select="/root/element[@name = $element-name]"/> 
              <xsl:value-of select="$empty"/>   @XmlElements(@XmlElement(name = "<xsl:value-of select='@name'/>", type = <xsl:value-of select='$element/@do-class'/>.class))<xsl:value-of select="$empty-line"/>
           </xsl:when>
           <xsl:otherwise>
              <xsl:value-of select="$empty"/>   @XmlElement(name = "<xsl:value-of select='@name'/>")<xsl:value-of select="$empty-line"/>
           </xsl:otherwise>
        </xsl:choose>
     </xsl:when>
   </xsl:choose>
</xsl:template>

<xsl:template name="method-add-fields">
   <xsl:for-each select="element[@list='true' and @list-type='list'] | element-ref[@list='true' and (@list-type='list' or @list-type='map')]">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@add-method'/>(<xsl:value-of select='@value-type-element' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name-element'/>) {<xsl:value-of select="$empty-line"/>
      <xsl:choose>
         <xsl:when test="@list='true' and @list-type='list'">
	     	<xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/>.add(<xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
		 </xsl:when>
         <xsl:when test="@list='true' and @list-type='map'">
         	<xsl:variable name="name" select="@name"/>
			<xsl:variable name="key" select="//element[@name=$name]/node()[name()='attribute' or name()='element'][@key]"/>
	     	<xsl:value-of select="'      '"/><xsl:value-of select='@field-name'/>.put(<xsl:value-of select='@param-name-element'/>.<xsl:value-of select="$key/@get-method"/>(), <xsl:value-of select='@param-name-element'/>);<xsl:value-of select="$empty-line"/>
         </xsl:when>
      </xsl:choose>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-get-fields">
   <xsl:for-each select="attribute | element | element-ref">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public <xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@get-method'/>() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return <xsl:value-of select='@field-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-get-dynamic-attributes">
   <xsl:if test="@has-dynamic-attributes">
      <xsl:value-of select="$empty"/>   public Map<xsl:call-template name="generic-string-string-type"/> getDynamicAttributes() {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      return m_dynamicAttributes;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-set-fields">
   <xsl:for-each select="attribute | element[not(@list='true' and @list-type='list')] | element-ref[not(@list='true' and (@list-type='list' or @list-type='map'))]">
      <xsl:sort select="@name"/>
      
      <xsl:value-of select="$empty"/>   public void <xsl:value-of select='@set-method'/>(<xsl:value-of select='@value-type' disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/>) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/><xsl:text>      </xsl:text><xsl:value-of select='@field-name'/> = <xsl:value-of select='@param-name'/>;<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="method-set-dynamic-attribute">
   <xsl:if test="@has-dynamic-attributes">
      <xsl:value-of select="$empty"/>   public void setDynamicAttribute(String name, String value) {<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>      m_dynamicAttributes.put(name, value);<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>
</xsl:template>

<xsl:template name="method-to-string">
   <xsl:value-of select="$empty"/>   @Override<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   public String toString() {<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      StringBuilder sb = new StringBuilder();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      sb.append("<xsl:value-of select="@do-class"/>[");<xsl:value-of select="$empty-line"/>
   <xsl:for-each select="attribute | element | element-ref">
      <xsl:value-of select="$empty"/>      sb.append("<xsl:value-of select="$empty"/>
      <xsl:if test="position()!=1">,</xsl:if>
      <xsl:value-of select="@name"/>=").append(<xsl:value-of select="@field-name"/>);<xsl:value-of select="$empty-line"/>
   </xsl:for-each>
   <xsl:value-of select="$empty"/>      sb.append("]");<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>      return sb.toString();<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty"/>   }<xsl:value-of select="$empty-line"/>
   <xsl:value-of select="$empty-line"/>
</xsl:template>

<xsl:template name="generic-string-string-type">
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type" select="'String, String'"/>
   </xsl:call-template>
</xsl:template>

</xsl:stylesheet>
