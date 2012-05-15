<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8"/>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="root">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		
		<xsl:apply-templates mode="entity"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="element" mode="entity">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		
		<!-- variable definition -->
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="normalized-name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="$name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$normalized-name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="class-name">
			<xsl:choose>
				<xsl:when test="@class-name"><xsl:value-of select="@class-name"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="$capital-name"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="do-package">
			<xsl:choose>
				<xsl:when test="@do-package"><xsl:value-of select="@do-package"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="../@do-package"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- attribute definition -->
		<xsl:attribute name="do-package">
			<xsl:value-of select="$do-package"/>
		</xsl:attribute>
		<xsl:attribute name="do-class">
			<xsl:value-of select="$class-name"/>
		</xsl:attribute>
		
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="attribute">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		
		<!-- variable definition -->
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="normalized-name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="$name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$normalized-name"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- attribute definition -->
		<xsl:attribute name="value-type">
			<xsl:call-template name="normalize-value-type">
		       <xsl:with-param name="value-type">
		        	<xsl:value-of select="@value-type"/>
				    <xsl:if test="@value-type='Class'">
				    	<xsl:call-template name="generic-type">
				    		<xsl:with-param name="type">?</xsl:with-param>
				    	</xsl:call-template>
				    </xsl:if>
		        </xsl:with-param>
			</xsl:call-template>
		</xsl:attribute>
		<xsl:attribute name="param-name">
			<xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="field-name">
			<xsl:value-of select="'m_'"/>
			<xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="get-method">
			<xsl:choose>
				<xsl:when test="@value-type='boolean'">is</xsl:when>
				<xsl:otherwise>get</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/>
			<xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="element">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		
	    <xsl:call-template name="element">
	       <xsl:with-param name="element" select="."/>
	    </xsl:call-template>
		
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="element-ref">
	<xsl:copy>
		<xsl:copy-of select="@*"/>
		
		<xsl:variable name="element-name" select="@name"/>
		<xsl:variable name="element" select="/root/element[@name=$element-name]"/>

	    <xsl:call-template name="element">
	       <xsl:with-param name="element" select="$element"/>
	    </xsl:call-template>
		
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template name="element">
    <xsl:param name="element"/>
	
	<!-- variable definition -->
	<xsl:variable name="element-name">
		<xsl:choose>
			<xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="name">
		<xsl:choose>
			<xsl:when test="@list='true' and @list-alias"><xsl:value-of select="@list-alias"/></xsl:when>
			<xsl:when test="@list='true' and @list-name"><xsl:value-of select="@list-name"/></xsl:when>
			<xsl:when test="@list='true' and @list-type='map'"><xsl:value-of select="$element-name"/>Map</xsl:when>
			<xsl:when test="@list='true'"><xsl:value-of select="$element-name"/>List</xsl:when>
			<xsl:when test="@alias"><xsl:value-of select="@alias"/></xsl:when>
			<xsl:when test="$element/@alias"><xsl:value-of select="$element/@alias"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="normalized-name-element">
		<xsl:call-template name="normalize">
			<xsl:with-param name="source" select="$element-name"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="normalized-name">
		<xsl:call-template name="normalize">
			<xsl:with-param name="source" select="$name"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="normalized-value-type">
	    <xsl:call-template name="normalize-value-type">
	       <xsl:with-param name="value-type">
	        	<xsl:value-of select="@value-type"/>
			    <xsl:if test="@value-type='Class'">
			    	<xsl:call-template name="generic-type">
			    		<xsl:with-param name="type">?</xsl:with-param>
			    	</xsl:call-template>
			    </xsl:if>
	        </xsl:with-param>
	    </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="capital-name-element">
		<xsl:call-template name="capital-name">
			<xsl:with-param name="name" select="$normalized-name-element"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="capital-name">
		<xsl:call-template name="capital-name">
			<xsl:with-param name="name" select="$normalized-name"/>
		</xsl:call-template>
	</xsl:variable>
	<xsl:variable name="value-type-key">
	    <xsl:call-template name="normalize-key-type">
	       <xsl:with-param name="value-type">
	          <xsl:value-of select="$element/node()[name()='attribute' or name()='element'][@key]/@value-type"/>
	       </xsl:with-param>
	    </xsl:call-template>
	</xsl:variable>
	<xsl:variable name="value-type-element">
		<xsl:choose>
			<xsl:when test="@value-type">
			    <xsl:call-template name="normalize-key-type">
			       <xsl:with-param name="value-type" select="@value-type"/>
			    </xsl:call-template>
			    <xsl:if test="@value-type='Class'">
			    	<xsl:call-template name="generic-type">
			    		<xsl:with-param name="type">?</xsl:with-param>
			    	</xsl:call-template>
			    </xsl:if>
			</xsl:when>
			<xsl:when test="$element/@class-name">
			    <xsl:value-of select="$element/@class-name"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="capital-name">
					<xsl:with-param name="name">
						<xsl:call-template name="normalize">
							<xsl:with-param name="source">
								<xsl:choose>
									<xsl:when test="$element/@alias">
										<xsl:value-of select="$element/@alias"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="@name"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="value-type-generic">
	    <xsl:choose>
	       <xsl:when test="@list='true' and @list-type='list'">
	          <xsl:call-template name="generic-type">
	             <xsl:with-param name="type" select="$value-type-element"/>
	          </xsl:call-template>
	       </xsl:when>
	       <xsl:when test="@list='true' and @list-type='map'">
	          <xsl:call-template name="generic-type">
	             <xsl:with-param name="type">
	                <xsl:value-of select="$value-type-key"/>
	                <xsl:text>, </xsl:text>
	                <xsl:value-of select="$value-type-element"/>
	             </xsl:with-param>
	          </xsl:call-template>
	       </xsl:when>
	    </xsl:choose>
	</xsl:variable>
	<xsl:variable name="value-type">
	    <xsl:choose>
	       <xsl:when test="@list='true' and @list-type='list'">
	          <xsl:value-of select="'List'"/>
	          <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
	       </xsl:when>
	       <xsl:when test="@list='true' and @list-type='map'">
	          <xsl:value-of select="'Map'"/>
	          <xsl:value-of select="$value-type-generic" disable-output-escaping="yes"/>
	       </xsl:when>
	       <xsl:when test="@list='true'">
	          <xsl:value-of select="$value-type-element"/>
		      <xsl:text>[]</xsl:text>
	       </xsl:when>
	       <xsl:when test="$normalized-value-type != ''">
	          <xsl:value-of select="$normalized-value-type"/>
	       </xsl:when>
	       <xsl:otherwise>
	          <xsl:value-of select="$value-type-element"/>
	       </xsl:otherwise>
	    </xsl:choose>
	</xsl:variable>

	<!-- attribute definition -->
	<xsl:attribute name="value-type-key">
		<xsl:value-of select="$value-type-key"/>
	</xsl:attribute>
	<xsl:attribute name="value-type-element">
		<xsl:value-of select="$value-type-element"/>
	</xsl:attribute>
	<xsl:attribute name="value-type-generic">
		<xsl:value-of select="$value-type-generic"/>
	</xsl:attribute>
	<xsl:attribute name="value-type">
		<xsl:value-of select="$value-type"/>
	</xsl:attribute>
	<xsl:attribute name="param-name-element">
		<xsl:value-of select="$normalized-name-element"/>
	</xsl:attribute>
	<xsl:attribute name="param-name">
		<xsl:value-of select="$normalized-name"/>
	</xsl:attribute>
	<xsl:attribute name="field-name">
		<xsl:value-of select="'m_'"/>
		<xsl:value-of select="$normalized-name"/>
	</xsl:attribute>
	<xsl:attribute name="add-method">
		<xsl:value-of select="'add'"/>
		<xsl:value-of select="$capital-name-element"/>
	</xsl:attribute>
	<xsl:attribute name="get-method">
		<xsl:choose>
			<xsl:when test="$value-type='boolean'">is</xsl:when>
			<xsl:otherwise>get</xsl:otherwise>
		</xsl:choose>
		<xsl:value-of select="$capital-name"/>
	</xsl:attribute>
	<xsl:attribute name="set-method">
		<xsl:value-of select="'set'"/>
		<xsl:value-of select="$capital-name"/>
	</xsl:attribute>
</xsl:template>

</xsl:stylesheet>