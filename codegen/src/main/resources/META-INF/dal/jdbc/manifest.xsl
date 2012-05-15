<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:param name="xsl-dir"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="do-package" select="//entity/@do-package"/>

<xsl:template match="/">
   <xsl:call-template name="manifest"/>
</xsl:template>

<xsl:template name="manifest">
   <xsl:element name="manifest">
      <!-- Index class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@src-dir"/>/<xsl:value-of select="translate($do-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="'_INDEX'"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">index.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">do-package</xsl:attribute>
            
            <xsl:value-of select="$do-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
      
      <xsl:apply-templates select="/entities/entity[@gen='true' or not(@gen) and ../@gen='true']"/>
   </xsl:element>
</xsl:template>

<xsl:template match="entity">
   <xsl:if test="@do-package != ''">
      <!-- Do class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@src-dir"/>/<xsl:value-of select="translate(@do-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="@do-class"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">do.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">do-package</xsl:attribute>
            
            <xsl:value-of select="@do-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">name</xsl:attribute>
            
            <xsl:value-of select="@name"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
      
      <!-- Entity class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@src-dir"/>/<xsl:value-of select="translate(@do-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="@entity-class"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">entity.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">do-package</xsl:attribute>
            
            <xsl:value-of select="@do-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">name</xsl:attribute>
            
            <xsl:value-of select="@name"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
      
	  <!-- Dao class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@do-src-dir"/>/<xsl:value-of select="translate(@do-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="@dao-class"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">dao.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">do-package</xsl:attribute>
            
            <xsl:value-of select="@do-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">name</xsl:attribute>
            
            <xsl:value-of select="@name"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
   </xsl:if>

   <xsl:if test="@bo-package != ''">
      <!-- Bo class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@src-dir"/>/<xsl:value-of select="translate(@bo-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="@bo-class"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">bo.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">bo-package</xsl:attribute>
            
            <xsl:value-of select="@bo-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">name</xsl:attribute>
            
            <xsl:value-of select="@name"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
      
      <!-- BOF class -->
      <xsl:value-of select="$empty-line"/>
      <xsl:element name="file">
         <xsl:attribute name="path">
            <xsl:value-of select="@src-dir"/>/<xsl:value-of select="translate(@bo-package,'.','/')"/>/<xsl:value-of select="$empty"/>
            <xsl:value-of select="@bof-class"/>.java<xsl:value-of select="$empty"/>
         </xsl:attribute>
         
         <xsl:attribute name="template">bof.xsl</xsl:attribute>
         
         <xsl:attribute name="mode">create_or_overwrite</xsl:attribute>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">bo-package</xsl:attribute>
            
            <xsl:value-of select="@bo-package"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
         <xsl:element name="property">
            <xsl:attribute name="name">name</xsl:attribute>
            
            <xsl:value-of select="@name"/>
         </xsl:element>
         
         <xsl:value-of select="$empty-line"/>
      </xsl:element>
   </xsl:if>
</xsl:template>

</xsl:stylesheet>