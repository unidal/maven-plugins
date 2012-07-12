package org.unidal.maven.plugin.wizard;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class PomBuilder {
   private static Namespace NS = Namespace.getNamespace("http://maven.apache.org/POM/4.0.0");

   private boolean m_pomModifed;

   @SuppressWarnings("unchecked")
   public boolean checkDependency(Element dependencies, String groupId, String artifactId, String version, String scope) {
      List<Element> children = dependencies.getChildren("dependency", NS);
      Element dependency = null;

      for (Element child : children) {
         String g = child.getChildText("groupId", NS);
         String a = child.getChildText("artifactId", NS);

         if (groupId.equals(g) && artifactId.equals(a)) {
            dependency = child;
            break;
         }
      }

      if (dependency == null) {
         dependency = new Element("dependency", NS);
         createChild(dependency, "groupId", groupId);
         createChild(dependency, "artifactId", artifactId);

         if (version != null) {
            createChild(dependency, "version", version);
         }

         if (scope != null) {
            createChild(dependency, "scope", scope);
         }

         dependencies.addContent(dependency);
         return false;
      } else {
         return true;
      }
   }

   @SuppressWarnings("unchecked")
   public Element checkPlugin(Element plugins, String groupId, String artifactId, String version) {
      List<Element> children = plugins.getChildren("plugin", NS);
      Element plugin = null;

      for (Element child : children) {
         String g = child.getChildText("groupId", NS);
         String a = child.getChildText("artifactId", NS);

         if ((groupId == null || groupId != null && groupId.equals(g)) && artifactId.equals(a)) {
            plugin = child;
            break;
         }
      }

      if (plugin == null) {
         plugin = new Element("plugin", NS);
         if (groupId != null) {
            createChild(plugin, "groupId", groupId);
         }

         createChild(plugin, "artifactId", artifactId);

         if (version != null) {
            createChild(plugin, "version", version);
         }

         plugins.addContent(plugin);
      }

      return plugin;
   }

   public Element checkPluginExecution(Element plugin, String goal, String phase, String id) {
      Element executions = findOrCreateChild(plugin, "executions");
      Element execution = createChild(executions, "execution", null);

      if (id != null) {
         createChild(execution, "id", id);
      }

      if (phase != null) {
         createChild(execution, "phase", phase);
      }

      if (goal != null) {
         Element goals = createChild(execution, "goals", null);

         createChild(goals, "goal", goal);
      }

      return execution;
   }

   public Element createChild(Element parent, String name, String value) {
      Element child = new Element(name, NS);

      if (value != null) {
         child.setText(value);
      }

      parent.addContent(child);
      m_pomModifed = true;
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

   public Element findOrCreateChild(Element parent, String name) {
      return findOrCreateChild(parent, name, null, null);
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

         m_pomModifed = true;
      }

      return child;
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

   public boolean isPomModified() {
      return m_pomModifed;
   }
}
