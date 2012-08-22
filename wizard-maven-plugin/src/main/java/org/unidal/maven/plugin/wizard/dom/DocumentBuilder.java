package org.unidal.maven.plugin.wizard.dom;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;

public class DocumentBuilder {
   private boolean m_modifed;

   public Element createChild(Element parent, String name, String value) {
      Element child = new Element(name, getNamespace());

      if (value != null) {
         child.setText(value);
      }

      parent.addContent(child);
      m_modifed = true;
      return child;
   }

   @SuppressWarnings("unchecked")
   public Element findElement(Element parent, String name, String value) {
      List<Object> children = parent.getContent();

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals(name) && e.getNamespace().equals(getNamespace()) && value.equals(e.getValue())) {
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
      Element child = parent.getChild(name, getNamespace());

      if (child == null) {
         child = new Element(name, getNamespace());

         parent.addContent(index, child);
         m_modifed = true;
      }

      return child;
   }

   public Element findOrCreateChild(Element parent, String name, String beforeElement, String afterElement) {
      Element child = parent.getChild(name, getNamespace());

      if (child == null) {
         int index = -1;

         child = new Element(name, getNamespace());

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

   protected Namespace getNamespace() {
      return null;
   }

   @SuppressWarnings("unchecked")
   public int indexOfElement(Element parent, String name) {
      List<Object> children = parent.getContent();
      int index = 0;

      for (Object child : children) {
         if (child instanceof Element) {
            Element e = (Element) child;

            if (e.getName().equals(name) && e.getNamespace().equals(getNamespace())) {
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

            if (e.getName().equals(name) && e.getNamespace().equals(getNamespace())) {
               lastIndex = i;
            }
         }
      }

      return lastIndex;
   }

   public boolean isModified() {
      return m_modifed;
   }

   protected void setModified(boolean modified) {
      m_modifed = modified;
   }
}
