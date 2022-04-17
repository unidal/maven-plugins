package org.unidal.maven.plugin.wizard.meta;

import java.io.Reader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.unidal.lookup.annotation.Named;

@Named(type = WizardMeta.class)
public class DefaultWizardMeta implements WizardMeta {
   @Override
   public Document getWizard(Reader reader) {
      SAXBuilder builder = new SAXBuilder();

      try {
         return builder.build(reader);
      } catch (Exception e) {
         throw new RuntimeException("Error to build Document. " + e, e);
      }
   }

   @Override
   public Document getManifest(String wizardXml) {
      Element manifest = new Element("manifest");

      manifest.addContent(new Element("file").setAttribute("path", wizardXml));
      return new Document(manifest);
   }
}
