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

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jasper.servlet.JspServlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import com.site.test.browser.Browser;
import com.site.test.browser.DefaultBrowser;
import com.site.web.MVC;

public class TestServer {
	private static TestServer s_instance;

	private static Context s_ctx;

	private Server m_server;

	@AfterClass
	public static void afterClass() throws Exception {
		if (s_instance != null) {
			s_instance.shutdown();
			s_instance = null;
		}
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		s_instance = new TestServer();
		s_instance.configure();
		s_instance.startServer(s_ctx);
		s_instance.postConfigure(s_ctx);
	}

	public static void main(String[] args) throws Exception {
		TestServer.beforeClass();

		try {
			s_instance.before();
			s_instance.test();
			s_instance.after();
		} finally {
			TestServer.afterClass();
		}
	}

	@After
	public void after() {
	}

	@Before
	public void before() {
	}

	protected void configure() throws Exception {
		Context ctx = new Context(Context.SESSIONS);
		String contextPath = getContextPath();

		if (contextPath != null) {
			if (contextPath.length() == 0) {
				contextPath = null;
			} else if (!contextPath.startsWith("/")) {
				throw new RuntimeException(String.format("ContextPath(%s) must be null or starting with '/'.",
						contextPath));
			}
		}

		ctx.setResourceBase(getWarRoot().getPath());
		ctx.setContextPath(contextPath == null ? "/" : contextPath);

		configureJsp(ctx);
		s_ctx = ctx;
	}

	protected ServletHolder configureJsp(Context ctx) throws Exception {
		ServletHolder jsp = ctx.addServlet(JspServlet.class, "*.jsp");
		String scratchDir = getScratchDir().getCanonicalPath();

		if (scratchDir != null) {
			jsp.setInitParameter("scratchdir", scratchDir);
		}

		jsp.setInitParameter("keepgenerated", "true");
		jsp.setInitParameter("genStringAsCharArray", "true");
		return jsp;
	}

	protected void display(String requestUri) throws Exception {
		StringBuilder sb = new StringBuilder(256);
		Browser browser = new DefaultBrowser();

		sb.append("http://localhost:").append(getServerPort()).append(requestUri);
		browser.display(new URL(sb.toString()));
	}

	protected String getContextPath() {
		return "/<xsl:value-of select="@name"/>";
	}

	protected File getScratchDir() {
		File work = new File(System.getProperty("java.io.tmpdir", "."), "<xsl:value-of select="@name"/>");

		work.mkdirs();
		return work;
	}

	protected int getServerPort() {
		return <xsl:value-of select="@server-port"/>;
	}

	protected String getTimestamp() {
		return new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());
	}

	protected File getWarRoot() {
		return new File("src/main/webapp");
	}

	protected void postConfigure(Context ctx) {
		ServletHolder mvc = new ServletHolder(new MVC());

		mvc.setInitParameter("cat-client-xml", "/data/appdatas/cat/client.xml");
<xsl:for-each select="module">
<xsl:value-of select="$empty" />		ctx.addServlet(mvc, "/<xsl:value-of select="@path"/>/*");
</xsl:for-each>
	}

	protected void shutdown() throws Exception {
		if (m_server != null) {
			m_server.stop();
		}
	}

	protected void startServer(Context ctx) throws Exception {
		Server server = new Server(getServerPort());

		server.setStopAtShutdown(true);
		server.setHandler(ctx);
		server.start();

		m_server = server;
	}

	@Test
	public void test() throws Exception {
		// open the page in the default browser
		display("/<xsl:value-of select="@name"/>/<xsl:value-of select="module[@default='true']/@path"/>");

		System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", getTimestamp()));
		System.in.read();
	}
}
</xsl:template>

</xsl:stylesheet>
