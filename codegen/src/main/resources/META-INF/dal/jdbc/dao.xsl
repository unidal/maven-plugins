<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8"/>
	
<xsl:param name="name"/>
<xsl:variable name="space" select="' '"/>
<xsl:variable name="empty" select="''"/>
<xsl:variable name="empty-line" select="'&#x0A;'"/>
<xsl:variable name="generic-do-class">
   <xsl:call-template name="generic-type">
      <xsl:with-param name="type" select="/entities/entity[@name = $name]/@do-class"/>
   </xsl:call-template>
</xsl:variable>

<xsl:template match="/">
   <xsl:apply-templates select="/entities/entity[@name = $name]"/>
</xsl:template>

<xsl:template match="entity">
   <xsl:variable name="do-package" select="@do-package"/>
   <xsl:if test="$do-package">
   <xsl:value-of select='$empty'/>package <xsl:value-of select="$do-package"/>;<xsl:value-of select='$empty-line'/>
      <xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:call-template name='import-list'/>
   <xsl:value-of select='$empty'/>public class <xsl:value-of select='@dao-class'/> extends AbstractDao {<xsl:value-of select='$empty-line'/>
   <xsl:call-template name="create-local"/>
   <xsl:call-template name="query-methods-delete"/>
   <xsl:call-template name="query-methods-find-multiple"/>
   <xsl:call-template name="query-methods-find-single"/>
   <xsl:call-template name="get-entity-class"/>
   <xsl:call-template name="query-methods-insert"/>
   <xsl:call-template name="query-methods-update"/>
   <xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>}<xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="import-list">
   <xsl:call-template name="import-entity-classes"/>
   <xsl:if test="query-defs/query[@type = 'SELECT' and @multiple = 'true']">
      <xsl:value-of select='$empty'/>import java.util.List;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.DalException;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:value-of select='$empty'/>import com.site.dal.jdbc.AbstractDao;<xsl:value-of select='$empty-line'/>
   <xsl:if test="query-defs/query[@type = 'SELECT']/param[@in = 'true' and @out = 'true']">
      <xsl:value-of select='$empty'/>import com.site.dal.DalRuntimeException;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'SELECT']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Readset;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'UPDATE']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Updateset;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:if test="query-defs/query[@type = 'SELECT']/param[@out = 'true']">
      <xsl:value-of select='$empty'/>import com.site.dal.jdbc.Ref;<xsl:value-of select='$empty-line'/>
   </xsl:if>
   <xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="import-entity-classes">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="relation">
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="relatedEntity" select="/entities/entity[@name=$entity-name]"/>
      
      <xsl:if test="$relatedEntity/@do-package != $entity/@do-package and generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>import <xsl:value-of select="$relatedEntity/@do-package"/>.<xsl:value-of select="$relatedEntity/@entity-class"/>;<xsl:value-of select="$empty-line"/>
      </xsl:if>
   </xsl:for-each>
</xsl:template>

<xsl:template name="get-entity-class">
   <xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>   @Override<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>   protected Class<xsl:call-template name="tag-start"/>?<xsl:call-template name="tag-end"/>[] getEntityClasses() {<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>      return new Class<xsl:call-template name="tag-start"/>?<xsl:call-template name="tag-end"/>[] { <xsl:value-of select='@entity-class'/>.class<xsl:value-of select='$empty'/>

   <xsl:for-each select="relation">
      <xsl:variable name="entity-name" select="@entity-name"/>
      <xsl:variable name="entity" select="/entities/entity[@name=$entity-name]"/>
      
      <xsl:if test="generate-id(../relation[@entity-name=$entity-name][1])=generate-id()">
         <xsl:value-of select="$empty"/>, <xsl:value-of select="$entity/@entity-class"/>.class<xsl:value-of select="$empty"/>
      </xsl:if>
   </xsl:for-each>

   <xsl:value-of select='$empty'/> };<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="create-local">
   <xsl:value-of select='$empty'/>   public <xsl:value-of select='@do-class'/> createLocal() {<xsl:value-of select='$empty-line'/>
   <xsl:value-of select="'      '"/><xsl:value-of select='@do-class'/> proto = new <xsl:value-of select='@do-class'/>();<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>      return proto;<xsl:value-of select='$empty-line'/>
   <xsl:value-of select='$empty'/>   }<xsl:value-of select='$empty-line'/>
</xsl:template>

<xsl:template name="query-methods-find-multiple">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'SELECT' and @multiple = 'true']">
   public List<xsl:call-template name="tag-start"/><xsl:value-of select='$entity/@do-class'/><xsl:call-template name="tag-end"/><xsl:value-of select="$space"/><xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
      <xsl:for-each select="param">
         <xsl:value-of select='$empty'/><xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> 
         <xsl:text>, </xsl:text>
      </xsl:for-each>Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> readset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:for-each select="param[@in='true' and @out='true']">
         <xsl:value-of select='$empty'/>      if (<xsl:value-of select="@param-name"/>.get() == null) {<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty'/>         throw new DalRuntimeException("<xsl:value-of select="@param-name"/> must be initialzied");<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty'/>      }<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty-line'/>
      </xsl:for-each>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> proto = new <xsl:value-of select='$entity/@do-class'/>();<xsl:value-of select='$empty-line'/>

      <xsl:if test="param[@in='true']">
         <xsl:value-of select='$empty-line'/>
         <xsl:for-each select="param[@in='true']">
            <xsl:choose>
               <xsl:when test="@out='true'">
                  <xsl:value-of select='$empty'/>      proto.<xsl:value-of select="@set-method"/>(<xsl:value-of select="@param-name"/>.get());<xsl:value-of select='$empty-line'/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select='$empty'/>      proto.<xsl:value-of select="@set-method"/>(<xsl:value-of select="@param-name"/>);<xsl:value-of select='$empty-line'/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
      </xsl:if>
      List<xsl:call-template name="tag-start"/><xsl:value-of select='$entity/@do-class'/><xsl:call-template name="tag-end"/> result = getQueryEngine().queryMultiple(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select='@upper-name'/>, 
            proto,
            readset);
      <xsl:if test="param[@out='true']">
         <xsl:value-of select='$empty-line'/>
         <xsl:for-each select="param[@out='true']">
            <xsl:value-of select="'      '"/><xsl:value-of select="@param-name"/>.set(proto.<xsl:value-of select="@get-method"/>());<xsl:value-of select='$empty-line'/>
         </xsl:for-each>
      </xsl:if>
      return result;
   }
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-find-single">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'SELECT' and not(@multiple = 'true')]">
   public <xsl:value-of select='$entity/@do-class'/><xsl:value-of select="$space"/><xsl:value-of select='@name'/>(<xsl:value-of select='$empty'/>
      <xsl:for-each select="param">
         <xsl:value-of select='$empty'/><xsl:value-of select="@value-type" disable-output-escaping="yes"/><xsl:value-of select="$space"/><xsl:value-of select="@param-name"/> 
         <xsl:text>, </xsl:text>
      </xsl:for-each>Readset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> readset) throws DalException {<xsl:value-of select='$empty-line'/>
      <xsl:for-each select="param[@in='true' and @out='true']">
         <xsl:value-of select='$empty'/>      if (<xsl:value-of select="@param-name"/>.get() == null) {<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty'/>         throw new DalRuntimeException("<xsl:value-of select="@param-name"/> must be initialzied");<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty'/>      }<xsl:value-of select='$empty-line'/>
         <xsl:value-of select='$empty-line'/>
      </xsl:for-each>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> proto = new <xsl:value-of select='$entity/@do-class'/>();<xsl:value-of select='$empty-line'/>
      
      <xsl:if test="param[@in='true']">
         <xsl:value-of select='$empty-line'/>
         <xsl:for-each select="param[@in='true']">
            <xsl:choose>
               <xsl:when test="@out='true'">
                  <xsl:value-of select='$empty'/>      proto.<xsl:value-of select="@set-method"/>(<xsl:value-of select="@param-name"/>.get());<xsl:value-of select='$empty-line'/>
               </xsl:when>
               <xsl:otherwise>
                  <xsl:value-of select='$empty'/>      proto.<xsl:value-of select="@set-method"/>(<xsl:value-of select="@param-name"/>);<xsl:value-of select='$empty-line'/>
               </xsl:otherwise>
            </xsl:choose>
         </xsl:for-each>
      </xsl:if>

      <xsl:value-of select='$empty-line'/>
      <xsl:value-of select="'      '"/><xsl:value-of select='$entity/@do-class'/> result = getQueryEngine().querySingle(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select='@upper-name'/>, 
            proto,
            readset);
      <xsl:if test="param[@out='true']">
         <xsl:value-of select='$empty-line'/>
         <xsl:for-each select="param[@out='true']">
            <xsl:value-of select="'      '"/><xsl:value-of select="@param-name"/>.set(proto.<xsl:value-of select="@get-method"/>());<xsl:value-of select='$empty-line'/>
         </xsl:for-each>
      </xsl:if>
      return result;
   }
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-insert">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'INSERT' and @batch = 'true']">
   public int[] <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/>[] protos) throws DalException {
      return getQueryEngine().insertBatch(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            protos);
   }
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'INSERT']">
   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/> proto) throws DalException {
      return getQueryEngine().insertSingle(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            proto);
   }
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-update">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'UPDATE' and @batch = 'true']">
   public int[] <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/>[] protos, Updateset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> updateset) throws DalException {
      return getQueryEngine().updateBatch(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            protos,
            updateset);
   }
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'UPDATE']">
   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/> proto, Updateset<xsl:value-of select="$generic-do-class" disable-output-escaping="yes"/> updateset) throws DalException {
      return getQueryEngine().updateSingle(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            proto,
            updateset);
   }
   </xsl:for-each>
</xsl:template>

<xsl:template name="query-methods-delete">
   <xsl:variable name="entity" select="."/>
   <xsl:for-each select="query-defs/query[@type = 'DELETE' and @batch = 'true']">
   public int[] <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/>[] protos) throws DalException {
      return getQueryEngine().deleteBatch(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            protos);
   }
   </xsl:for-each>
   <xsl:for-each select="query-defs/query[@type = 'DELETE']">
   public int <xsl:value-of select="@name"/>(<xsl:value-of select='$entity/@do-class'/> proto) throws DalException {
      return getQueryEngine().deleteSingle(
            <xsl:value-of select='$entity/@entity-class'/>.<xsl:value-of select="@upper-name"/>,
            proto);
   }
   </xsl:for-each>
</xsl:template>

<xsl:template name="tag-start">
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
</xsl:template>

<xsl:template name="tag-end">
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
</xsl:template>

<xsl:template name="generic-type">
   <xsl:param name="type"/>
   
   <xsl:text disable-output-escaping="yes">&#x3c;</xsl:text>
   <xsl:value-of select="$type" disable-output-escaping="yes"/>
   <xsl:text disable-output-escaping="yes">&#x3e;</xsl:text>
</xsl:template>

</xsl:stylesheet>
