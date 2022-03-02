<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../common.xsl" />
<xsl:output method="html" indent="no" media-type="text/plain" encoding="utf-8" />
<xsl:param name="package" />
<xsl:variable name="space" select="' '" />
<xsl:variable name="empty" select="''" />
<xsl:variable name="empty-line" select="'&#x0A;'" />

<xsl:template match="/">
<xsl:apply-templates select="/wizard/webapp" />
</xsl:template>

<xsl:template match="webapp">
<xsl:value-of select="$empty" />package <xsl:value-of select="$package" />;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.unidal.test.jetty.JettyServer;

@RunWith(JUnit4.class)
public class TestServer extends JettyServer {
   public static void main(String[] args) throws Exception {
      TestServer server = new TestServer();

      server.startServer();
      server.startWebapp();
      server.stopServer();
   }
   
   @Override
   protected void configure(WebAppContext context) throws Exception {
      System.setProperty("template.html.cache.ttl", "-1"); // disable HTML template cache
      System.setProperty("devMode", "true");

      super.configure(context);
   }

   @Override
   protected String getContextPath() {
      return "/<xsl:value-of select="@name"/>";
   }

   @Override
   protected int getServerPort() {
      return <xsl:value-of select="@server-port"/>;
   }

   @Override
   protected void postConfigure(WebAppContext context) {
      context.addFilter(GzipFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
   }

   @Test
   public void startWebapp() throws Exception {
      // open the page in the default browser
      display("/<xsl:value-of select="@name"/>/<xsl:value-of select="module[@default='true']/@path"/>");
      waitForAnyKey();
   }
}
</xsl:template>

</xsl:stylesheet>
