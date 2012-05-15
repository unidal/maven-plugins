<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../naming.xsl"/>
<xsl:output method="xml" indent="yes" media-type="text/xml" encoding="utf-8" cdata-section-elements="statement"/>

<xsl:template match="/">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="entities">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="entity">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<!-- variable definition -->
		<xsl:variable name="name">
			<xsl:choose>
				<xsl:when test="@entity-name"><xsl:value-of select="@entity-name"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="nonrmalized-name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="$name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$nonrmalized-name"/>
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
		<xsl:attribute name="name">
			<xsl:value-of select="$name"/>
		</xsl:attribute>
		<xsl:attribute name="class-name">
			<xsl:value-of select="$class-name"/>
		</xsl:attribute>

		<xsl:attribute name="entity-class">
			<xsl:value-of select="$class-name"/><xsl:value-of select="'Entity'"/>
		</xsl:attribute>
		<xsl:attribute name="do-class">
			<xsl:value-of select="$class-name"/>
		</xsl:attribute>
		<xsl:attribute name="dao-class">
			<xsl:value-of select="$class-name"/><xsl:value-of select="'Dao'"/>
		</xsl:attribute>
		<xsl:attribute name="bo-class">
			<xsl:value-of select="$class-name"/><xsl:value-of select="'Bo'"/>
		</xsl:attribute>
		<xsl:attribute name="bof-class">
			<xsl:value-of select="$class-name"/><xsl:value-of select="'Bof'"/>
		</xsl:attribute>
		<xsl:attribute name="do-package">
			<xsl:value-of select="$do-package"/>
		</xsl:attribute>
		<xsl:attribute name="bo-package">
			<xsl:choose>
				<xsl:when test="@bo-package"><xsl:value-of select="@bo-package"/></xsl:when>
				<xsl:when test="../@bo-package"><xsl:value-of select="../@bo-package"/></xsl:when>
			</xsl:choose>
		</xsl:attribute>

		<xsl:attribute name="param-name">
			<xsl:value-of select="$nonrmalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="variable-name">
			<xsl:value-of select="'m_'"/>
			<xsl:value-of select="$nonrmalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="get-method">
			<xsl:value-of select="'get'"/>
			<xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/>
			<xsl:value-of select="$capital-name"/>
		</xsl:attribute>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="relation">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<!-- variable definition -->
		<xsl:variable name="normalized-name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="@name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$normalized-name"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- attribute declaration -->
		<xsl:attribute name="field-name">
			<xsl:value-of select="'m_'"/><xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="param-name">
			<xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="value-type">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name">
					<xsl:call-template name="normalize">
						<xsl:with-param name="source" select="@entity-name"/>
					</xsl:call-template>
			  </xsl:with-param>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="get-method">
			<xsl:variable name="prefix">
				<xsl:choose>
					<xsl:when test="@value-type = 'boolean'">is</xsl:when>
					<xsl:otherwise>get</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$prefix"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="member">
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

		<!-- attribute declaration -->
		<xsl:attribute name="name">
			<xsl:value-of select="$name"/>
		</xsl:attribute>
		<xsl:attribute name="field-name">
			<xsl:value-of select="'m_'"/><xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="param-name">
			<xsl:value-of select="$normalized-name"/>
		</xsl:attribute>
		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="$name"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="value-type">
			<xsl:call-template name="normalize-value-type">
				<xsl:with-param name="value-type" select="@value-type"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="get-method">
			<xsl:variable name="prefix">
				<xsl:choose>
					<xsl:when test="@value-type = 'boolean'">is</xsl:when>
					<xsl:otherwise>get</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$prefix"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>

		<xsl:if test="not(@all)">
			<xsl:attribute name="all">true</xsl:attribute>
		</xsl:if>
		
		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="flag">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<!-- attribute declaration -->
		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>
		
		<xsl:text><xsl:copy-of select="text()"/></xsl:text>
	</xsl:copy>
</xsl:template>

<xsl:template match="var">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<!-- variable definition -->
		<xsl:variable name="name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="@name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$name"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- attribute declaration -->
		<xsl:attribute name="field-name">
			<xsl:value-of select="'m_'"/><xsl:value-of select="$name"/>
		</xsl:attribute>
		<xsl:attribute name="param-name">
			<xsl:value-of select="$name"/>
		</xsl:attribute>
		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="value-type">
			<xsl:call-template name="normalize-value-type">
				<xsl:with-param name="value-type" select="@value-type"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="get-method">
			<xsl:variable name="prefix">
				<xsl:choose>
					<xsl:when test="@value-type = 'boolean'">is</xsl:when>
					<xsl:otherwise>get</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$prefix"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/>
			<xsl:value-of select="$capital-name"/>
		</xsl:attribute>

		<xsl:if test="@key-member">
			<xsl:attribute name="get-key-method">
				<xsl:value-of select="'get'"/>
				<xsl:call-template name="capital-name">
					<xsl:with-param name="name">
						<xsl:call-template name="normalize">
							<xsl:with-param name="source" select="@key-member"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
		</xsl:if>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="primary-key | index">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="readsets | updatesets">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="readset | updateset">
	<xsl:copy>
		<xsl:copy-of select="@name | @all"/>

		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:for-each select="member">
			<xsl:copy>
				<xsl:copy-of select="@name"/>

				<xsl:attribute name="upper-name">
					<xsl:call-template name="upper-name">
						<xsl:with-param name="name" select="@name"/>
					</xsl:call-template>
				</xsl:attribute>
			</xsl:copy>
		</xsl:for-each>

		<xsl:apply-templates select="readset-ref"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="readset-ref">
	<xsl:copy>
		<xsl:copy-of select="@name | @relation-name"/>

		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>
	</xsl:copy>
</xsl:template>

<xsl:template match="query-defs">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<xsl:apply-templates select="query[not(@enabled='false')] | text()"/>
	</xsl:copy>
</xsl:template>

<xsl:template match="query">
	<xsl:copy>
		<!-- variable definition -->
		<xsl:variable name="name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="@name"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- attribute declaration -->
		<xsl:attribute name="name">
			<xsl:value-of select="$name"/>
		</xsl:attribute>
		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>
		<xsl:attribute name="type">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@type"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:copy-of select="@multiple | @sp | @batch"/>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="param">
	<xsl:copy>
		<xsl:copy-of select="@name"/>

		<xsl:variable name="name" select="@name"/>
		<xsl:variable name="entity" select="ancestor::node()[name()='entity']"/>
		<xsl:variable name="var" select="$entity/var[@name=$name]"/>
		<xsl:variable name="member" select="$entity/member[@alias=$name or @name=$name]"/>
		<xsl:variable name="parent" select="$var | $member"/>

		<!-- variable definition -->
		<xsl:variable name="normalized-name">
			<xsl:call-template name="normalize">
				<xsl:with-param name="source" select="@name"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="capital-name">
			<xsl:call-template name="capital-name">
				<xsl:with-param name="name" select="$normalized-name"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- attribute declaration -->
		<xsl:attribute name="value-type">
			<xsl:call-template name="normalize-value-type">
				<xsl:with-param name="value-type" select="$parent/@value-type"/>
				<xsl:with-param name="is-ref" select="@type='out' or @type='inout'"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:attribute name="get-method">
			<xsl:variable name="prefix">
				<xsl:choose>
					<xsl:when test="@value-type = 'boolean'">is</xsl:when>
					<xsl:otherwise>get</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="$prefix"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>
		<xsl:attribute name="set-method">
			<xsl:value-of select="'set'"/><xsl:value-of select="$capital-name"/>
		</xsl:attribute>

		<xsl:attribute name="param-name">
			<xsl:value-of select="$normalized-name"/>
		</xsl:attribute>

		<xsl:attribute name="upper-name">
			<xsl:call-template name="upper-name">
				<xsl:with-param name="name" select="@name"/>
			</xsl:call-template>
		</xsl:attribute>

		<xsl:if test="$parent/@enum-class">
			<xsl:attribute name="enum-class">
				<xsl:value-of select="$parent/@enum-class"/>
			</xsl:attribute>

			<xsl:attribute name="enum-package">
				<xsl:value-of select="$parent/@enum-package"/>
			</xsl:attribute>
		</xsl:if>

		<xsl:attribute name="in">
			<xsl:choose>
				<xsl:when test="not(@type) or @type = 'in' or @type = 'inout'">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

		<xsl:attribute name="out">
			<xsl:choose>
				<xsl:when test="@type = 'out' or @type = 'inout'">true</xsl:when>
				<xsl:otherwise>false</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

<xsl:template match="statement">
	<xsl:copy>
		<xsl:copy-of select="@*"/>

		<xsl:apply-templates/>
	</xsl:copy>
</xsl:template>

</xsl:stylesheet>