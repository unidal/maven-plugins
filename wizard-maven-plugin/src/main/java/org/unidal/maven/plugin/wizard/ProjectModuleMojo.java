package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.wizard.dom.PomXmlBuilder;

/**
 * Create an empty module project POM.
 * 
 * @goal project-module
 * @aggregator
 */
public class ProjectModuleMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Project group id.
    * 
    * @parameter expression="${groupId}"
    */
   protected String groupId;

   /**
    * Project artifact id.
    * 
    * @parameter expression="${artifactId}"
    */
   protected String artifactId;

   /**
    * Project version.
    * 
    * @parameter expression="${version}"
    */
   protected String version;

   /**
    * Project name.
    * 
    * @parameter expression="${name}"
    */
   protected String name;

   /**
    * Project packaging(pom, jar, war).
    * 
    * @parameter expression="${packaging}" default-value="jar"
    */
   protected String packaging;

   protected Document createModulePom() {
      PomXmlBuilder b = new PomXmlBuilder();
      Document doc = b.createMavenDocument();
      Element project = doc.getRootElement();
      Element parent = b.findOrCreateChild(project, "parent");

      b.createChild(parent, "groupId", m_project.getGroupId());
      b.createChild(parent, "artifactId", m_project.getArtifactId());
      b.createChild(parent, "version", m_project.getVersion());
      b.createChild(parent, "relativePath", "../pom.xml");

      b.createChild(project, "modelVersion", "4.0.0");

      if (!groupId.equals(m_project.getGroupId())) {
         b.createChild(project, "groupId", groupId);
      }

      b.createChild(project, "artifactId", artifactId);

      if (version.length() > 0 && !version.equals(m_project.getVersion())) {
         b.createChild(project, "version", version);
      }

      b.createChild(project, "name", name);

      if (!packaging.equals("jar")) {
         b.createChild(project, "packaging", packaging);
      }

      return doc;
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (!"pom".equals(m_project.getPackaging())) {
         throw new MojoFailureException("The project is not an POM project!");
      }

      prepare();

      File dir = new File(m_project.getBasedir(), artifactId);

      if (dir.exists() && new File(dir, "pom.xml").exists()) {
         throw new MojoFailureException(String.format("The project(%s) is already existed!", artifactId));
      }

      Document parentDoc = modifyParentPom();
      Document doc = createModulePom();

      try {
         saveXml(doc, new File(m_project.getBasedir(), artifactId + "/pom.xml"));
         saveXml(parentDoc, m_project.getFile());
      } catch (Exception e) {
         throw new MojoExecutionException("Failed to generate pom.xml.", e);
      }
   }

   protected String getProjectName(String artifactId) {
      StringBuilder sb = new StringBuilder(artifactId.length());
      StringTokenizer st = new StringTokenizer(artifactId, "- ");

      while (st.hasMoreTokens()) {
         String part = st.nextToken();

         if (part.length() > 0) {
            if (sb.length() > 0) {
               sb.append(' ');
            }

            sb.append(Character.toUpperCase(part.charAt(0)) + part.substring(1));
         }
      }

      return sb.toString();
   }

   protected Document modifyParentPom() {
      PomXmlBuilder b = new PomXmlBuilder();
      Document doc = b.openMavenDocument(m_project.getFile());
      Element project = doc.getRootElement();
      Element modules = b.findOrCreateChild(project, "modules", "build", null);

      b.createChild(modules, "module", artifactId);
      return doc;
   }

   protected void prepare() {
      if (groupId == null) {
         groupId = PropertyProviders.fromConsole().forString("groupId", "Project group id:", m_project.getGroupId(),
               null);
      }

      if (artifactId == null) {
         artifactId = PropertyProviders.fromConsole().forString("groupId", "Project artifact id:", null, null);
      }

      if (version == null) {
         version = PropertyProviders.fromConsole().forString("groupId", "Project version:", "", null);
      }

      if (name == null) {
         name = PropertyProviders.fromConsole().forString("groupId", "Project name:", getProjectName(artifactId), null);
      }
   }

   protected void saveXml(Document doc, File file) throws IOException {
      File parent = file.getCanonicalFile().getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(doc, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }
}
