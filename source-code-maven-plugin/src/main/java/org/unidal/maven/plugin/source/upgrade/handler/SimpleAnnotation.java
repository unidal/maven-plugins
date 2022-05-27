package org.unidal.maven.plugin.source.upgrade.handler;

import java.util.LinkedHashMap;
import java.util.Map;

class SimpleAnnotation {
	private String m_name;

	private Map<String, String> m_attributes = new LinkedHashMap<>();

	public SimpleAnnotation(String str) {
		int len = str.length();
		int flag = 0; // 0: begin, 1: name, 2: attributes, 3: end
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			switch (ch) {
			case '@':
				if (flag == 0) {
					flag++;
				} else {
					sb.append(ch);
				}

				break;
			case '(':
				if (flag == 1) {
					flag++;
					m_name = sb.toString().trim();
					sb.setLength(0);
				} else {
					sb.append(ch);
				}

				break;
			case ')':
			case ',':
				if (flag == 2) {
					String part = sb.toString();
					int pos = part.indexOf('=');

					if (pos > 0) {
						m_attributes.put(part.substring(0, pos).trim(), part.substring(pos + 1).trim());
					} else {
						m_attributes.put("value", part);
					}

					sb.setLength(0);

					if (ch == ')' && flag == 2) {
						flag++;
					}

				} else {
					sb.append(ch);
				}

				break;
			default:
				sb.append(ch);
			}
		}

		if (sb.length() > 0 && m_name == null) {
			m_name = sb.toString().trim();
		}
	}

	public boolean getBoolean(String attribute, boolean defaultValue) {
		String value = m_attributes.get(attribute);

		if (value != null) {
			return "true".equals(value);
		} else {
			return defaultValue;
		}
	}

	public String getName() {
		return m_name;
	}

	public String getString(String attribute, String defaultValue) {
		String value = m_attributes.get(attribute);

		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public String toString() {
		return String.format("@%s%s", m_name, m_attributes);
	}
}