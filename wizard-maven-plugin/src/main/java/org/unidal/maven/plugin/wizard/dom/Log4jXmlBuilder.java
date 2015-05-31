package org.unidal.maven.plugin.wizard.dom;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

public class Log4jXmlBuilder extends JDomBuilder {
   @SuppressWarnings("unchecked")
   public boolean checkAppenderRef(Element parent, String ref) {
      List<Object> children = parent.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("appender-ref") && ref.equals(e.getAttributeValue("ref"))) {
               return true;
            }
         }
      }

      parent.addContent(new Element("appender-ref").setAttribute("ref", ref));
      setModified(true);
      return false;
   }

   @SuppressWarnings("unchecked")
   public void checkAppenderRefForLoggers(Element parent, String ref) {
      List<Object> children = parent.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if ("logger".equals(e.getName()) && "false".equals(e.getAttributeValue("additivity"))) {
               checkAppenderRef(e, ref);
            }
         }
      }
   }

   public boolean checkAppenderRefForRoot(Element parent, String ref) {
      Element root = findOrCreateChild(parent, "root");

      return checkAppenderRef(root, ref);
   }

   @Override
   protected Namespace getNamespace() {
      return null;
   }
}
