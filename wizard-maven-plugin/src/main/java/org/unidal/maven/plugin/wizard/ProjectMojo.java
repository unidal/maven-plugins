package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.wizard.dom.PomFileBuilder;

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

   protected File m_pomFile = new File("pom.xml");

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (m_pomFile.exists()) {
         getLog().warn("pom.xml file is already existed! SKIPPED");
         return;
      }

      prepare();

      PomFileBuilder b = new PomFileBuilder();
      Document doc = b.createMavenDocument();
      Element project = doc.getRootElement();

      b.createChild(project, "modelVersion", "4.0.0");
      b.createChild(project, "groupId", groupId);
      b.createChild(project, "artifactId", artifactId);
      b.createChild(project, "version", version);
      b.createChild(project, "name", name);

      try {
         saveXml(doc, m_pomFile);
      } catch (Exception e) {
         throw new MojoExecutionException("Failed to generate pom.xml.", e);
      }
   }

   protected void prepare() {
      if (groupId == null) {
         groupId = PropertyProviders.fromConsole().forString("groupId", "Project group id:", null, null);
      }

      if (artifactId == null) {
         artifactId = PropertyProviders.fromConsole().forString("groupId", "Project artifact id:", null, null);
      }

      if (version == null) {
         version = PropertyProviders.fromConsole().forString("groupId", "Project version:", null, null);
      }

      if (name == null) {
         name = PropertyProviders.fromConsole().forString("groupId", "Project name:", artifactId, null);
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
