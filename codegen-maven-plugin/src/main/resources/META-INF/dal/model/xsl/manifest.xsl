<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="src-main-java"/>
<xsl:param name="src-main-resources"/>
<xsl:param name="src-test-java"/>
<xsl:param name="src-test-resources"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="manifest">
      <xsl:apply-templates select="/model"/>
      <xsl:apply-templates select="/model/entity"/>
   </xsl:element>
</xsl:template>

<xsl:template match="model">
   <xsl:variable name="package" select="@model-package"/>
   <xsl:variable name="transform-package">
       <xsl:value-of select="$package"/><xsl:value-of select="'.transform'"/>
   </xsl:variable>

   <!-- IEntity class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'IEntity'"/>
     <xsl:with-param name="package" select="$package"/>
     <xsl:with-param name="template" select="'ientity.xsl'"/>
   </xsl:call-template>
   
   <!-- IVisitor class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'IVisitor'"/>
     <xsl:with-param name="package" select="$package"/>
     <xsl:with-param name="template" select="'ivisitor.xsl'"/>
   </xsl:call-template>
   
   <!-- Constants class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'Constants'"/>
     <xsl:with-param name="package" select="$package"/>
     <xsl:with-param name="template" select="'constants.xsl'"/>
   </xsl:call-template>
   
   <!-- BaseEntity class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'BaseEntity'"/>
     <xsl:with-param name="package" select="$package"/>
     <xsl:with-param name="template" select="'base-entity.xsl'"/>
   </xsl:call-template>
   
   <!-- Helper class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="concat(//entity[@root='true']/@class-name, 'Helper')"/>
     <xsl:with-param name="package" select="$package"/>
     <xsl:with-param name="template" select="'helper.xsl'"/>
   </xsl:call-template>
   
   <xsl:if test="@enable-xml-sample='true'">
      <!-- model.xml file -->
      <xsl:call-template name="generate-test-resource">
        <xsl:with-param name="file" select="concat(translate($package,'.','/'), '/', //entity[@root='true']/@name, '.xml')"/>
        <xsl:with-param name="template" select="'xml/sample.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-xml-schema='true'">
      <!-- model.xsd file -->
      <xsl:call-template name="generate-resource">
        <xsl:with-param name="file" select="concat(translate($package,'.','/'), '/', //entity[@root='true']/@name, '.xsd')"/>
        <xsl:with-param name="template" select="'xml/schema.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:if test="@enable-filter='true'">
      <!-- IFilter class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'IFilter'"/>
        <xsl:with-param name="package" select="$package"/>
        <xsl:with-param name="template" select="'ifilter.xsl'"/>
      </xsl:call-template>

      <!-- IVisitorEnabled class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'IVisitorEnabled'"/>
        <xsl:with-param name="package" select="$package"/>
        <xsl:with-param name="template" select="'ivisitor_enabled.xsl'"/>
      </xsl:call-template>

      <!-- BaseFilter class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'BaseFilter'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/base-filter.xsl'"/>
      </xsl:call-template>

      <!-- VisitorChain class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'VisitorChain'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/visitor-chain.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <!-- transform package -->

   <!-- XML format -->
   <xsl:if test="@enable-xml='true' or @enable-xml-builder='true'">
      <!-- DefaultXmlBuilder class -->
      <xsl:call-template name="generate-java">
         <xsl:with-param name="class" select="'DefaultXmlBuilder'"/>
         <xsl:with-param name="package" select="$transform-package"/>
         <xsl:with-param name="template" select="'transform/default-xml-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   <xsl:if test="@enable-xml='true' or @enable-xml-parser='true'">
      <!-- DefaultSaxMaker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultXmlMaker'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-xml-maker.xsl'"/>
      </xsl:call-template>
      <!-- DefaultSaxParser class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultXmlParser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-xml-parser.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <!-- JSON format -->
   <xsl:if test="@enable-json='true' or @enable-json-builder='true'">
      <!-- DefaultJsonBuilder class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultJsonBuilder'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-json-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   <xsl:if test="@enable-json='true' or @enable-json-parser='true'">
      <!-- DefaultJsonBuilder class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultJsonParser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-json-parser.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <!-- DOM format -->
   <xsl:if test="@enable-dom='true' or @enable-dom-builder='true'">
      <!-- DefaultDomMaker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultDomBuilder'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-dom-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   <xsl:if test="@enable-dom='true' or @enable-dom-parser='true'">
      <!-- DefaultDomParser class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultDomParser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-dom-parser.xsl'"/>
      </xsl:call-template>
   </xsl:if>
      
   <!-- Native format -->
   <xsl:if test="@enable-native='true' or @enable-native-builder='true'">
      <!-- DefaultNativeBuilder class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultNativeBuilder'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-native-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-native='true' or @enable-native-parser='true'">
      <!-- DefaultNativeParser class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultNativeParser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-native-parser.xsl'"/>
      </xsl:call-template>
   </xsl:if>
      
   <!-- Native2 format -->
   <xsl:if test="@enable-native2='true' or @enable-native2-builder='true'">
      <!-- DefaultNativeBuilder class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultNative2Builder'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-native2-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-native2='true' or @enable-native2-parser='true'">
      <!-- DefaultNativeParser class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultNative2Parser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-native2-parser.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <!-- supplement classes -->
   <xsl:if test="@enable-validator='true'">
      <!-- DefaultValidator class -->
       <xsl:call-template name="generate-java">
         <xsl:with-param name="class" select="'DefaultValidator'"/>
         <xsl:with-param name="package" select="$transform-package"/>
         <xsl:with-param name="template" select="'transform/default-validator.xsl'"/>
       </xsl:call-template>
   </xsl:if>
   
   <xsl:if test="@enable-maker='true'">
      <!-- DefaultMaker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultMaker'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-maker.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-merger='true'">
      <!-- DefaultMerger class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultMerger'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-merger.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-base-visitor='true'">
      <!-- BaseVisitor class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'BaseVisitor'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/base-visitor.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-base-visitor2='true'">
      <!-- BaseVisitor2 class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'BaseVisitor2'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/base-visitor2.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-empty-visitor='true'">
      <!-- EmptyVisitor class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'EmptyVisitor'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/empty-visitor.xsl'"/>
      </xsl:call-template>
   </xsl:if>

   <xsl:if test="@enable-xml='true' or @enable-xml-parser='true' or 
                 @enable-json='true' or @enable-json-parser='true' or 
                 @enable-dom='true' or @enable-dom-parser='true' or 
                 @enable-native='true' or @enable-native-parser='true' or 
                 @enable-native2='true' or @enable-native2-parser='true'">
      <!-- ILinker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'ILinker'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/ilinker.xsl'"/>
      </xsl:call-template>
   
      <!-- DefaultLinker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultLinker'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-linker.xsl'"/>
      </xsl:call-template>
      
      <!-- IMaker class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'IMaker'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/imaker.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:if test="entity/any">
      <xsl:call-template name="generate-java">
         <xsl:with-param name="class" select="'Any'"/>
         <xsl:with-param name="package" select="entity/@entity-package"/>
         <xsl:with-param name="template" select="'entity/any.xsl'"/>
      </xsl:call-template>
   </xsl:if>
</xsl:template>

<xsl:template match="entity">
   <!-- Entity class -->
   <xsl:call-template name="generate-java">
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="class" select="@entity-class"/>
      <xsl:with-param name="package" select="@entity-package"/>
      <xsl:with-param name="template" select="'entity/entity.xsl'"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-java">
   <xsl:param name="src-dir" select="$src-main-java" />
   <xsl:param name="template"/>
   <xsl:param name="package"/>
   <xsl:param name="class"/>
   <xsl:param name="name" select="''" />
   <xsl:param name="mode" select="'create_or_overwrite'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
        <xsl:value-of select="$src-dir"/>/<xsl:value-of select="translate($package,'.','/')"/>/<xsl:value-of select="$empty"/>
        <xsl:value-of select="$class"/>.java<xsl:value-of select="$empty"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
      <xsl:with-param name="package" select="$package"/>
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-resource">
   <xsl:param name="src-dir" select="$src-main-resources" />
   <xsl:param name="template"/>
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_or_overwrite'"/>


   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-test-java">
   <xsl:param name="src-dir" select="$src-test-java" />
   <xsl:param name="template"/>
   <xsl:param name="package"/>
   <xsl:param name="class"/>
   <xsl:param name="name" select="''" />
   <xsl:param name="mode" select="'create_if_not_exists'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
        <xsl:value-of select="$src-dir"/>/<xsl:value-of select="translate($package,'.','/')"/>/<xsl:value-of select="$empty"/>
        <xsl:value-of select="$class"/>.java<xsl:value-of select="$empty"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
      <xsl:with-param name="package" select="$package"/>
      <xsl:with-param name="class" select="$class"/>
      <xsl:with-param name="name" select="$name"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-test-resource">
   <xsl:param name="src-dir" select="$src-test-resources" />
   <xsl:param name="template"/>
   <xsl:param name="file" select="''" />
   <xsl:param name="mode" select="'create_or_overwrite'"/>

   <xsl:call-template name="generate-code">
      <xsl:with-param name="path">
          <xsl:value-of select="$src-dir"/>/<xsl:value-of select="$file"/>
      </xsl:with-param>
      <xsl:with-param name="template" select="$template"/>
      <xsl:with-param name="mode" select="$mode"/>
   </xsl:call-template>
</xsl:template>

<xsl:template name="generate-code">
   <xsl:param name="path" />
   <xsl:param name="template"/>
   <xsl:param name="mode"/>
   <xsl:param name="package" select="''"/>
   <xsl:param name="class" select="''"/>
   <xsl:param name="name" select="''"/>

    <xsl:value-of select="$empty-line"/>
    <xsl:element name="file">
       <xsl:attribute name="path"><xsl:value-of select="$path"/></xsl:attribute>
       
       <xsl:attribute name="template"><xsl:value-of select="$template"/></xsl:attribute>
       
       <xsl:attribute name="mode"><xsl:value-of select="$mode"/></xsl:attribute>
       
       <xsl:if test="$package">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">package</xsl:attribute>
             
             <xsl:value-of select="$package"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$class">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">class</xsl:attribute>
          
             <xsl:value-of select="$class"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:if test="$name">
          <xsl:value-of select="$empty-line"/>
          <xsl:element name="property">
             <xsl:attribute name="name">name</xsl:attribute>
          
             <xsl:value-of select="$name"/>
          </xsl:element>
       </xsl:if>
       
       <xsl:value-of select="$empty-line"/>
    </xsl:element>
</xsl:template>

</xsl:stylesheet>