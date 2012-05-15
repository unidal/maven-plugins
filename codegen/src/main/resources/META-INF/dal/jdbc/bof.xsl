<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>

<xsl:template match="/">
   <xsl:apply-templates select="/entities/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:if test="@bo-package">
      <xsl:value-of select='$empty'/>package <xsl:value-of select="@bo-package"/>;<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="$empty-line"/>
   </xsl:if>

   <xsl:call-template name='import-list'/>
   <xsl:value-of select='$empty'/>public class <xsl:value-of select='@bof-class'/> {<xsl:value-of select='$empty-line'/>
   <xsl:call-template name="variable-definition"/>
   <xsl:call-template name="create-local"/>
   <xsl:call-template name="query-methods-find-multiple"/>
   <xsl:call-template name="query-methods-find-single"/>
   <xsl:call-template name="query-methods-insert"/>
   <xsl:call-template name="query-methods-update"/>
   <xsl:call-template name="query-methods-delete"/>
   <xsl:value-of select='$empty'/>}<xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:if test="query-defs/query[@type = 'SELECT' and @multiple = 'true']">
      <xsl:value-of select='$empty'/>import java.util.List;<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:value-of select='$empty'/>import com.site.dal.jdbc.BizObjectHelper;<xsl:value-of select='$empty-line'/>
   <xsl:if test="query-defs/query[@type = 'INSERT' or @type = 'UPDATE' or @type = 'SELECT' and not(@multiple = 'true')]">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.DalException;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@paged='true'] or query-defs/query[@type = 'SELECT']/param[@in = 'true' and @out = 'true']">
      <xsl:value-of select='$empty'/>import com.site.dal.DalRuntimeException;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'SELECT']/param[@out = 'true']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Ref;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'SELECT']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Readset;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'UPDATE']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Updateset;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="@bo-package!=@do-package">
      <xsl:value-of select='$empty'/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@dao-class"/>;<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>import <xsl:value-of select="@do-package"/>.<xsl:value-of select="@do-class"/>;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="variable-definition">
   <xsl:value-of select='$empty'/>   private <xsl:value-of select='@dao-class'/> m_dao;<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="create-local">
   <xsl:value-of select='$empty'/>   public <xsl:value-of select='@bo-class'/> createLocal() {<xsl:value-of select='$empty-line'/>
    <xsl:value-of select="'      '"/><xsl:value-of select='@do-class'/><xsl:value-of select="$space"/><xsl:value-of select='@param-name'/> = m_dao.createLocal();<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>      return BizObjectHelper.wrap(<xsl:value-of select='@param-name'/>, <xsl:value-of select='@bo-class'/>.class);<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="query-methods-find-multiple">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'SELECT' and @multiple = 'true']">
      <xsl:value-of select='$empty'/>   public List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
      <xsl:call-template name="query-find-params-definition">
         <xsl:with-param name="params" select="param"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>Readset<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> readset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      List<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template>doList = m_dao.<xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
         <xsl:call-template name="query-find-params">
            <xsl:with-param name="params" select="param"/>
         </xsl:call-template>
         <xsl:value-of select="$empty"/>readset);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return BizObjectHelper.wrap(doList, <xsl:value-of select='$entity/@bo-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-find-single">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'SELECT' and not(@multiple = 'true')]">
      <xsl:value-of select='$empty'/>   public <xsl:value-of select='$entity/@bo-class'/><xsl:value-of select="$space"/><xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
      <xsl:call-template name="query-find-params-definition">
         <xsl:with-param name="params" select="param"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>Readset<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> readset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/><xsl:value-of select="' '"/><xsl:value-of select="$entity/@param-name"/> = m_dao.<xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
      <xsl:call-template name="query-find-params">
         <xsl:with-param name="params" select="param"/>
      </xsl:call-template>
      <xsl:value-of select="$empty"/>readset);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return BizObjectHelper.wrap(<xsl:value-of select='$entity/@param-name'/>, <xsl:value-of select='$entity/@bo-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-insert">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'INSERT' and @batch = 'true']">
      <xsl:value-of select='$empty'/>   public int[] <xsl:value-of select="@name"/>(List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>List) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      List<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> protoList = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>List, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/>[] protoDos = new <xsl:value-of select='$entity/@do-class'/>[protoList.size()];<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      protoList.toArray(protoDos);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDos);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'INSERT']">
      <xsl:value-of select='$empty'/>   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@bo-class'/><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> protoDo = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDo);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-update">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'UPDATE' and @batch = 'true']">
      <xsl:value-of select='$empty'/>   public int[] <xsl:value-of select="@name"/>(List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>List, Updateset<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> updateset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      List<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> protoList = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>List, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/>[] protoDos = new <xsl:value-of select='$entity/@do-class'/>[protoList.size()];<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      protoList.toArray(protoDos);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDos, updateset);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'UPDATE']">
      <xsl:value-of select='$empty'/>   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@bo-class'/><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>, Updateset<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> updateset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> protoDo = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDo, updateset);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-delete">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'DELETE' and @batch = 'true']">
      <xsl:value-of select='$empty'/>   public int[] <xsl:value-of select="@name"/>(List<xsl:call-template name="generic-bo-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>List) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      List<xsl:call-template name="generic-do-type"><xsl:with-param name="entity" select="$entity"/></xsl:call-template> protoList = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>List, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/>[] protoDos = new <xsl:value-of select='$entity/@do-class'/>[protoList.size()];<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      protoList.toArray(protoDos);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDos);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'DELETE']">
      <xsl:value-of select='$empty'/>   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@bo-class'/><xsl:value-of select='$space'/><xsl:value-of select='$entity/@param-name'/>) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> protoDo = BizObjectHelper.unwrap(<xsl:value-of select='$entity/@param-name'/>, <xsl:value-of select='$entity/@do-class'/>.class);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>      return m_dao.<xsl:value-of select="@name"/>(protoDo);<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-find-params-definition">
   <xsl:param name="params"/>

   <xsl:for-each select="$params">
      <xsl:choose>
         <xsl:when test="@enum-class">
            <xsl:value-of select="@enum-class"/><xsl:value-of select="$space"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="@value-type"/><xsl:value-of select="$space"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:value-of select="@param-name"/>
      <xsl:text>, </xsl:text>
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-find-params">
   <xsl:param name="params"/>

   <xsl:for-each select="$params">
      <xsl:choose>
         <xsl:when test="@enum-class and @upper-value-type='STRING'">
            <xsl:value-of select='$empty'/>(<xsl:value-of select="@param-name"/> == null ? null : <xsl:value-of select="@param-name"/>.getName())<xsl:value-of select='$empty'/>
         </xsl:when>
         <xsl:when test="@enum-class and @upper-value-type='INT'">
            <xsl:value-of select='$empty'/>(<xsl:value-of select="@param-name"/> == null ? 0 : <xsl:value-of select="@param-name"/>.getId())<xsl:value-of select='$empty'/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="@param-name"/>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:text>, </xsl:text>
   </xsl:for-each>
</xsl:template>

<xsl:template name="generic-bo-type">
   <xsl:param name="entity" select="."/>

   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$entity/@bo-class"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
   <xsl:text> </xsl:text>
</xsl:template>

<xsl:template name="generic-do-type">
   <xsl:param name="entity" select="."/>

   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$entity/@do-class"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
   <xsl:text> </xsl:text>
</xsl:template>

</xsl:stylesheet>
