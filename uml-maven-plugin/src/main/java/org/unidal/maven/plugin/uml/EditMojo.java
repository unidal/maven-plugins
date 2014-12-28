package org.unidal.maven.plugin.uml;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.helper.Files;

/**
 * Generate UML diagram from text.
 * 
 * @goal edit
 * @aggregator true
 * @requiresProject false
 * @requiresDependencyResolution runtime
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

	protected File m_warFile;

	private void display(Server server) throws Exception {
		int port = server.getConnectors()[0].getPort();
		String uri = String.format("http://localhost:%s/uml", port);

		Desktop.getDesktop().browse(new URI(uri));
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String warRoot = getWarRoot();
			Server server = startServer(warRoot);

			display(server);
			waitForAnyKey();
		} catch (Exception e) {
			throw new MojoExecutionException("Error when starting up server! " + e, e);
		}
	}

	private String getWarRoot() throws Exception {
		File warFile = null;

		for (Artifact a : m_project.getDependencyArtifacts()) {
			if ("war".equals(a.getType())) {
				warFile = a.getFile();
				break;
			}
		}

		if (warFile == null) {
			throw new RuntimeException("No war file found in local repository!");
		}

		File target = new File(m_project.getBasedir(), "target/webapp");

		try {
			Files.forZip().copyDir(new ZipInputStream(new FileInputStream(warFile)), target, null);

			return target.getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when extracting war(%s) to %s!", warFile, target));
		}
	}

	@SuppressWarnings("unchecked")
	private Server startServer(String warRoot) throws Exception {
		Server server = new Server(8650);
		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/");
		webapp.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		webapp.setResourceBase(warRoot);
		webapp.getInitParams().put("org.mortbay.jetty.servlet.Default.dirAllowed", "false");
		server.setHandler(webapp);

		server.start();
		return server;
	}

	private void waitForAnyKey() throws IOException {
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

		System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", timestamp));
		System.in.read();
	}
}
