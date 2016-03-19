package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.Arg;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.unidal.helper.Joiners;
import org.unidal.helper.Splitters;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;

/**
 * Generates file(/META_INF/plexus/components.xml) for Plexus component lookup.
 * 
 * @goal plexus
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @author Frankie Wu
 */
public class PlexusMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject m_project;

	/**
	 * Configurator class names delimitered with comma(',').
	 * 
	 * @parameter expression="${className}"
	 * @required
	 */
	protected String className;

	/**
	 * @parameter expression="${env}"
	 */
	protected String env;

	/**
	 * Verbose information or not
	 * 
	 * @parameter expression="${verbose}" default-value="false"
	 */
	protected boolean verbose;

	/**
	 * Verbose information or not
	 * 
	 * @parameter expression="${debug}" default-value="false"
	 */
	protected boolean debug;

	/**
	 * Skip this codegen or not
	 * 
	 * @parameter expression="${codegen.skip}" default-value="false"
	 */
	protected boolean skip;

	@SuppressWarnings("deprecation")
	protected Map<String, String> buildProperties() {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		Properties userProperties = m_project.getProjectBuildingRequest().getUserProperties();

		if (userProperties != null) {
			for (Map.Entry<Object, Object> e : userProperties.entrySet()) {
				properties.put((String) e.getKey(), (String) e.getValue());
			}
		}

		return properties;
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Model codegen was skipped explicitly.");
			return;
		}

		if (debug) {
			verbose = true;
		}

		try {
			List<String> names = Splitters.by(',').noEmptyItem().trim().split(className);
			List<String> classpathElements = m_project.getCompileClasspathElements();
			ClassLoader classLoader = makeClassLoader(classpathElements);
			String classpath = Joiners.by(System.getProperty("path.separator")).join(classpathElements);

			if (verbose) {
				getLog().info("Configurators: " + className);
				getLog().info("Classpath: " + classpath);
			}

			Thread.currentThread().setContextClassLoader(classLoader);

			for (String name : names) {
				if (debug) {
					getLog().info("Configurator: " + name);
				}

				Class<?> clazz = classLoader.loadClass(name);

				if (clazz == null) {
					throw new MojoExecutionException(String.format("Configurator(%s) is not found!", name));
				} else if (AbstractResourceConfigurator.class.isAssignableFrom(clazz)) {
					runJavaApplication(classpath, name, buildProperties());
				} else {
					throw new MojoExecutionException(String.format("Class(%s) is not extended from %s.", name,
					      AbstractResourceConfigurator.class));
				}
			}
		} catch (MojoExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new MojoExecutionException("Error when generating plexus components descriptor!", e);
		}
	}

	protected ClassLoader makeClassLoader(List<String> classpathElements) {
		List<URL> urls = new ArrayList<URL>(classpathElements.size());

		try {
			for (String element : classpathElements) {
				File file = new File(element);

				urls.add(file.toURI().toURL());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
	}

	protected void runJavaApplication(String classpath, String application, Map<String, String> properties)
	      throws MojoExecutionException {
		Commandline cli = new Commandline();

		cli.setWorkingDirectory(m_project.getBasedir());
		cli.setExecutable("java");

		StringBuilder sb = new StringBuilder(4096);

		if (classpath != null) {
			sb.append("-cp \"").append(classpath).append("\" ");
		}

		for (Map.Entry<String, String> e : properties.entrySet()) {
			String name = e.getKey();
			String value = e.getValue();

			sb.append("-D").append(name).append("=");

			if (value.indexOf(' ') >= 0) {
				sb.append("\"").append(value).append("\"");
			} else {
				sb.append(value);
			}

			sb.append(' ');
		}

		sb.append(application);

		Arg arg = cli.createArg();

		arg.setLine(sb.toString());

		if (debug) {
			getLog().info("Executing java " + sb);
		}

		try {
			CommandLineUtils.executeCommandLine(cli, new StreamConsumer() {
				@Override
				public void consumeLine(String line) {
					getLog().info(line);
				}
			}, new StreamConsumer() {
				@Override
				public void consumeLine(String line) {
					getLog().error(line);
				}
			});
		} catch (CommandLineException e) {
			throw new MojoExecutionException("Fail to execute command!", e);
		}
	}
}
