package org.unidal.codegen.template;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.unidal.lookup.annotation.Named;

@Named(type = XslTemplateManager.class)
public class DefaultXslTemplateManager implements XslTemplateManager {
	private Map<URL, Templates> m_cachedTemplates;

	private Map<URL, Long> m_lastModifiedDates;

	public DefaultXslTemplateManager() {
		m_cachedTemplates = new HashMap<URL, Templates>();
		m_lastModifiedDates = new HashMap<URL, Long>();
	}

	public Templates getTemplates(URL style) {
		Templates templates = m_cachedTemplates.get(style);
		Long lastModifiedDate = m_lastModifiedDates.get(style);
		long lastModified = 0;

		if ("file".equals(style.getProtocol())) {
			lastModified = new File(style.getFile()).lastModified();
		}

		if (templates == null || lastModifiedDate.longValue() != lastModified) {
			TransformerFactory factory = TransformerFactory.newInstance();

			try {
				factory.setURIResolver(new URIResolver() {
					public Source resolve(String href, String base) throws TransformerException {
						try {
							URL uri = new URL(new URL(base), href);

							return new StreamSource(uri.openStream(), uri.toString());
						} catch (Exception e) {
							// ignore it
							System.err.println("Can't result URI (" + base + "," + href + ") " + e);
						}

						// let the processor to resolve the URI itself
						return null;
					}
				});

				templates = factory.newTemplates(new StreamSource(style.openStream(), style.toString()));

				m_cachedTemplates.put(style, templates);
				m_lastModifiedDates.put(style, new Long(lastModified));
			} catch (Exception e) {
				throw new RuntimeException("Fail to open XSL template: " + style, e);
			}
		}

		return templates;
	}
}
