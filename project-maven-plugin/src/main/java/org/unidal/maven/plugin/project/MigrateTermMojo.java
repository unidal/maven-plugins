package org.unidal.maven.plugin.project;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Splitters;

/**
 * Migrate all term occurred.
 * 
 * @goal migrate-term
 */
public class MigrateTermMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @parameter expression="${from}"
	 * @required
	 */
	private String m_from;

	/**
	 * @parameter expression="${to}"
	 * @required
	 */
	private String m_to;

	/**
	 * @parameter expression="${verbose}" default-value="false"
	 * @required
	 */
	private boolean verbose;

	private String m_from2;

	private String m_to2;

	private int m_success;

	private int m_changed;

	private int m_failure;

	private String capitalize(String str) {
		int len = str.length();
		StringBuilder sb = new StringBuilder(len);

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (i == 0) {
				sb.append(Character.toUpperCase(ch));
			} else {
				sb.append(ch);
			}
		}

		return sb.toString();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		m_from2 = capitalize(m_from);
		m_to2 = capitalize(m_to);

		try {
			long start = System.currentTimeMillis();
			final Set<File> sourceFolders = new HashSet<File>();
			File baseDir = project.getBasedir().getCanonicalFile();

			m_success = 0;
			m_changed = 0;
			m_failure = 0;

			for (String source : (List<String>) project.getCompileSourceRoots()) {
				sourceFolders.add(new File(source));
			}

			for (String testSource : (List<String>) project.getTestCompileSourceRoots()) {
				sourceFolders.add(new File(testSource));
			}

			for (Resource resource : (List<Resource>) project.getResources()) {
				sourceFolders.add(new File(resource.getDirectory()));
			}

			for (Resource testResource : (List<Resource>) project.getTestResources()) {
				sourceFolders.add(new File(testResource.getDirectory()));
			}

			if (new File(baseDir, "pom.xml").exists()) {
				migrateFile(new File(baseDir, "pom.xml"), new File(baseDir, "pom.xml"));
			}

			if ("war".equals(project.getPackaging())) {
				sourceFolders.add(new File(baseDir, "src/main/webapp"));
			}

			Scanners.forDir().scan(baseDir, new FileMatcher() {
				@Override
				public Direction matches(File base, String path) {
					if (path.equals("src") || path.startsWith("src/")) {
						List<String> parts = Splitters.by('/').split(path);

						if (parts.size() == 3) {
							sourceFolders.add(new File(base, path));
							return Direction.MATCHED;
						} else {
							return Direction.DOWN;
						}
					}

					return Direction.NEXT;
				}
			});

			for (File sourceFolder : sourceFolders) {
				migrateDirectory(sourceFolder);
			}

			long timeUsed = System.currentTimeMillis() - start;

			getLog().info(String.format("%s files migrated in %s ms, with %s changed and %s failures.", //
			      m_success, timeUsed, m_changed, m_failure));
		} catch (Exception e) {
			String message = String.format("Error when migrating term from %s to %s!.", m_from, m_to);

			getLog().error(message);
			throw new MojoFailureException(message, e);
		}
	}

	protected void migrateDirectory(File dir) {
		migrateDirectory(dir, dir);
	}

	protected void migrateDirectory(File sourceDir, File targetDir) {
		String[] names = sourceDir.list();

		if (names != null) {
			for (String name : names) {
				File source = new File(sourceDir, name);
				File target;

				if (name.contains(m_from)) {
					target = new File(targetDir, replace(name, m_from, m_to));
				} else if (name.contains(m_from2)) {
					target = new File(targetDir, replace(name, m_from2, m_to2));
				} else {
					target = new File(targetDir, name);
				}

				if (source.isDirectory()) {
					migrateDirectory(source, target);
				} else if (source.isFile()) {
					migrateFile(source, target);
				}
			}
		}
	}

	protected void migrateFile(File source, File target) {
		try {
			String encoding = "utf-8";
			String original = Files.forIO().readFrom(source, encoding);
			String migrated;

			// for variable or parameter name
			migrated = replaceAll(original, m_from, m_to);

			// for Class or Method Name
			migrated = replaceAll(migrated, m_from2, m_to2);

			Files.forIO().writeTo(target, migrated, encoding);
			m_success++;

			boolean changed = !original.equals(migrated);

			if (changed) {
				m_changed++;
			}

			if (verbose && changed) {
				getLog().info(String.format("File(%s) migrated, length is %s.", target, target.length()));
			}
		} catch (Exception e) {
			getLog().warn(String.format("Error when migrating file(%s)!", source), e);
			m_failure++;
		}
	}

	private String replace(String str, String from, String to) {
		int pos = str.indexOf(from);

		if (pos >= 0) {
			return str.substring(0, pos) + to + str.substring(pos + from.length());
		} else {
			return str;
		}
	}

	protected String replaceAll(String source, String fromToken, String toToken) {
		int toLength = toToken.length();
		int fromLength = fromToken.length();
		StringBuilder sb = new StringBuilder(source.length() + (toLength < fromLength ? 0 : toLength - fromLength) * 30);
		int offset = 0;
		int index = source.indexOf(fromToken, offset);

		while (true) {
			if (index == -1) {
				sb.append(source.substring(offset));
				break;
			} else {
				sb.append(source.substring(offset, index));
				sb.append(toToken);

				offset = index + fromLength;
				index = source.indexOf(fromToken, offset);
			}
		}

		return sb.toString();
	}
}
