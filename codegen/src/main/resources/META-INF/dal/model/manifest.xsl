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
   
   <xsl:variable name="policy-xml-sample">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'xml-sample'"/>
      </xsl:call-template>
   </xsl:variable>
   
   <xsl:if test="$policy-xml-sample='true'">
      <!-- model.xml file -->
      <xsl:call-template name="generate-test-resource">
        <xsl:with-param name="file" select="concat(translate($package,'.','/'), '/', //entity[@root='true']/@name, '.xml')"/>
        <xsl:with-param name="template" select="'xml/sample.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:variable name="policy-xml-schema">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'xml-schema'"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:if test="$policy-xml-schema='true'">
      <!-- model.xsd file -->
      <xsl:call-template name="generate-resource">
        <xsl:with-param name="file" select="concat(translate($package,'.','/'), '/', //entity[@root='true']/@name, '.xsd')"/>
        <xsl:with-param name="template" select="'xml/schema.xsl'"/>
        <xsl:with-param name="mode" select="'create_if_not_exists'"/>
      </xsl:call-template>
   </xsl:if>
   
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
     <xsl:with-param name="template" select="'base_entity.xsl'"/>
   </xsl:call-template>
   
   <!-- transform folder -->
   <xsl:variable name="transform-package">
       <xsl:value-of select="$package"/>
       <xsl:value-of select="'.transform'"/>
   </xsl:variable>

   <!-- DefaultValidator class -->
   <xsl:variable name="policy-validator">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'validator'"/>
      </xsl:call-template>
   </xsl:variable>
   
   <xsl:if test="$policy-validator='true'">
	   <xsl:call-template name="generate-java">
	     <xsl:with-param name="class" select="'DefaultValidator'"/>
	     <xsl:with-param name="package" select="$transform-package"/>
	     <xsl:with-param name="template" select="'transform/default-validator.xsl'"/>
	   </xsl:call-template>
   </xsl:if>
   
   <!-- DefaultXmlBuilder class -->
   <xsl:call-template name="generate-java">
     <xsl:with-param name="class" select="'DefaultXmlBuilder'"/>
     <xsl:with-param name="package" select="$transform-package"/>
     <xsl:with-param name="template" select="'transform/default-xml-builder.xsl'"/>
   </xsl:call-template>
   
   <xsl:variable name="policy-json-builder">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'json-builder'"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:if test="$policy-json-builder='true'">
      <!-- DefaultJsonBuilder class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultJsonBuilder'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-json-builder.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:variable name="policy-merger">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'merger'"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:if test="$policy-merger='true'">
      <!-- DefaultMerger class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'DefaultMerger'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/default-merger.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:variable name="policy-base-visitor">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'base-visitor'"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:if test="$policy-base-visitor='true'">
      <!-- BaseVisitor class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'BaseVisitor'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/base-visitor.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:variable name="policy-empty-visitor">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'empty-visitor'"/>
      </xsl:call-template>
   </xsl:variable>

   <xsl:if test="$policy-empty-visitor='true'">
      <!-- EmptyVisitor class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'EmptyVisitor'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/empty-visitor.xsl'"/>
      </xsl:call-template>
   </xsl:if>
   
   <xsl:variable name="policy-xml-parser-sax">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'sax-parser'"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="policy-xml-parser-dom">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'xml-parser'"/>
      </xsl:call-template>
   </xsl:variable>
   <xsl:variable name="policy-xml-parser-tag-node">
      <xsl:call-template name="model-policy">
         <xsl:with-param name="name" select="'xml-parser-tag-node'"/>
      </xsl:call-template>
   </xsl:variable>
   
   <xsl:if test="$policy-xml-parser-sax='true' or $policy-xml-parser-dom='true' or $policy-xml-parser-tag-node='true'">
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

      <!-- IParser class -->
      <xsl:call-template name="generate-java">
        <xsl:with-param name="class" select="'IParser'"/>
        <xsl:with-param name="package" select="$transform-package"/>
        <xsl:with-param name="template" select="'transform/iparser.xsl'"/>
      </xsl:call-template>
   
      <xsl:if test="$policy-xml-parser-tag-node='true'">
         <!-- TagNodeBasedMaker class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'TagNodeBasedMaker'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/tag-node-based-maker.xsl'"/>
         </xsl:call-template>

         <!-- TagNodeBasedParser class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'TagNodeBasedParser'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/tag-node-based-parser.xsl'"/>
         </xsl:call-template>
      </xsl:if>

      <xsl:if test="$policy-xml-parser-dom='true'">
         <!-- DefaultMaker class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'DefaultDomMaker'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/default-dom-maker.xsl'"/>
         </xsl:call-template>

         <!-- DefaultDomParser class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'DefaultDomParser'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/default-dom-parser.xsl'"/>
         </xsl:call-template>
      </xsl:if>

      <xsl:if test="$policy-xml-parser-sax='true'">
         <!-- DefaultSaxMaker class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'DefaultSaxMaker'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/default-sax-maker.xsl'"/>
         </xsl:call-template>

         <!-- DefaultSaxParser class -->
         <xsl:call-template name="generate-java">
           <xsl:with-param name="class" select="'DefaultSaxParser'"/>
           <xsl:with-param name="package" select="$transform-package"/>
           <xsl:with-param name="template" select="'transform/default-sax-parser.xsl'"/>
         </xsl:call-template>
      </xsl:if>
   </xsl:if>

   <xsl:if test="$policy-xml-parser-dom='true'">
      <xsl:variable name="policy-model-test">
         <xsl:call-template name="model-policy">
            <xsl:with-param name="name" select="'model-test'"/>
         </xsl:call-template>
      </xsl:variable>
         
      <xsl:if test="$policy-model-test='true'">
         <!-- model test class -->
         <xsl:call-template name="generate-test-java">
           <xsl:with-param name="class" select="concat(//entity[@root='true']/@entity-class, 'Test')"/>
           <xsl:with-param name="package" select="$package"/>
           <xsl:with-param name="template" select="'test/model-test.xsl'"/>
         </xsl:call-template>
      </xsl:if>
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