package com.site.codegen.aggregator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {
   public void parse(URL manifestXml) throws SAXException, IOException {
      parse(new InputSource(manifestXml.openStream()));
   }

   public void parse(InputStream stream) throws SAXException, IOException {
      parse(new InputSource(stream));
   }

   public void parse(InputSource input) throws SAXException, IOException {
      try {
         SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
         XMLReader reader = parser.getXMLReader();

         reader.setFeature("http://xml.org/sax/features/namespaces", true);

         if (parser.getClass().getName().equals("org.apache.xerces.jaxp.SAXParserImpl")) {
            // disable DTD validate
            String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            reader.setFeature(feature, false);
         }

         reader.setContentHandler(this);
         reader.setErrorHandler(this);
         reader.setDTDHandler(this);
         reader.setEntityResolver(this);

         reader.parse(input);
      } catch (ParserConfigurationException e) {
         throw new RuntimeException(e);
      }
   }
}
