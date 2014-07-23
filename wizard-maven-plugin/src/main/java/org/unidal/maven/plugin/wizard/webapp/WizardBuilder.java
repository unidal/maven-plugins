package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;

public class WizardBuilder extends BaseVisitor {
   private WebAppMojo m_webAppMojo;

   private File m_wizardFile;

   private Module m_module;

   public WizardBuilder(WebAppMojo webAppMojo, File wizardFile) {
      m_webAppMojo = webAppMojo;
      m_wizardFile = wizardFile;
   }

   @Override
   public void visitModule(Module module) {
      List<String> pageNames = new ArrayList<String>(module.getPages().size());

      for (Page page : module.getPages()) {
         pageNames.add(page.getName());
      }

      String pageName = PropertyProviders.fromConsole().forString("page", "Select page name below or input a new one:", pageNames,
            null, null);
      Page page = module.findPage(pageName);

      if (page == null) {
         String path = PropertyProviders.fromConsole().forString("page.path", "Page path:", pageName, null);

         page = new Page(pageName);

         if (module.getPages().isEmpty()) {
            page.setDefault(true);
         }

         String caption = Character.toUpperCase(pageName.charAt(0)) + pageName.substring(1);

         page.setPath(path);
         page.setTitle(caption);
         page.setDescription(caption);
         page.setTemplate("default");
         module.addPage(page);
      }

      m_module = module;
      visitPage(page);
   }

   @Override
   public void visitPage(Page page) {
      boolean flag = false; // DISABLED

      if (flag) {
         List<String> templates = Arrays.asList("default", "single");
         String template = PropertyProviders.fromConsole().forString("template", "Page template:", templates, page.getTemplate(),
               null);

         if ("single".equals(template)) {
            File templateFile = new File(m_wizardFile.getParentFile(), "template.xml");

            try {
               m_webAppMojo.buildTemplate(templateFile, m_module.getName(), page.getName());
            } catch (Exception e) {
               throw new RuntimeException(String.format("Error when building template(%s)!", templateFile), e);
            }
         }
      }
   }

   @Override
   public void visitWebapp(Webapp webapp) {
      List<Module> modules = webapp.getModules();
      List<String> moduleNames = new ArrayList<String>(modules.size());

      for (Module module : modules) {
         moduleNames.add(module.getName());
      }

      String moduleName = PropertyProviders.fromConsole().forString("module", "Select module name below or input a new one:",
            moduleNames, null, null);
      Module module = webapp.findModule(moduleName);

      if (module == null) {
         String path = PropertyProviders.fromConsole().forString("module.path", "Module path:", moduleName, null);

         module = new Module(moduleName);

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
         webapp = new Webapp();
         String packageName = wizard.getPackage();
         String defaultName = packageName.substring(packageName.lastIndexOf('.') + 1);
         String name = PropertyProviders.fromConsole().forString("name", "Webapp name:", defaultName, null);
         boolean module = PropertyProviders.fromConsole().forBoolean("module", "Support Web Module?", false);
         boolean webres = PropertyProviders.fromConsole().forBoolean("webres", "Support WebRes framework?", false);
         boolean cat = PropertyProviders.fromConsole().forBoolean("cat", "Support CAT?", true);
         boolean jstl = PropertyProviders.fromConsole().forBoolean("cat", "Support JSTL?", true);
         boolean bootstrap = PropertyProviders.fromConsole().forBoolean("layout", "Support bootstrap layout?", true);
         boolean pluginManagement = PropertyProviders.fromConsole().forBoolean("pluginManagement",
               "Support POM plugin management for Java Compiler and Eclipse?", false);

         wizard.setWebapp(webapp);
         webapp.setPackage(packageName);
         webapp.setName(name);
         webapp.setModule(module);
         webapp.setWebres(webres);
         webapp.setCat(cat);
         webapp.setJstl(jstl);
         webapp.setLayout(bootstrap ? "bootstrap" : null);
         webapp.setPluginManagement(pluginManagement);
      }

      visitWebapp(webapp);
   }
}