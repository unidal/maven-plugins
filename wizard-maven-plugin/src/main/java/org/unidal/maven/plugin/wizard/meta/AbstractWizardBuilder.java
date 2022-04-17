package org.unidal.maven.plugin.wizard.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Splitters;
import org.unidal.maven.plugin.wizard.model.WizardHelper;
import org.unidal.maven.plugin.wizard.model.entity.Manifest;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.xml.sax.SAXException;

abstract class AbstractWizardBuilder implements LogEnabled {
   private MavenProject m_project;

   private Logger m_logger;

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   private String getWizardPackageName() {
      String defaultName = guessPackageName();

      if (defaultName == null) {
         List<String> parts = new ArrayList<String>();

         Splitters.by('.').split(m_project.getGroupId(), parts);
         Splitters.by('-').split(m_project.getArtifactId(), parts);

         StringBuilder sb = new StringBuilder(256);
         Set<String> done = new HashSet<String>();

         for (String part : parts) {
            if (!done.contains(part)) {
               sb.append(part).append('.');
               done.add(part);
            }
         }

         defaultName = sb.substring(0, sb.length() - 1);
      }

      ConsoleProvider console = PropertyProviders.fromConsole();
      String name = console.forString("project.package", "Please input project-level package name:", defaultName, null);

      return name;
   }

   protected String getDefaultPackageName(String groupId, String artifactId) {
      List<String> parts = new ArrayList<String>();

      Splitters.by('.').split(groupId, parts);
      Splitters.by('-').split(artifactId, parts);

      StringBuilder sb = new StringBuilder(256);
      Set<String> done = new HashSet<String>();

      for (String part : parts) {
         if (!done.contains(part)) {
            sb.append(part).append('.');
            done.add(part);
         }
      }

      return sb.substring(0, sb.length() - 1);
   }

   private String guessPackageName() {
      for (String sourceRoot : m_project.getCompileSourceRoots()) {
         final AtomicReference<String> packageName = new AtomicReference<String>();

         Scanners.forDir().scan(new File(sourceRoot), new DirMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (path.endsWith("/build")) {
                  packageName.set(path.substring(0, path.length() - 6).replace('/', '.'));
                  return Direction.MATCHED;
               } else {
                  return Direction.DOWN;
               }
            }
         });

         if (packageName.get() != null) {
            return packageName.get();
         }
      }

      return null;
   }

   protected void initialize(MavenProject project) {
      m_project = project;
   }

   protected Wizard loadWizard() throws IOException, SAXException {
      File wizardFile = new File(m_project.getBasedir(), "src/main/resources/META-INF/wizard/wizard.xml");
      Wizard wizard;

      if (wizardFile.isFile()) {
         String content = Files.forIO().readFrom(wizardFile, "utf-8");

         wizard = WizardHelper.fromXml(content);
      } else {
         wizard = new Wizard();
         wizard.setPackage(getWizardPackageName());
      }

      return wizard;
   }

   protected void saveManifest() throws IOException {
      File manifestFile = new File(m_project.getBasedir(), "src/main/resources/META-INF/wizard/manifest.xml");

      if (!manifestFile.isFile()) {
         Manifest manifest = new Manifest();

         manifest.addFile(new org.unidal.maven.plugin.wizard.model.entity.File().setPath("wizard.xml"));
         Files.forIO().writeTo(manifestFile, manifest.toString());
         m_logger.info("File " + manifestFile.getCanonicalPath() + " created.");
      }
   }

   protected void saveWizard(Wizard wizard) throws IOException {
      File wizardFile = new File(m_project.getBasedir(), "src/main/resources/META-INF/wizard/wizard.xml");

      Files.forIO().writeTo(wizardFile, wizard.toString());

      if (!wizardFile.isFile()) {
         m_logger.info("File " + wizardFile.getCanonicalPath() + " created.");
      } else {
         m_logger.info("File " + wizardFile.getCanonicalPath() + " updated.");
      }
   }
}