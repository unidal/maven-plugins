package org.unidal.maven.plugin.wizard.dom;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class JDomBuilder {
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

   public Document loadDocument(File xmlFile) throws JDOMException, IOException {
      return new SAXBuilder().build(xmlFile);
   }

   public Document loadDocument(String xml) throws JDOMException, IOException {
      return new SAXBuilder().build(new StringReader(xml));
   }

   public String selectAttribute(Parent parent, String xpath, Object... variables) throws JDOMException {
      XPath path = XPath.newInstance(xpath);

      setVariables(path, variables);

      Attribute attribute = (Attribute) path.selectSingleNode(parent);

      return attribute.getValue();
   }

   @SuppressWarnings("unchecked")
   public List<String> selectAttributes(Parent parent, String xpath, Object... variables) throws JDOMException {
      XPath path = XPath.newInstance(xpath);

      setVariables(path, variables);

      List<Attribute> attributes = path.selectNodes(parent);
      List<String> result = new ArrayList<String>();

      for (Attribute attribute : attributes) {
         result.add(attribute.getValue());
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   public <S, T> List<T> selectNodes(Parent parent, String xpath, Function<S, T> function, Object... variables)
         throws JDOMException {
      XPath path = XPath.newInstance(xpath);

      setVariables(path, variables);

      List<Object> nodes = path.selectNodes(parent);
      List<T> result = new ArrayList<T>();

      for (Object node : nodes) {
         if (function != null) {
            result.add(function.apply((S) node));
         } else {
            result.add((T) node);
         }
      }

      return result;
   }

   private void setVariables(XPath path, Object... variables) {
      int len = variables.length;

      if (len % 2 != 0) {
         throw new RuntimeException(String.format("Variables(%s) should be paired!", Arrays.asList(variables)));
      }

      for (int i = 0; i < len; i += 2) {
         path.setVariable(String.valueOf(variables[i]), variables[i + 1]);
      }
   }

   protected void setModified(boolean modified) {
      m_modifed = modified;
   }

   public static interface Function<S, T> {
      public T apply(S o);
   }
}
