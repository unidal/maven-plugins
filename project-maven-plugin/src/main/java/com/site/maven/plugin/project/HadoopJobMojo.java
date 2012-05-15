package com.site.maven.plugin.project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import com.site.helper.Files;
import com.site.helper.Files.AutoClose;

/**
 * Package a hadoop job (named MyJob) into MyJob-1.0.0.jar, which can be
 * submitted to hadoop environment to execute directly.
 * <p>
 * 
 * @goal hadoop-job
 * @execute phase=install
 * @requiresDependencyResolution runtime
 * @author Frankie Wu
 */
public class HadoopJobMojo extends AbstractMojo {
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

	private void addMainClassAndManifest(Archiver a, File dir, File artifactJar, String classpath) throws IOException,
	      ArchiverException {
		String manifestName = "META-INF/MANIFEST.MF";
		JarFile jar = new JarFile(artifactJar);
		Manifest manifest = jar.getManifest();
		Attributes attributes = manifest.getMainAttributes();
		String mainClassName = attributes.getValue("Main-Class");

		if (mainClassName == null) {
			jar.close();
			throw new IllegalStateException(String.format("No Main-Class entry is found in jar(%s)!", artifactJar));
		}

		String mainClassPath = mainClassName.replace('.', '/') + ".class";
		ZipEntry entry = jar.getEntry(mainClassPath);
		File mainClassFile = File.createTempFile("main", "class");

		Files.forIO().copy(jar.getInputStream(entry), new FileOutputStream(mainClassFile), AutoClose.INPUT_OUTPUT);

		jar.close();

		attributes.putValue("Class-Path", classpath);

		File manifestFile = new File(dir, manifestName);

		manifestFile.getParentFile().mkdirs();

		FileOutputStream fos = new FileOutputStream(manifestFile);

		manifest.write(fos);
		fos.close();

		a.addFile(manifestFile, manifestName);
		a.addFile(mainClassFile, mainClassPath);
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		String jarName = m_project.getArtifactId() + "-" + m_project.getVersion() + "-job.jar";
		Archiver a = new ZipArchiver();

		a.setDestFile(new File(outputDir, jarName));

		try {
			List<String> elements = m_project.getRuntimeClasspathElements();

			makeArchive(a, m_project.getBuild().getDirectory(), m_project.getExecutionProject().getArtifact().getFile(),
			      elements);
			a.createArchive();

			getLog().info(String.format("File(%s) created.", a.getDestFile().getCanonicalPath()));
		} catch (Exception e) {
			throw new MojoExecutionException("Fail to resolve runtime classpath!", e);
		}
	}

	void makeArchive(Archiver a, String baseDir, File artifactJar, List<String> elements) throws Exception {
		StringBuilder classpath = new StringBuilder(2048);
		String mainJar = "lib/" + artifactJar.getName();

		a.addFile(artifactJar, mainJar);
		classpath.append(". ").append(mainJar);

		for (String element : elements) {
			File file = new File(element);

			if (file.isFile()) {
				String name = "lib/" + file.getName();

				a.addFile(file, name);
				classpath.append(' ').append(name);
			}
		}

		File tmpDir = new File(baseDir, "hadoop-job");

		addMainClassAndManifest(a, tmpDir, artifactJar, classpath.toString());

		if (verbose) {
			getLog().info(classpath);
		}
	}
}
