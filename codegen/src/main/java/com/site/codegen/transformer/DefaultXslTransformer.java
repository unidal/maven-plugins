package com.site.codegen.transformer;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.site.codegen.template.XslTemplateManager;

public class DefaultXslTransformer implements XslTransformer {
   private XslTemplateManager m_templateManager;

   private void setParameters(Transformer transformer, Map<String, String> parameters) {
      if (parameters != null) {
         for (Map.Entry<String, String> e : parameters.entrySet()) {
            transformer.setParameter(e.getKey(), e.getValue());
         }
      }
   }

   public String transform(URL template, String source) throws TransformerException {
      return transform(template, source, null);
   }

   public String transform(URL template, String source, Map<String, String> parameters) throws TransformerException {
      Transformer transformer = m_templateManager.getTemplates(template).newTransformer();
      StringReader reader = new StringReader(source);
      StringWriter buffer = new StringWriter(64 * 1024);

      setParameters(transformer, parameters);
      transformer.transform(new StreamSource(reader), new StreamResult(buffer));

      return buffer.toString();
   }
}
