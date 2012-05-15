package com.site.maven.plugin.project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * Generate a Maven project build plan (build-plan.xml)
 * 
 * @goal plan
 * 
 */
public class BuildPlanMojo extends AbstractMojo {
   /**
    * POM file
    * 
    * @parameter expression="${pom}" default-value="${basedir}/pom.xml"
    * @required
    */
   private File pomFile;
   /**
    * Build plan file
    * 
    * @parameter expression="${plan}" default-value="${basedir}/plan.xml"
    * @required
    */
   private File planFile;

   private static Namespace POM_NS = Namespace.getNamespace("pom", "http://maven.apache.org/POM/4.0.0");

   private Map<String, XPath> m_pathMap = new HashMap<String, XPath>();

   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         Document doc = new SAXBuilder().build(pomFile);
         XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat().setIndent("   "));
         FileWriter writer = new FileWriter(planFile);
         Element project = new Element("project");

         getBuildPlan(project, pomFile.getParentFile(), doc.getRootElement(), null);
         outputter.output(new Document().setRootElement(project), writer);
         writer.close();

         getLog().info("File " + planFile.getCanonicalPath() + " generated.");
      } catch (Exception e) {
         throw new MojoExecutionException("Failed when generating project build file, error: " + e, e);
      }
   }

   private void getBuildPlan(Element project, File baseDir, Element pom, String module) throws JDOMException,
         IOException {
      processProject(pom, project);

      if (module != null) {
         project.addContent(new Element("path").addContent(module));
      }

      processDependencies(pom, project);
      processDependencyManagement(pom, project);
      processModules(baseDir, pom, project);
   }

   private XPath getXPath(Element context, String pathWithNs) throws JDOMException {
      boolean hasNamespace = context.getNamespaceURI().length() > 0;
      String path = hasNamespace ? pathWithNs : pathWithNs.replaceAll("pom:", "");
      XPath xpath = m_pathMap.get(path);

      if (xpath == null) {
         xpath = XPath.newInstance(path);

         if (hasNamespace) {
            xpath.addNamespace(POM_NS);
         }

         m_pathMap.put(path, xpath);
      }

      return xpath;
   }

   private void processDependencies(Element pom, Element project) throws JDOMException {
      Element dependencies = new Element("dependencies");
      List<Element> children = selectElements(pom, "pom:dependencies/pom:dependency");

      if (children.isEmpty()) {
         return;
      }

      project.addContent(dependencies);

      for (Element child : children) {
         Element dependency = new Element("dependency");
         String groupId = selectString(child, "pom:groupId/text()");
         String artifactId = selectString(child, "pom:artifactId/text()");
         String version = selectString(child, "pom:version/text()");
         String scope = selectString(child, "pom:scope/text()");
         String type = selectString(child, "pom:type/text()");

         dependency.addContent(new Element("groupId").addContent(groupId));
         dependency.addContent(new Element("artifactId").addContent(artifactId));

         if (version != null) {
            dependency.addContent(new Element("version").addContent(version));
         }

         if (scope != null) {
            dependency.addContent(new Element("scope").addContent(scope));
         }

         if (type != null) {
            dependency.addContent(new Element("type").addContent(type));
         }

         dependencies.addContent(dependency);
      }
   }

   private void processDependencyManagement(Element pom, Element project) throws JDOMException {
      Element e = selectElement(pom, "pom:dependencyManagement");

      if (e != null) {
         Element dependencyManagement = new Element("dependencyManagement");

         processDependencies(e, dependencyManagement);

         if (!dependencyManagement.getChildren().isEmpty()) {
            project.addContent(dependencyManagement);
         }
      }
   }

   private void processModules(File baseDir, Element pom, Element enclosingProject) throws JDOMException, IOException {
      Element projects = new Element("projects");
      List<String> modules = selectStrings(pom, "pom:modules/pom:module/text()");

      if (modules.isEmpty()) {
         return;
      }

      enclosingProject.addContent(projects);

      for (String module : modules) {
         File projectBaseDir = new File(baseDir, module);
         File projectPomFile = new File(projectBaseDir, "pom.xml");

         if (!projectPomFile.exists()) {
            throw new RuntimeException("POM file(" + projectPomFile + ") of module(" + module + ") does not exist");
         }

         Element projectRootElement = new SAXBuilder().build(projectPomFile).getRootElement();
         Element project = new Element("project");

         projects.addContent(project);
         getBuildPlan(project, projectBaseDir, projectRootElement, module);
      }
   }

   private void processProject(Element pom, Element project) throws JDOMException {
      String parentGroupId = selectString(pom, "pom:parent/pom:groupId/text()");
      String parentArtifactId = selectString(pom, "pom:parent/pom:artifactId/text()");
      String parentVersion = selectString(pom, "pom:parent/pom:version/text()");
      String groupId = selectString(pom, "pom:groupId/text()");
      String artifactId = selectString(pom, "pom:artifactId/text()");
      String version = selectString(pom, "pom:version/text()");
      String packaging = selectString(pom, "pom:packaging/text()");
      String name = selectString(pom, "pom:name/text()");
      String description = selectString(pom, "pom:description/text()");

      project.setAttribute("artifactId", artifactId);

      if (groupId != null && !groupId.equals(parentGroupId)) {
         project.setAttribute("groupId", groupId);
      }

      if (version != null && !version.equals(parentVersion)) {
         project.setAttribute("version", version);
      }

      if (packaging == null) {
         project.setAttribute("packaging", "jar");
      } else {
         project.setAttribute("packaging", packaging);
      }

      project.addContent(new Element("name").addContent(name));
      Element enclosingProject = project.getParentElement();

      if (enclosingProject != null) {
         String enclosingGroupId = selectStringWithParent(enclosingProject, "@groupId");
         String enclosingArtifactId = selectStringWithParent(enclosingProject, "@artifactId");
         String enclosingVersion = selectStringWithParent(enclosingProject, "@version");

         if (enclosingGroupId != null && !enclosingGroupId.equals(parentGroupId) || enclosingArtifactId != null
               && !enclosingArtifactId.equals(parentArtifactId) || enclosingVersion != null
               && !enclosingVersion.equals(parentVersion)) {
            project.addContent(new Element("parent").addContent(parentGroupId + ":" + parentArtifactId + ":"
                  + parentVersion));
         }
      }

      if (description != null) {
         project.addContent(new Element("description").addContent(description));
      }
   }

   private Element selectElement(Element context, String path) throws JDOMException {
      XPath xpath = getXPath(context, path);
      Object result = xpath.selectSingleNode(context);

      if (result == null) {
         return null;
      } else if (result instanceof Element) {
         return (Element) result;
      } else {
         throw new RuntimeException("Invalid Element path: " + path + ", got: " + result);
      }
   }

   private String selectStringWithParent(Element context, String path) throws JDOMException {
      String result = selectString(context, path);

      if (result != null) {
         return result;
      } else if (context.getParentElement() == null) {
         return null;
      } else {
         return selectStringWithParent(context.getParentElement(), path);
      }
   }

   @SuppressWarnings("unchecked")
   private List<Element> selectElements(Element context, String path) throws JDOMException {
      XPath xpath = getXPath(context, path);
      Object result = xpath.selectNodes(context);

      if (result == null) {
         return null;
      } else if (result instanceof List) {
         return (List<Element>) result;
      } else {
         throw new RuntimeException("Invalid Element path: " + path + ", got: " + result);
      }
   }

   private String selectString(Element context, String path) throws JDOMException {
      XPath xpath = getXPath(context, path);
      Object result = xpath.selectSingleNode(context);

      if (result == null) {
         return null;
      } else if (result instanceof Text) {
         return ((Text) result).getText();
      } else if (result instanceof Attribute) {
         return ((Attribute) result).getValue();
      } else {
         throw new RuntimeException("Invalid String path: " + path + ", got: " + result);
      }
   }

   private List<String> selectStrings(Element context, String path) throws JDOMException {
      XPath xpath = getXPath(context, path);
      Object result = xpath.selectNodes(context);

      if (result == null) {
         return null;
      } else if (result instanceof List) {
         List<String> list = new ArrayList<String>();
         for (Object item : (List<?>) result) {
            list.add(((Text) item).getText());
         }
         return list;
      } else {
         throw new RuntimeException("Invalid Element path: " + path + ", got: " + result);
      }
   }
}
