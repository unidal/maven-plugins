package com.site.maven.plugin.project;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import job.JobInstaller;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.site.helper.Files;

/**
 * Package a normal job (named MyJob) into MyJob-installer.jar, which can be
 * self-extracted to folder(./MyJob-1.0.0) using following command:<br>
 * <code>java -jar MyJob-1.0.0-installer.jar [dir]</code>
 * <p>
 * 
 * @goal job-installer
 * @execute phase=package
 * @requiresDependencyResolution runtime
 * @author Frankie Wu
 */
public class JobInstallerMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject m_project;

	/**
	 * @parameter expression="${outputDir}"
	 *            default-value="${project.build.directory}"
	 */
	protected String outputDir;

	/**
	 * Verbose information or not
	 * 
	 * @parameter expression="${verbose}" default-value="false"
	 */
	protected boolean verbose;

	private void addMainClass(Archiver a, File dir) throws IOException, ArchiverException {
		String mainClassName = JobInstaller.class.getName().replace('.', '/') + ".class";
		File mainClassFile = new File(dir, mainClassName);
		InputStream in = getClass().getResourceAsStream("/" + mainClassName);

		Files.forIO().writeTo(mainClassFile, Files.forIO().readFrom(in));

		a.addFile(mainClassFile, mainClassName);
	}

	private void addManifest(Archiver a, File dir) throws IOException, ArchiverException {
		StringBuilder manifest = new StringBuilder(4096);

		manifest.append("Manifest-Version: 1.0\r\n");
		manifest.append("Created-By: JobInstallerMojo\r\n");
		manifest.append("Main-Class: ").append(JobInstaller.class.getName()).append("\r\n");

		String manifestName = "META-INF/MANIFEST.MF";
		File manifestFile = new File(dir, manifestName);

		Files.forIO().writeTo(manifestFile, manifest.toString(), "utf-8");

		a.addFile(manifestFile, manifestName);
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String jarName = m_project.getArtifactId() + "-" + m_project.getVersion() + "-installer.jar";
		Archiver a = new ZipArchiver();

		a.setDestFile(new File(outputDir, jarName));

		try {
			List<String> elements = m_project.getRuntimeClasspathElements();

			makeArchive(a, m_project.getExecutionProject().getArtifact().getFile(), elements);
			a.createArchive();

			getLog().info(String.format("File(%s) created.", a.getDestFile().getCanonicalPath()));
		} catch (Exception e) {
			throw new MojoExecutionException("Fail to resolve runtime classpath!", e);
		}
	}

	private void makeArchive(Archiver a, File artifactJar, List<String> elements) throws Exception {
		StringBuilder classpath = new StringBuilder(2048);

		a.addFile(artifactJar, artifactJar.getName());

		for (String element : elements) {
			File file = new File(element);

			if (file.isFile()) {
				String name = "lib/" + file.getName();

				a.addFile(file, name);

				if (classpath.length() == 0) {
					classpath.append(name);
				} else {
					classpath.append(File.pathSeparatorChar).append(name);
				}
			}
		}

		File tmpDir = new File(m_project.getBuild().getDirectory(), "job-installer");

		addMainClass(a, tmpDir);
		addManifest(a, tmpDir);

		if (verbose) {
			getLog().info(classpath);
		}
	}
}
