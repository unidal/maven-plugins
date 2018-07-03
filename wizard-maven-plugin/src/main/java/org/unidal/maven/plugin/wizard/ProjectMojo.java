package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.maven.plugin.pom.PomDelegate;

/**
 * Create an empty project POM.
 * 
 * @goal project
 * @requiresProject false
 */
public class ProjectMojo extends AbstractMojo {
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

   // for test purpose
   protected File m_pomFile = new File("pom.xml");

   protected Document createPom() {
      PomDelegate b = new PomDelegate();
      Document doc = b.createMavenDocument();
      Element project = doc.getRootElement();

      b.createChild(project, "modelVersion", "4.0.0");
      b.createChild(project, "groupId", groupId);
      b.createChild(project, "artifactId", artifactId);
      b.createChild(project, "version", version);
      b.createChild(project, "name", name);
      b.createChild(project, "packaging", packaging);
      return doc;
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (m_pomFile.exists()) {
         getLog().warn("pom.xml file is already existed! SKIPPED");
         return;
      }

      prepare();

      Document doc = createPom();

      try {
         saveXml(doc, m_pomFile);
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

   protected void prepare() {
      if (groupId == null) {
         groupId = PropertyProviders.fromConsole().forString("groupId", "Project group id:", null, null);
      }

      if (artifactId == null) {
         artifactId = PropertyProviders.fromConsole().forString("artifactId", "Project artifact id:", null, null);
      }

      if (version == null) {
         version = PropertyProviders.fromConsole().forString("version", "Project version:", null, null);
      }

      if (name == null) {
         name = PropertyProviders.fromConsole().forString("name", "Project name:", getProjectName(artifactId), null);
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
