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
   <xsl:apply-templates select="/wizard/webapp/module[@name=$name]"/>
</xsl:template>

<xsl:template match="module">
<xsl:value-of select="$empty"/>package <xsl:value-of select="$package"/>;
<xsl:if test="../@webres='true'">
import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.unidal.webres.resource.runtime.ResourceConfigurator;
import org.unidal.webres.resource.runtime.ResourceInitializer;
import org.unidal.webres.resource.runtime.ResourceRuntime;
import org.unidal.webres.resource.runtime.ResourceRuntimeContext;
import org.unidal.webres.resource.spi.IResourceRegistry;
import org.unidal.webres.tag.resource.ResourceTagConfigurator;
import org.unidal.webres.taglib.basic.ResourceTagLibConfigurator;<xsl:value-of select="$empty"/>
</xsl:if>
import com.site.web.mvc.Action;
import com.site.web.mvc.ActionContext;
import com.site.web.mvc.ActionPayload;
import com.site.web.mvc.Page;
<xsl:variable name="type">
	<xsl:value-of select="'T extends ActionPayload'"/>
	<xsl:call-template name="generic-type">
		<xsl:with-param name="type" select="'? extends Page, ? extends Action'"/>
	</xsl:call-template>
</xsl:variable>
public class <xsl:value-of select="@context-class"/><xsl:call-template name="generic-type"><xsl:with-param name="type" select="$type"/></xsl:call-template> extends ActionContext<xsl:call-template name="generic-type"><xsl:with-param name="type" select="'T'"/></xsl:call-template> {
<xsl:if test="../@webres='true'">
   @Override
   public void initialize(HttpServletRequest request, HttpServletResponse response) {
      super.initialize(request, response);

      String contextPath = request.getContextPath();

      synchronized (ResourceRuntime.INSTANCE) {
         if (!ResourceRuntime.INSTANCE.hasConfig(contextPath)) {
            ServletContext servletContext = request.getSession().getServletContext();
            File warRoot = new File(servletContext.getRealPath("/"));
   
            System.out.println("[INFO] Working directory is "+ System.getProperty("user.dir"));
            System.out.println("[INFO] War root is " + warRoot);
   
            ResourceRuntime.INSTANCE.removeConfig(contextPath);
            ResourceInitializer.initialize(contextPath, warRoot);
   
            IResourceRegistry registry = ResourceRuntime.INSTANCE.getConfig(contextPath).getRegistry();
   
            new ResourceConfigurator().configure(registry);
            new ResourceTagConfigurator().configure(registry);
            new ResourceTagLibConfigurator().configure(registry);
   
            registry.lock();
         }
   
         ResourceRuntimeContext.setup(contextPath);
      }
   }
</xsl:if>
}
</xsl:template>

</xsl:stylesheet>
