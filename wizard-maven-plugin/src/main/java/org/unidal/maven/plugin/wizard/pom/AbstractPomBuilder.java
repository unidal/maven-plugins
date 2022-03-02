package org.unidal.maven.plugin.wizard.pom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

abstract class AbstractPomBuilder implements LogEnabled {
   protected PomDelegate m_pom;

   protected Logger m_logger;

   public AbstractPomBuilder() {
      m_pom = new PomDelegate(new VersionManager());
   }

   protected Document loadPom(File pomFile) throws JDOMException, IOException {
      Document doc = new SAXBuilder().build(pomFile);

      return doc;
   }

   protected void savePom(File pomFile, Document doc) throws IOException {
      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      FileWriter writer = new FileWriter(pomFile);

      try {
         outputter.output(doc, writer);

         m_logger.info(String.format("File(%s) updated.", pomFile.getCanonicalPath()));
      } finally {
         writer.close();
      }
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   
}
