package org.unidal.maven.plugin.wizard;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class WebFileBuilder {
   private static Namespace NS = Namespace.getNamespace("http://java.sun.com/xml/ns/javaee");

   private boolean m_modifed;

   public Element createChild(Element parent, String name, String value) {
      Element child = new Element(name, NS);

      if (value != null) {
         child.setText(value);
      }

      parent.addContent(child);
      m_modifed = true;
      return child;
   }

   public Document createDocument() {
      Element root = new Element("project", NS);
      Document doc = new Document(root);
      Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

      root.addNamespaceDeclaration(NS);
      root.setAttribute("schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd", xsi);
      return doc;
   }

   @SuppressWarnings("unchecked")
   public Element findElement(Element parent, String name, String value) {
      List<Object> children = parent.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals(name) && e.getNamespace().equals(NS) && value.equals(e.getValue())) {
               return e;
            }
         }
      }

      return null;
   }

   public Element findOrCreateChild(Element parent, String name) {
      return findOrCreateChild(parent, name, null, null);
   }

   public Element findOrCreateChild(Element parent, String name, int index) {
      Element child = parent.getChild(name, NS);

      if (child == null) {
         child = new Element(name, NS);

         parent.addContent(index, child);
         m_modifed = true;
      }

      return child;
   }

   public Element findOrCreateChild(Element parent, String name, String beforeElement, String afterElement) {
      Element child = parent.getChild(name, NS);

      if (child == null) {
         int index = -1;

         child = new Element(name, NS);

         if (beforeElement != null) {
            index = indexOfElement(parent, beforeElement);
         } else if (afterElement != null) {
            index = indexOfElement(parent, afterElement);

            if (index >= 0) {
               index++;
            }
         }

         if (index < 0) {
            parent.addContent(child);
         } else {
            parent.addContent(index, child);
         }

         m_modifed = true;
      }

      return child;
   }

   @SuppressWarnings("unchecked")
   public Element findOrCreateFilter(Element parent, String filterName, String className) {
      List<Object> children = parent.getContent();
      int len = children.size();
      int lastIndex = -1;

      for (int i = 0; i < len; i++) {
         Object child = children.get(i);

         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("filter") && e.getNamespace().equals(NS)) {
               lastIndex = i;

               Element fn = findElement(e, "filter-name", filterName);

               if (fn != null) {
                  return e;
               }
            }
         }
      }

      Element e = new Element("filter", NS) //
            .addContent(new Element("filter-name", NS).addContent(filterName)) //
            .addContent(new Element("filter-class", NS).addContent(className));

      if (lastIndex < 0) {
         parent.addContent(e);
      } else {
         parent.addContent(lastIndex, e);
      }

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

            if (e.getName().equals("filter-mapping") && e.getNamespace().equals(NS)) {
               lastIndex = i;

               Element fn = findElement(e, "filter-name", filterName);

               if (fn != null) {
                  return e;
               }
            }
         }
      }

      Element e = new Element("filter-mapping", NS) //
            .addContent(new Element("filter-name", NS).addContent(filterName)) //
            .addContent(new Element("url-pattern", NS).addContent(urlPattern));

      for (String dispatcher : dispatchers) {
         e.addContent(new Element("dispatcher", NS).addContent(dispatcher));
      }

      if (lastIndex < 0) {
         parent.addContent(e);
      } else {
         parent.addContent(lastIndex, e);
      }

      return e;
   }

   @SuppressWarnings("unchecked")
   public Element findOrCreateListener(Element parent, String value) {
      Element listener = findOrCreateChild(parent, "listener", 0);
      List<Object> children = listener.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals("listener-class") && e.getNamespace().equals(NS) && value.equals(e.getValue())) {
               return e;
            }
         }
      }

      Element e = new Element("listener-class", NS).addContent(value);

      listener.addContent(e);
      return e;
   }

   @SuppressWarnings("unchecked")
   public int indexOfElement(Element parent, String name) {
      List<Object> children = parent.getContent();
      int index = 0;

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals(name) && e.getNamespace().equals(NS)) {
               return index;
            }
         }

         index++;
      }

      return -1;
   }

   @SuppressWarnings("unchecked")
   public int indexOfLastElement(Element parent, String name) {
      List<Object> children = parent.getContent();
      int len = children.size();
      int lastIndex = -1;

      for (int i = 0; i < len; i++) {
         Object child = children.get(i);

         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals(name) && e.getNamespace().equals(NS)) {
               lastIndex = i;
            }
         }
      }

      return lastIndex;
   }

   public boolean isWebFileModified() {
      return m_modifed;
   }
}
