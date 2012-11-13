<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:import href="../../common.xsl" />
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.servlet.GzipFilter;

import org.unidal.test.jetty.JettyServer;

@RunWith(JUnit4.class)
public class TestServer extends JettyServer {
   public static void main(String[] args) throws Exception {
      TestServer server = new TestServer();

      server.startServer();
      server.showReport();
      server.stopServer();
   }

   @Before
   public void before() throws Exception {
      System.setProperty("devMode", "true");
      super.startServer();
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
      context.addFilter(GzipFilter.class, "/<xsl:value-of select="module[@default='true']/@path"/>/*", Handler.ALL);
   }

   @Test
   public void showReport() throws Exception {
      // open the page in the default browser
      display("/<xsl:value-of select="@name"/>/<xsl:value-of select="module[@default='true']/@path"/>");
      waitForAnyKey();
   }
}
</xsl:template>

</xsl:stylesheet>
