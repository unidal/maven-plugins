package org.unidal.maven.plugin.uml;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipInputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.helper.Files;
import org.unidal.helper.Files.Policy;

/**
 * Generate UML diagram from text files.
 * 
 * @goal edit
 * @aggregator true
 * @requiresProject false
 * @author Frankie Wu
 */
public class EditMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject m_project;

	private void display(Server server) throws Exception {
		int port = server.getConnectors()[0].getPort();
		String uri = String.format("http://localhost:%s/uml", port);

		Desktop.getDesktop().browse(new URI(uri));
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String warRoot = getWarRoot();

		try {
			Server server = startServer(warRoot);
			String mode = System.getProperty("mode", "local");

			if ("local".equals(mode)) {
				display(server);
			}

			waitForAnyKey();
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Error when starting up server at " + warRoot + "!", e);
		}
	}

	private String getWarRoot() {
		URL url = getClass().getResource("/webapp");
		String protocol = url.getProtocol();

		// extract webapp of jar into a temporary directory
		if (protocol.equals("jar")) {
			String path = url.getPath();

			if (path.startsWith("file:")) {
				File target = new File(m_project.getBasedir(), "target");
				int pos = path.indexOf('!');
				String jarPath = path.substring(5, pos);

				try {
					Files.forZip().copyDir(
							new ZipInputStream(new FileInputStream(jarPath)),
							target, new Policy() {
								@Override
								public boolean apply(String path) {
									return path.startsWith("webapp/");
								}
							});

					return new File(target, "webapp").getCanonicalPath();
				} catch (Exception e) {
					throw new RuntimeException(String.format(
							"Error when extracting jar(%s) to %s!", jarPath,
							target));
				}
			}
		}

		return url.getPath();
	}

	@SuppressWarnings("unchecked")
	private Server startServer(String warRoot) throws Exception {
		Server server = new Server(8650);
		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/");
		webapp.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		webapp.setResourceBase(warRoot);
		webapp.getInitParams().put(
				"org.mortbay.jetty.servlet.Default.dirAllowed", "false");
		server.setHandler(webapp);

		server.start();
		return server;
	}

	private void waitForAnyKey() throws IOException {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
				.format(new Date());

		System.out.println(String.format(
				"[%s] [INFO] Press any key to stop server ... ", timestamp));
		System.in.read();
	}
}
