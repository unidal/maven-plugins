package org.unidal.maven.plugin.codegen.function;

public class Normalizer {
	public static String normalize(String str) {
		int len = str.length();
		StringBuilder sb = new StringBuilder(len);
		boolean upper = false;

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (ch == '-' || ch == '_' || ch == ':') {
				upper = true;
			} else {
				if (upper) {
					sb.append(Character.toUpperCase(ch));
					upper = false;
				} else {
					sb.append(ch);
				}
			}
		}

		return sb.toString();
	}
}
