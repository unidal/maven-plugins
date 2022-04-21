package org.unidal.codegen.framework;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.unidal.lookup.annotation.Named;

@Named(type = XslTransformer.class)
public class DefaultXslTransformer implements XslTransformer {
   private Map<URL, Templates> m_cachedTemplates = new HashMap<URL, Templates>();

   private Map<URL, Long> m_lastModifiedDates = new HashMap<URL, Long>();

   private Templates getTemplates(URL style) {
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
                     System.err.println("Can't open XSL template (" + base + "," + href + ") " + e);
                  }

                  // let the processor to resolve the URI itself
                  return null;
               }
            });

            templates = factory.newTemplates(new StreamSource(style.openStream(), style.toString()));

            m_cachedTemplates.put(style, templates);
            m_lastModifiedDates.put(style, Long.valueOf(lastModified));
         } catch (Exception e) {
            throw new IllegalStateException("Fail to open XSL template: " + style, e);
         }
      }

      return templates;
   }

   private void setParameters(Transformer transformer, Map<String, String> parameters) {
      if (parameters != null) {
         for (Map.Entry<String, String> e : parameters.entrySet()) {
            transformer.setParameter(e.getKey(), e.getValue());
         }
      }
   }

   public String transform(URL template, String source, Map<String, String> parameters) throws TransformerException {
      Transformer transformer = getTemplates(template).newTransformer();
      StringReader reader = new StringReader(source);
      StringWriter buffer = new StringWriter(64 * 1024);

      setParameters(transformer, parameters);
      transformer.transform(new StreamSource(reader), new StreamResult(buffer));

      return buffer.toString();
   }
}
