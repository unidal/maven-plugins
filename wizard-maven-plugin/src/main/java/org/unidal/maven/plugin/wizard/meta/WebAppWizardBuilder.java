package org.unidal.maven.plugin.wizard.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.xml.sax.SAXException;

@Named
public class WebAppWizardBuilder extends AbstractWizardBuilder {
   public Wizard build(MavenProject project) throws IOException, SAXException {
      super.initialize(project);

      Wizard wizard = super.loadWizard();

      wizard.accept(new Builder());

      super.saveWizard(wizard);
      super.saveManifest();
      return wizard;
   }

   private class Builder extends BaseVisitor {
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
               String language = console.forString("language", "Which template language?",
                     Arrays.asList("Thymeleaf", "JSP"), "Thymeleaf", null);

               webapp.setName(name);
               webapp.setLanguage(language.toLowerCase());
            }
         }

         visitWebapp(webapp);
      }
   }
}