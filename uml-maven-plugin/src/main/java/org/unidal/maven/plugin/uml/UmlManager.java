package org.unidal.maven.plugin.uml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.SourceStringReader;

import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;

public class UmlManager {
	public byte[] generateImage(String uml, String type) throws IOException {
		if (!uml.trim().startsWith("@startuml")) {
			uml = "@startuml\n" + uml;
		}

		if (!uml.trim().endsWith("@enduml")) {
			uml = uml + "\n@enduml";
		}

		SourceStringReader reader = new SourceStringReader(uml);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
		FileFormat format = FileFormat.PNG;

		for (FileFormat e : FileFormat.values()) {
			if (e.name().equalsIgnoreCase(type)) {
				format = e;
				break;
			}
		}

		reader.generateImage(baos, new FileFormatOption(format));

		if (!hasError(reader.getBlocks())) {
			return baos.toByteArray();
		} else {
			return null;
		}
	}

	public String getContextType(String type) {
		if (type == null || "png".equals(type)) {
			return "image/png";
		} else if ("text".equals(type)) {
			return "text/plain; charset=utf-8";
		} else if ("svg".equals(type)) {
			return "image/svg+xml; charset=utf-8";
		} else {
			return "image/" + type;
		}
	}

	public String getImageType(String type) {
		if (type == null) {
			return "png";
		} else if ("text".equals(type)) {
			return "svg";
		} else {
			return type;
		}
	}

	private boolean hasError(List<BlockUml> blocks) throws IOException {
		for (BlockUml b : blocks) {
			if (b.getDiagram() instanceof PSystemError) {
				return true;
			}
		}

		return false;
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public List<File> scanUmlFiles() {
		final List<File> files = new ArrayList<File>();

		FileMatcher matcher = new FileMatcher() {
			@Override
			public Direction matches(File base, String path) {
				if (path.endsWith(".uml")) {
					files.add(new File(base, path));
				}

				return Direction.DOWN;
			}
		};

		Scanners.forDir().scan(new File("src"), matcher);
		Scanners.forDir().scan(new File("doc"), matcher);

		return files;
	}

	public boolean tryCreateFile(String umlFile) {
		File file = new File(umlFile);

		if (file.exists()) {
			return false;
		} else {
			file.getParentFile().mkdirs();

			try {
				FileOutputStream fos = new FileOutputStream(file);

				fos.close();
				return true;
			} catch (IOException e) {
				return false;
			}
		}
	}

	public boolean updateUml(String umlFile, String uml, StringBuilder message)
			throws IOException {
		if (!isEmpty(umlFile) && !isEmpty(uml)) {
			File file = new File(umlFile);
			byte[] image = generateImage(uml, null);

			try {
				if (image != null) {
					Files.forIO().writeTo(file, uml);

					message.append("Update file(" + umlFile + ") successfully!");
					return true;
				} else {
					message.append("UML is invalid, can't update file("
							+ umlFile + ")!");
				}
			} catch (IOException e) {
				message.append("Failed to update file(" + umlFile + ")!");
			}
		}

		return false;
	}
}
