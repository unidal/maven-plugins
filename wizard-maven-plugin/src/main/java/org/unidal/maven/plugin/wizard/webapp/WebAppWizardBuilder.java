package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.unidal.maven.plugin.wizard.model.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

@Named
public class WebAppWizardBuilder implements LogEnabled {
   @Inject
   private WizardMeta m_wizardMeta;

   private Logger m_logger;

   private MavenProject m_project;

   public Wizard build(MavenProject project, File manifestFile) throws IOException, SAXException {
      m_project = project;
      File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");
      Wizard wizard = buildWizard(wizardFile);

      if (!manifestFile.exists()) {
         saveXml(m_wizardMeta.getManifest("wizard.xml"), manifestFile);
      }

      return wizard;
   }

   private Wizard buildWizard(File wizardFile) throws IOException, SAXException {
      Wizard wizard;

      if (wizardFile.isFile()) {
         String content = Files.forIO().readFrom(wizardFile, "utf-8");

         wizard = DefaultSaxParser.parse(content);
      } else {
         String packageName = getPackageName();

         wizard = new Wizard();
         wizard.setPackage(packageName);
      }

      wizard.accept(new Builder());
      Files.forIO().writeTo(wizardFile, wizard.toString());

      m_logger.info("File " + wizardFile.getCanonicalPath() + " generated.");

      return wizard;
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   private String getPackageName() {
      String defaultPackageName = guessPackageName();

      if (defaultPackageName == null) {
         String groupId = m_project.getGroupId();
         String artifactId = m_project.getArtifactId();
         int index = artifactId.lastIndexOf('-');

         defaultPackageName = (groupId + "." + artifactId.substring(index + 1)).replace('-', '.');
      }

      String packageName = PropertyProviders.fromConsole().forString("package",
            "Please input project-level package name:", defaultPackageName, null);

      return packageName;
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

   private void saveXml(Document doc, File file) throws IOException {
      File parent = file.getCanonicalFile().getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(doc, writer);
         m_logger.info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }

   static class Builder extends BaseVisitor {
      @Override
      public void visitModule(Module module) {
         List<String> pageNames = new ArrayList<String>();

         for (Page page : module.getPages()) {
            pageNames.add(page.getName());
         }

         ConsoleProvider console = PropertyProviders.fromConsole();
         String pageName = console.forString("page", "Select page below or new one:", pageNames, null, null);
         Page page = module.findPage(pageName);

         if (page == null) {
            page = new Page(pageName);

            if (module.getPages().isEmpty()) {
               page.setDefault(true);
            }

            page.setPath(pageName);
            page.setTitle(Character.toUpperCase(pageName.charAt(0)) + pageName.substring(1));
            page.setDescription(page.getTitle());
            page.setPackage(module.getPackage() + "." + pageName);
            module.addPage(page);
         }

         visitPage(page);
      }

      @Override
      public void visitPage(Page page) {
      }

      @Override
      public void visitWebapp(Webapp webapp) {
         List<String> moduleNames = new ArrayList<String>();

         for (Module module : webapp.getModules()) {
            moduleNames.add(module.getName());
         }

         ConsoleProvider console = PropertyProviders.fromConsole();
         String moduleName = console.forString("module", "Select module below or new name:", moduleNames, null, null);
         Module module = webapp.findModule(moduleName);

         if (module == null) { // new module
            module = new Module(moduleName);

            if (webapp.isModule()) {
               module.setPackage(webapp.getPackage() + ".page");
            } else {
               module.setPackage(webapp.getPackage() + "." + moduleName);
            }

            module.setPath(moduleName);
            module.setDefault(moduleNames.isEmpty());
            webapp.addModule(module);
         }

         visitModule(module);
      }

      @Override
      public void visitWizard(Wizard wizard) {
         Webapp webapp = wizard.getWebapp();

         if (webapp == null) {
            ConsoleProvider console = PropertyProviders.fromConsole();

            webapp = new Webapp();
            wizard.setWebapp(webapp);

            if (webapp.getModule() == null) {
               boolean module = PropertyProviders.fromConsole().forBoolean("module", "Is it a web module?", true);

               webapp.setModule(module);
            }

            String packageName = wizard.getPackage();

            webapp.setPackage(packageName);

            if (!webapp.isModule()) {
               String defaultName = packageName.substring(packageName.lastIndexOf('.') + 1);
               String name = console.forString("name", "Webapp name:", defaultName, null);

               webapp.setName(name);

               boolean cat = console.forBoolean("cat", "Support CAT?", true);
               boolean jstl = console.forBoolean("jstl", "Support JSTL?", true);

               webapp.setCat(cat);
               webapp.setJstl(jstl);
            }
         }

         visitWebapp(webapp);
      }
   }
}