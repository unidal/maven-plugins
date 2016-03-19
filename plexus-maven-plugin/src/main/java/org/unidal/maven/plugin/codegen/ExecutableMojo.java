package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.IMatcher;

/**
 * Creates an executable jar from standard war and current project.
 *
 * @goal executable
 * @phase package
 * @requiresProject
 * @requiresDependencyResolution test
 */
public class ExecutableMojo extends AbstractMojo {
	/**
	 * This Maven project
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject m_project;

	/**
	 * Working directory
	 *
	 * @parameter expression="${project.build.directory}/${project.build.finalName}_jar"
	 * @readonly
	 * @required
	 */
	private File m_work;

	/**
	 * Source war file
	 *
	 * @parameter expression="${project.build.directory}/${project.build.finalName}.war"
	 * @readonly
	 * @required
	 */
	private File m_warFile;

	/**
	 * Target jar file
	 *
	 * @parameter expression="${project.build.directory}/${project.build.finalName}.jar"
	 * @readonly
	 * @required
	 */
	private File m_jarFile;

	/**
	 * For attaching artifacts etc.
	 *
	 * @component
	 * @readonly
	 */
	private MavenProjectHelper m_projectHelper;

	/**
	 * The main class that should be activated
	 *
	 * @parameter expression="${mainClass}"
	 */
	private String mainClass;

	void copyBootstrapClasses(File work) throws IOException {
		copyClass(work, "/bootstrap/Bootstrap.class");
	}

	private void copyClass(File work, String name) throws IOException, FileNotFoundException {
		InputStream in = getClass().getResourceAsStream(name);
		File file = new File(work, name);

		file.getParentFile().mkdirs();
		Files.forIO().copy(in, new FileOutputStream(file));
	}

	void copyClassFile(File work, String testClasses, String main) throws IOException, FileNotFoundException {
		String classFile = main.replace('.', '/') + ".class";

		Files.forDir().copyFile(new File(testClasses, classFile), new File(work, "WEB-INF/classes/" + classFile));
	}

	private void copyTestScopedDependencies(File work, List<String> compile, List<String> runtime, List<String> test)
	      throws IOException {
		for (String e : test) {
			if (!compile.contains(e) && !runtime.contains(e)) {
				if (e.endsWith(".jar")) {
					File jar = new File(e);

					Files.forDir().copyFile(jar, new File(work, "WEB-INF/ext/" + jar.getName()));
					getLog().debug("copying " + jar.getName() + "to WEB-INF/ext");
				}
			}
		}
	}

	String detectTestClass(File testClasses, final String className) {
		final StringBuilder sb = new StringBuilder();

		Scanners.forDir().scanForOne(testClasses, new FileMatcher() {
			@Override
			public Direction matches(File base, String path) {
				if (path.endsWith(className)) {
					int pos = path.lastIndexOf('.');

					sb.append(path.substring(0, pos).replace('/', '.'));
				}

				return Direction.DOWN;
			}
		});

		if (sb.length() == 0) {
			return null;
		} else {
			return sb.toString();
		}
	}

	public void execute() throws MojoExecutionException {
		getLog().info("Checking WAR file: " + m_warFile);

		if (!m_warFile.exists()) {
			throw new MojoExecutionException("Not found, please run 'mvn package' first!");
		}

		getLog().info("Working dir is " + m_work);
		m_work.mkdirs();

		try {
			getLog().info("Extract WAR ...");
			extractWar(m_work, m_warFile);

			getLog().info("Copy bootstrap classes ...");
			copyBootstrapClasses(m_work);

			getLog().info("Revise MANIFEST.MF ...");
			reviseManifest(m_work);

			List<String> test = m_project.getTestClasspathElements();
			List<String> compile = m_project.getCompileClasspathElements();
			List<String> runtime = m_project.getRuntimeClasspathElements();

			getLog().info("Copy test scope dependencies to /WEB-INF/ext ...");
			copyTestScopedDependencies(m_work, compile, runtime, test);

			getLog().info("Package into jar: " + m_jarFile);
			packageIntoJar(m_work, m_jarFile);

			m_projectHelper.attachArtifact(m_project, "jar", m_jarFile);
		} catch (Exception e) {
			getLog().error(e);
			throw new MojoExecutionException("Error when making executable jar from war!", e);
		}
	}

	void extractWar(File target, File warFile) throws IOException {
		ZipInputStream zis = new ZipInputStream(new FileInputStream(warFile));

		Files.forZip().copyDir(zis, target);
		zis.close();
	}

	void packageIntoJar(File work, File jarFile) throws IOException {
		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jarFile));

		Scanners.forDir().scan(work, new IMatcher<File>() {
			@Override
			public boolean isDirEligible() {
				return true;
			}

			@Override
			public boolean isFileElegible() {
				return true;
			}

			@Override
			public Direction matches(File base, String path) {
				ZipEntry entry = new ZipEntry(path);

				try {
					File file = new File(base, path);

					if (file.isFile()) {
						byte[] data = Files.forIO().readFrom(file);

						entry.setSize(data.length);
						entry.setTime(file.lastModified());
						zos.putNextEntry(entry);
						zos.write(data);
					}
				} catch (IOException e) {
					// ignore
					getLog().warn(e.getMessage());
				}

				return Direction.DOWN;
			}
		});

		zos.closeEntry();
		zos.close();
	}

	void reviseManifest(File work) throws IOException {
		File file = new File(work, "META-INF/MANIFEST.MF");
		InputStream in = new FileInputStream(file);
		Manifest manifest = new Manifest(in);

		in.close();

		// revise Main-Class
		Attributes a = manifest.getMainAttributes();
		String main = mainClass;

		if (main == null) {
			main = a.getValue("Main-Class");
		}

		if (main == null) {
			String testClasses = m_project.getBuild().getTestOutputDirectory();

			main = detectTestClass(new File(testClasses), "/TestServer.class");
		}

		if (main == null) {
			throw new IllegalStateException("TestServer is not found!");
		}

		getLog().info("Main class is " + main);

		a.putValue("Main-Class", "bootstrap.Bootstrap");
		a.putValue("X-Main-Class", main);

		copyClassFile(work, m_project.getBuild().getTestOutputDirectory(), main);

		FileOutputStream out = new FileOutputStream(file);

		manifest.write(out);
		out.close();
	}
}