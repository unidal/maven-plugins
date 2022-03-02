package org.unidal.maven.plugin.wizard.meta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.codegen.helper.PropertyProviders.IValidator;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.dom.DomAccessor;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.xml.sax.SAXException;

@Named
public class ModelWizardBuilder extends AbstractWizardBuilder {
   public Wizard build(MavenProject project) throws IOException, SAXException {
      super.initialize(project);

      Wizard wizard = super.loadWizard();

      wizard.accept(new Builder());

      super.saveWizard(wizard);
      super.saveManifest();

      return wizard;
   }

   private class Builder extends BaseVisitor {
      private Wizard m_wizard;

      @SuppressWarnings({ "unchecked" })
      private String getRootElementName(String xmlFile) {
         File file = new File(xmlFile);

         try {
            Document doc = new DomAccessor().loadDocument(file);

            for (Element child : (List<Element>) doc.getRootElement().getChildren()) {
               return child.getName();
            }
         } catch (Exception e) {
            // ignore it
         }

         return null;
      }

      @Override
      public void visitModel(Model model) {
         ConsoleProvider console = PropertyProviders.fromConsole();

         if (model.getName() == null) {
            String defaultName = getRootElementName(model.getSampleModel());
            String name = console.forString("model.name", "Prefix name of target files:", defaultName,
                  new IValidator<String>() {
                     @Override
                     public boolean validate(String name) {
                        if (m_wizard.findModel(name) != null) {
                           System.out.println("The prefix has been already used by others.");
                           return false;
                        } else {
                           return true;
                        }
                     }
                  });

            model.setName(name);
         }

         if (model.getPackage() == null) {
            String defaultName = getDefaultPackageName(m_wizard.getPackage(), model.getName());
            String packageName = console.forString("model.package", "Package name of generated model:", defaultName, null);

            model.setPackage(packageName);
         }
      }

      @Override
      public void visitWizard(final Wizard wizard) {
         m_wizard = wizard;

         List<String> names = new ArrayList<String>();

         for (Model model : wizard.getModels()) {
            names.add(model.getName());
         }

         ConsoleProvider console = PropertyProviders.fromConsole();
         String name = console.forString("model.sample", "Select model name below or input a sample xml file:", names, null,
               new IValidator<String>() {
                  @Override
                  public boolean validate(String name) {
                     return wizard.findModel(name) != null || new File(name).isFile();
                  }
               });
         Model model = wizard.findModel(name);

         if (model == null) { // must be a new sample model
            model = new Model();
            model.setSampleModel(name);
            wizard.addModel(model);
         }

         visitModel(model);
      }
   }
}