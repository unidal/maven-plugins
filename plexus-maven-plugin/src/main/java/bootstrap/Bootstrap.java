package bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Bootstrap {
	public static void main(String[] args) throws Exception {
		Bootstrap bootstrap = new Bootstrap();
		String cp = System.getProperty("java.class.path");
		File work = new File("work");

		work.mkdirs();

		bootstrap.setup(work, cp);
		bootstrap.startup(work, args);
	}

	void hackClassLoader(List<String> entries) throws Exception {
		ClassLoader cl = getClass().getClassLoader();
		boolean hacked = false;

		if (cl instanceof URLClassLoader) {
			Field field = URLClassLoader.class.getDeclaredField("ucp");

			field.setAccessible(true);

			Object ucp = field.get(cl);
			Method method = ucp.getClass().getMethod("addURL", URL.class);

			method.setAccessible(true);

			for (String entry : entries) {
				URL url = new File(entry).toURI().toURL();

				method.invoke(ucp, url);
				hacked = true;
			}
		}

		if (!hacked) {
			throw new IllegalStateException("Hacking classloader failed!");
		}
	}

	public List<String> setup(File work, String classPath) throws Exception {
		char psc = File.pathSeparatorChar;
		List<String> paths = split(classPath, psc);
		final List<String> entries = new ArrayList<String>(100);

		for (String path : paths) {
			if (path.endsWith(".jar")) {
				String[] dirs = { "WEB-INF/ext", "WEB-INF/lib" };

				unzipWar(work, new File(path));
				entries.add("work/WEB-INF/classes");

				for (String dir : dirs) {
					File base = new File(work, dir);
					String[] names = base.list();

					if (names != null) {
						for (String name : names) {
							entries.add("work/" + dir + "/" + name);
						}
					}
				}

				hackClassLoader(entries);
			}
		}

		return entries;
	}

	private List<String> split(String str, char delimiter) {
		int len = str.length();
		StringBuilder sb = new StringBuilder(len);
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < len + 1; i++) {
			char ch = i == len ? delimiter : str.charAt(i);

			if (ch == delimiter) {
				String item = sb.toString();

				sb.setLength(0);

				if (item.length() == 0) {
					continue;
				}

				list.add(item);
			} else {
				sb.append(ch);
			}
		}

		return list;
	}

	public void startup(File work, String[] args) throws IOException {
		InputStream in = new FileInputStream(new File(work, "META-INF/MANIFEST.MF"));
		Manifest manifest = new Manifest(in);

		in.close();

		// find the actual main class
		Attributes a = manifest.getMainAttributes();
		String mainClassName = a.getValue("X-Main-Class");

		System.out.println(getClass().getClassLoader().getClass());

		try {
			Class<?> mainClass = Class.forName(mainClassName);

			// pass the work directory
			System.setProperty("warRoot", work.getPath());

			// invoke main class
			Method method = mainClass.getMethod("main", String[].class);

			method.invoke(null, (Object) args);
		} catch (Exception e) {
			throw new IllegalStateException("Unable to load class " + mainClassName + "!", e);
		}
	}

	void unzipWar(final File warRoot, File warFile) throws IOException {
		// clean or create war root directory
		if (!warRoot.exists()) {
			warRoot.getCanonicalFile().mkdirs();
		}

		// extract all files to war root
		final ZipFile zipFile = new ZipFile(warFile);

		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();

			try {
				InputStream in = zipFile.getInputStream(entry);
				File file = new File(warRoot, name);

				file.getParentFile().mkdirs();

				FileOutputStream out = new FileOutputStream(file);
				byte[] content = new byte[4096];

				try {
					while (true) {
						int size = in.read(content);

						if (size == -1) {
							break;
						} else {
							out.write(content, 0, size);
						}
					}
				} finally {
					in.close();
					out.close();
				}
			} catch (IOException e) {
				System.err.println(e);
			}
		}

		zipFile.close();
	}
}
