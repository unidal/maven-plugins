package org.unidal.maven.plugin.wizard.dom;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.unidal.maven.plugin.pom.DomAccessor;

public class WebXmlBuilder extends DomAccessor {
   private Namespace m_activeNamespace = Namespace.getNamespace("http://java.sun.com/xml/ns/javaee");

   @SuppressWarnings("unchecked")
   public Element findOrCreateFilter(Element parent, String filterName, String className) {
      List<Object> children = parent.getContent();
      int len = children.size();
      int lastIndex = -1;

      for (int i = 0; i < len; i++) {
         Object child = children.get(i);

         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("filter") && e.getNamespace().equals(m_activeNamespace)) {
               lastIndex = i;

               Element fn = findElement(e, "filter-name", filterName);

               if (fn != null) {
                  return e;
               }
            }
         }
      }

      Element e = new Element("filter", m_activeNamespace) //
            .addContent(new Element("filter-name", m_activeNamespace).addContent(filterName)) //
            .addContent(new Element("filter-class", m_activeNamespace).addContent(className));

      parent.addContent(lastIndex + 1, e);

      return e;
   }

   @SuppressWarnings("unchecked")
   public Element findOrCreateFilterMapping(Element parent, String filterName, String urlPattern, String... dispatchers) {
      List<Object> children = parent.getContent();
      int len = children.size();
      int lastIndex = -1;

      for (int i = 0; i < len; i++) {
         Object child = children.get(i);

         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("filter-mapping") && e.getNamespace().equals(m_activeNamespace)) {
               lastIndex = i;

               Element fn = findElement(e, "filter-name", filterName);

               if (fn != null) {
                  return e;
               }
            }
         }
      }

      Element e = new Element("filter-mapping", m_activeNamespace) //
            .addContent(new Element("filter-name", m_activeNamespace).addContent(filterName)) //
            .addContent(new Element("url-pattern", m_activeNamespace).addContent(urlPattern));

      for (String dispatcher : dispatchers) {
         e.addContent(new Element("dispatcher", m_activeNamespace).addContent(dispatcher));
      }

      parent.addContent(lastIndex + 1, e);

      return e;
   }

   @SuppressWarnings("unchecked")
   public Element findOrCreateListener(Element parent, String value) {
      Element listener = findOrCreateChild(parent, "listener", 0);
      List<Object> children = listener.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("listener-class") && e.getNamespace().equals(m_activeNamespace) && value.equals(e.getValue())) {
               return e;
            }
         }
      }

      Element e = new Element("listener-class", m_activeNamespace).addContent(value);

      listener.addContent(e);
      return e;
   }

   @Override
   protected Namespace getNamespace() {
      return m_activeNamespace;
   }

   public void setActiveNamespace(Namespace namespace) {
      m_activeNamespace = namespace;
   }
}
