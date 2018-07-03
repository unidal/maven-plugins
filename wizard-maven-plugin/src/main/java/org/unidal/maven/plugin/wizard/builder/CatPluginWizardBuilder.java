package org.unidal.maven.plugin.wizard.builder;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;

public class CatPluginWizardBuilder extends BaseVisitor {
   private Webapp m_webapp;

   @Override
   public void visitModule(Module module) {
      List<String> pageNames = new ArrayList<String>(module.getPages().size());

      for (Page page : module.getPages()) {
         pageNames.add(page.getName());
      }

      ConsoleProvider console = PropertyProviders.fromConsole();
      String pageName = console.forString("page", "Select page name below or input a new one:", pageNames, null, null);
      Page page = module.findPage(pageName);

      if (page == null) {
         page = new Page(pageName);

         if (module.getPages().isEmpty()) {
            page.setDefault(true);
         }

         if (m_webapp.isModule()) {
            String defaultPackage = module.getPackage() + ".page";
            String packageName = console.forString("module.package", "Module package:", defaultPackage, null);

            page.setPackage(packageName);
         }

         String path = console.forString("page.path", "Page path:", pageName, null);
         String caption = Character.toUpperCase(pageName.charAt(0)) + pageName.substring(1);

         page.setPath(path);
         page.setTitle(caption);
         page.setDescription(caption);
         page.setTemplate("default");
         module.addPage(page);
      }

      visitPage(page);
   }

   @Override
   public void visitPage(Page page) {
   }

   @Override
   public void visitWebapp(Webapp webapp) {
      m_webapp = webapp;

      List<Module> modules = webapp.getModules();
      List<String> moduleNames = new ArrayList<String>(modules.size());

      for (Module module : modules) {
         moduleNames.add(module.getName());
      }

      ConsoleProvider console = PropertyProviders.fromConsole();
      String moduleName = console.forString("module", "Select module name below or input a new one:", moduleNames,
            "report", null);
      Module module = webapp.findModule(moduleName);

      if (module == null) { // new module
         module = new Module(moduleName);

         if (webapp.isModule()) {
            String defaultPackage = webapp.getPackage() + "." + moduleName;
            String packageName = console.forString("module.package", "Module package:", defaultPackage, null);

            module.setPackage(packageName);
         }

         String path = console.forString("module.path", "Module path:", moduleName, null);

         module.setPath(path);
         module.setDefault(modules.isEmpty());
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
         webapp.setModule(true);

         String packageName = wizard.getPackage();
         boolean jstl = console.forBoolean("cat", "Support JSTL?", true);
         boolean bootstrap = console.forBoolean("layout", "Support bootstrap layout?", true);
         boolean pluginManagement = console.forBoolean("pluginManagement",
               "Support POM plugin management for Java Compiler and Eclipse?", false);
         /*
          * boolean webres = PropertyProviders.fromConsole().forBoolean("webres", "Support WebRes framework?", false);
          */

         wizard.setWebapp(webapp);
         webapp.setPackage(packageName);
         webapp.setName("cat");
         webapp.setWebres(false);
         webapp.setCat(false);
         webapp.setJstl(jstl);
         webapp.setLayout(bootstrap ? "bootstrap" : null);
         webapp.setPluginManagement(pluginManagement);
      }

      visitWebapp(webapp);
   }
}