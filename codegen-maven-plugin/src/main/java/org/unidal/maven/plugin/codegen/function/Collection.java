package org.unidal.maven.plugin.codegen.function;

import java.util.ArrayList;
import java.util.List;

public class Collection {
	private static List<String> s_values = new ArrayList<String>();

	public static boolean addIfAbsent(String value) {
		if (value == null || value.length() == 0) {
			return false;
		} else if (s_values.contains(value)) {
			return false;
		} else {
			s_values.add(value);
			return true;
		}
	}
	
	public static void reset() {
		s_values.clear();
	}
}
