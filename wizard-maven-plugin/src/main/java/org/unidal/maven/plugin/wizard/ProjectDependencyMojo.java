package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.IValidator;
import org.unidal.helper.Splitters;

/**
 * Add dependency to the project POM.
 * 
 * @goal project-dependency
 */
public class ProjectDependencyMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Dependency group id.
    * 
    * @parameter expression="${groupId}"
    */
   protected String groupId;

   /**
    * Dependency artifact id.
    * 
    * @parameter expression="${artifactId}"
    */
   protected String artifactId;

   /**
    * Dependency version.
    * 
    * @parameter expression="${version}"
    */
   protected String version;

   /**
    * Dependency scope.
    * 
    * @parameter expression="${scope}"
    */
   protected String scope;

   /**
    * Dependency is optional or not.
    * 
    * @parameter expression="${optional}"
    */
   protected Boolean optional;

   /**
    * Dependency with exclusions.
    * 
    * @parameter expression="${withExclusions}"
    */
   protected Boolean withExclusions;

   protected Document modifyPom(PomDelegate b) {
      Document doc = b.openMavenDocument(m_project.getFile());
      Element project = doc.getRootElement();
      Element dependencies = b.findOrCreateChild(project, "dependencies");

      b.checkDependency(dependencies, groupId, artifactId, version, scope);

      if (withExclusions.booleanValue()) {
         Element dependency = b.findDependency(dependencies, groupId, artifactId);

         while (true) {
            String id = PropertyProviders.fromConsole().forString("exclusions",
                  "Child dependency to exclude(groupId:artifactId, or 'end'):", null, null);

            if ("end".equals(id)) {
               break;
            } else {
               List<String> parts = Splitters.by(':').trim().split(id);

               if (parts.size() == 2) {
                  b.checkExclusion(dependency, parts.get(0), parts.get(1));
               }
            }
         }
      }

      return doc;
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      prepare();

      PomDelegate b = new PomDelegate();
      Document doc = modifyPom(b);

      try {
         if (b.isModified()) {
            saveXml(doc, m_project.getFile());
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Failed to generate pom.xml.", e);
      }
   }

   protected void prepare() {
      if (groupId == null) {
         groupId = PropertyProviders.fromConsole().forString("groupId", "Dependency group id:", null, null);
      }

      if (artifactId == null) {
         artifactId = PropertyProviders.fromConsole().forString("groupId", "Artifact id:", null, null);
      }

      if (version == null) {
         version = PropertyProviders.fromConsole().forString("groupId", "Version:", null, null);
      }

      if (scope == null) {
         scope = PropertyProviders.fromConsole().forString("scope", "Scope(compile, provided, runtime, test):", "compile",
               new IValidator<String>() {
                  private List<String> m_scopes = Arrays.asList("compile", "provided", "runtime", "test");

                  @Override
                  public boolean validate(String scope) {
                     return m_scopes.contains(scope);
                  }
               });

         if ("compile".equals(scope)) {
            scope = null;
         }
      }

      if (optional == null) {
         optional = PropertyProviders.fromConsole().forBoolean("optional", "Is optional?", false);
      }

      if (withExclusions == null) {
         withExclusions = PropertyProviders.fromConsole().forBoolean("withExclusions", "With exclusion? ", false);
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
