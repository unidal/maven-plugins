package com.ebay.esf.tag.core;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ebay.esf.dom.ComponentNode;
import com.ebay.esf.dom.ITagNode;
import com.ebay.esf.dom.TagNode;

public class TagXmlParser extends DefaultHandler {
   private TagNode m_current;

   private SAXParser m_parser;

   private static ThreadLocal<SAXParser> s_local = new ThreadLocal<SAXParser>() {
      @Override
      protected SAXParser initialValue() {
         try {
            return SAXParserFactory.newInstance().newSAXParser();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   };

   @Override
   public void characters(final char[] ch, final int start, final int length) throws SAXException {
      if (isAllWhiteSpace(ch, start, length)) {
         return;
      }

      final String text = new String(ch, start, length);

      m_current.appendText(text);
   }

   private boolean isAllWhiteSpace(final char[] chars, final int start, final int length) {
      for (int i = 0; i < length; i++) {
         if (chars[start + i] > ' ') {
            return false;
         }
      }

      return true;
   }

   private TagNode createElement(final String tagName, final Attributes attributes, final ITagNode parent) {
      TagNode element = null;

      if (ComponentNode.ESF_TAG_COMPONENT.equals(tagName)) {
         element = new ComponentNode(parent);
      } else {
         element = new TagNode(tagName, parent);
      }

      int length = (attributes == null ? 0 : attributes.getLength());

      for (int i = 0; i < length; i++) {
         String name = attributes.getLocalName(i);
         String value = attributes.getValue(i);

         element.setAttribute(name, value);
      }

      return element;
   }

   @Override
   public void endElement(final String uri, final String localName, final String qName) throws SAXException {
      m_current = (TagNode) m_current.getParent();
   }

   @Override
   public void error(SAXParseException e) throws SAXException {
      throw e;
   }

   @Override
   public void fatalError(SAXParseException e) throws SAXException {
      throw e;
   }

   public ITagNode parse(String snippet) throws SAXException, IOException {
      try {
         if (m_parser == null) {
            m_parser = s_local.get();
         }

         XMLReader reader = m_parser.getXMLReader();

         reader.setFeature("http://xml.org/sax/features/namespaces", true);

         if (m_parser.getClass().getName().equals("org.apache.xerces.jaxp.SAXParserImpl")) {
            // disable DTD validate
            String feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            reader.setFeature(feature, false);
         }

         reader.setContentHandler(this);
         reader.setErrorHandler(this);

         // Why we add a wrapper tag?
         // The reason is we need to make sure it's a well-formed XML document
         reader.parse(new InputSource(new StringReader("<tag>" + snippet + "</tag>")));
         return (ITagNode)m_current.getFirstChild();
      } finally {
         m_parser.reset();
      }
   }

   @Override
   public void startDocument() throws SAXException {
      if (m_current == null) {
         m_current = new TagNode("tag", null);
      }
   }

   @Override
   public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
         throws SAXException {
      m_current = createElement(localName, attributes, m_current);
   }

   @Override
   public void warning(SAXParseException e) throws SAXException {
      System.err.println(e.toString());
   }

}