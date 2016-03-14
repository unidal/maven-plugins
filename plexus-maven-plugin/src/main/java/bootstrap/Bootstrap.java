package bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Scanners.ZipEntryMatcher;
import org.unidal.helper.Splitters;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		String cp = System.getProperty("java.class.path");
		File work = new File("work");

		bootstrap.setup(work, cp);
		bootstrap.startup(work, args);
		bootstrap.waitForAnyKey();
	}

	void hackClassLoader(List<String> entries) throws IOException {
		ClassLoader cl = getClass().getClassLoader();
		boolean hacked = false;

		if (cl instanceof URLClassLoader) {
			Object ucp = Reflects.forField().getDeclaredFieldValue(URLClassLoader.class, "ucp", cl);

			for (String entry : entries) {
				Reflects.forMethod().invokeMethod(ucp, "addURL", URL.class, new File(entry).toURI().toURL());
				hacked = true;
			}
		}

		if (!hacked) {
			throw new IllegalStateException("Hacking classloader failed!");
		}
	}

	public List<String> setup(File work, String classPath) throws IOException {
		char psc = File.pathSeparatorChar;
		List<String> paths = Splitters.by(psc).noEmptyItem().trim().split(classPath);
		final List<String> entries = new ArrayList<String>(100);

		for (String path : paths) {
			if (path.endsWith(".war")) {
				unzipWar(work, new File(path));
				entries.add("WEB-INF/classes");

				Scanners.forDir().scan(new File(work, "WEB-INF/lib"), new FileMatcher() {
					@Override
					public Direction matches(File base, String path) {
						entries.add("work/WEB-INF/lib/" + path);

						return Direction.DOWN;
					}
				});

				Scanners.forDir().scan(new File(work, "WEB-INF/ext"), new FileMatcher() {
					@Override
					public Direction matches(File base, String path) {
						entries.add("work/WEB-INF/ext/" + path);

						return Direction.DOWN;
					}
				});

				hackClassLoader(entries);
			}
		}

		return entries;
	}

	public void startup(File work, String[] args) throws IOException {
		InputStream in = new FileInputStream(new File(work, "META-INF/MANIFEST.MF"));
		Manifest manifest = new Manifest(in);

		in.close();

		// find the actual main class
		Attributes a = manifest.getMainAttributes();
		String mainClassName = a.getValue("X-Main-Class");

		try {
			Class<?> mainClass = Class.forName(mainClassName);

			// pass the work directory
			System.setProperty("warRoot", work.getPath());

			// invoke main class
			Reflects.forMethod().invokeStaticMethod(mainClass, "main", String[].class, args);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Unable to load class " + mainClassName + "!", e);
		}
	}

	void unzipWar(final File warRoot, File warFile) throws IOException {
		// clean or create war root directory
		if (warRoot.exists()) {
			Files.forDir().delete(warRoot, true);
		} else {
			warRoot.getCanonicalFile().mkdirs();
		}

		// extract all files to war root
		final ZipFile zipFile = new ZipFile(warFile);

		Scanners.forJar().scan(zipFile, new ZipEntryMatcher() {
			@Override
			public Direction matches(ZipEntry entry, String path) {
				try {
					InputStream in = zipFile.getInputStream(entry);
					File file = new File(warRoot, path);

					file.getParentFile().mkdirs();
					Files.forIO().copy(in, new FileOutputStream(file));
				} catch (IOException e) {
					System.err.println(e);
				}

				return Direction.DOWN;
			}
		});
	}

	public void waitForAnyKey() throws IOException {
		String timestamp = new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date());

		System.out.println(String.format("[%s] [INFO] Press any key to stop server ... ", timestamp));
		System.in.read();
	}
}
