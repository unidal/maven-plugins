package org.unidal.maven.plugin.wizard.dom;

import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class PomFileBuilder extends DocumentBuilder {
   private static Namespace NS = Namespace.getNamespace("http://maven.apache.org/POM/4.0.0");

   private Log m_log;

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

         if (m_log != null) {
            m_log.info(String.format("Dependency(%s:%s:%s) added.", groupId, artifactId, version));
         }

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
      Element execution = findPluginExecution(executions, id);

      if (execution == null) {
         execution = createChild(executions, "execution", null);

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
      }

      return execution;
   }

   public Document createMavenDocument() {
      Element root = new Element("project", getNamespace());
      Document doc = new Document(root);
      Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

      root.addNamespaceDeclaration(NS);
      root.setAttribute("schemaLocation", "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd", xsi);
      return doc;
   }

   @SuppressWarnings("unchecked")
   public Element findPluginExecution(Element executions, String id) {
      Element execution = null;

      for (Element e : (List<Element>) executions.getChildren()) {
         Element idElement = e.getChild("id", NS);

         if (id == null) {
            if (idElement == null || "default".equals(idElement.getValue())) {
               execution = e;
               break;
            }
         } else if (idElement != null && id.equals(idElement.getValue())) {
            execution = e;
            break;
         }
      }

      return execution;
   }

   @Override
   protected Namespace getNamespace() {
      return NS;
   }

   public PomFileBuilder setLog(Log log) {
      m_log = log;
      return this;
   }
}
