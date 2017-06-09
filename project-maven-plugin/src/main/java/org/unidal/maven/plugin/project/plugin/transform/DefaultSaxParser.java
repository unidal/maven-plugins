package org.unidal.maven.plugin.project.plugin.transform;

import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_ARTIFACTID;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_GROUPID;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_LASTUPDATED;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_LATEST;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_RELEASE;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_VERSION;
import static org.unidal.maven.plugin.project.plugin.Constants.ELEMENT_VERSIONS;

import static org.unidal.maven.plugin.project.plugin.Constants.ENTITY_METADATA;
import static org.unidal.maven.plugin.project.plugin.Constants.ENTITY_VERSIONING;
import static org.unidal.maven.plugin.project.plugin.Constants.ENTITY_VERSIONS;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.unidal.maven.plugin.project.plugin.IEntity;
import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public class DefaultSaxParser extends DefaultHandler {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DefaultSaxMaker m_maker = new DefaultSaxMaker();

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private IEntity<?> m_entity;

   private StringBuilder m_text = new StringBuilder();

   public static PluginMetadata parse(InputStream in) throws SAXException, IOException {
      return parseEntity(PluginMetadata.class, new InputSource(removeBOM(in)));
   }

   public static PluginMetadata parse(Reader reader) throws SAXException, IOException {
      return parseEntity(PluginMetadata.class, new InputSource(removeBOM(reader)));
   }

   public static PluginMetadata parse(String xml) throws SAXException, IOException {
      return parseEntity(PluginMetadata.class, new InputSource(new StringReader(removeBOM(xml))));
   }

   @SuppressWarnings("unchecked")
   private static <T extends IEntity<?>> T parseEntity(Class<T> type, InputSource is) throws SAXException, IOException {
      try {
         DefaultSaxParser handler = new DefaultSaxParser();
         SAXParserFactory factory = SAXParserFactory.newInstance();

         factory.setValidating(false);
         factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         factory.setFeature("http://xml.org/sax/features/validation", false);

         factory.newSAXParser().parse(is, handler);
         return (T) handler.getEntity();
      } catch (ParserConfigurationException e) {
         throw new IllegalStateException("Unable to get SAX parser instance!", e);
      }
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, InputStream in) throws SAXException, IOException {
      return parseEntity(type, new InputSource(removeBOM(in)));
   }

   public static <T extends IEntity<?>> T parseEntity(Class<T> type, String xml) throws SAXException, IOException {
      return parseEntity(type, new InputSource(new StringReader(removeBOM(xml))));
   }

   // to remove Byte Order Mark(BOM) at the head of windows utf-8 file
   @SuppressWarnings("unchecked")
   private static <T> T removeBOM(T obj) throws IOException {
      if (obj instanceof String) {
         String str = (String) obj;

         if (str.charAt(0) == 0xFEFF) {
            return (T) str.substring(1);
         } else {
            return obj;
         }
      } else if (obj instanceof InputStream) {
         BufferedInputStream in = new BufferedInputStream((InputStream) obj);

         in.mark(3);

         if (in.read() != 0xEF || in.read() != 0xBB || in.read() != 0xBF) {
            in.reset();
         }

         return (T) in;
      } else if (obj instanceof Reader) {
         BufferedReader in = new BufferedReader((Reader) obj);

         in.mark(1);

         if (in.read() != 0xFEFF) {
            in.reset();
         }

         return (T) in;
      } else {
         return obj;
      }
   }

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException {
      m_text.append(ch, start, length);
   }

   @Override
   public void endDocument() throws SAXException {
      m_linker.finish();
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (uri == null || uri.length() == 0) {
         Object currentObj = m_objs.pop();
         String currentTag = m_tags.pop();

         if (currentObj instanceof PluginMetadata) {
            PluginMetadata metadata = (PluginMetadata) currentObj;

            if (ELEMENT_GROUPID.equals(currentTag)) {
               metadata.setGroupId(getText());
            } else if (ELEMENT_ARTIFACTID.equals(currentTag)) {
               metadata.setArtifactId(getText());
            }
         } else if (currentObj instanceof Versioning) {
            Versioning versioning = (Versioning) currentObj;

            if (ELEMENT_LATEST.equals(currentTag)) {
               versioning.setLatest(getText());
            } else if (ELEMENT_RELEASE.equals(currentTag)) {
               versioning.setRelease(getText());
            } else if (ELEMENT_LASTUPDATED.equals(currentTag)) {
               versioning.setLastUpdated(getText());
            }
         } else if (currentObj instanceof Versions) {
            Versions versions = (Versions) currentObj;

            if (ELEMENT_VERSION.equals(currentTag)) {
               versions.addVersion(getText());
            }
         }
      }

      m_text.setLength(0);
   }

   private IEntity<?> getEntity() {
      return m_entity;
   }

   protected String getText() {
      return m_text.toString();
   }

   private void parseForMetadata(PluginMetadata parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_GROUPID.equals(qName) || ELEMENT_ARTIFACTID.equals(qName)) {
         m_objs.push(parentObj);
      } else if (ENTITY_VERSIONING.equals(qName)) {
         Versioning versioning = m_maker.buildVersioning(attributes);

         m_linker.onVersioning(parentObj, versioning);
         m_objs.push(versioning);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under metadata!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForVersioning(Versioning parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_LATEST.equals(qName) || ELEMENT_RELEASE.equals(qName) || ELEMENT_LASTUPDATED.equals(qName)) {
         m_objs.push(parentObj);
      } else if (ENTITY_VERSIONS.equals(qName)) {
         Versions versions = m_maker.buildVersions(attributes);

         m_linker.onVersions(parentObj, versions);
         m_objs.push(versions);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under versioning!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForVersions(Versions parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_VERSIONS.equals(qName) || ELEMENT_VERSION.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under versions!", qName));
      }

      m_tags.push(qName);
   }

   private void parseRoot(String qName, Attributes attributes) throws SAXException {
      if (ENTITY_METADATA.equals(qName)) {
         PluginMetadata metadata = m_maker.buildMetadata(attributes);

         m_entity = metadata;
         m_objs.push(metadata);
         m_tags.push(qName);
      } else if (ENTITY_VERSIONING.equals(qName)) {
         Versioning versioning = m_maker.buildVersioning(attributes);

         m_entity = versioning;
         m_objs.push(versioning);
         m_tags.push(qName);
      } else if (ENTITY_VERSIONS.equals(qName)) {
         Versions versions = m_maker.buildVersions(attributes);

         m_entity = versions;
         m_objs.push(versions);
         m_tags.push(qName);
      } else {
         throw new SAXException("Unknown root element(" + qName + ") found!");
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (uri == null || uri.length() == 0) {
         if (m_objs.isEmpty()) { // root
            parseRoot(qName, attributes);
         } else {
            Object parent = m_objs.peek();
            String tag = m_tags.peek();

            if (parent instanceof PluginMetadata) {
               parseForMetadata((PluginMetadata) parent, tag, qName, attributes);
            } else if (parent instanceof Versioning) {
               parseForVersioning((Versioning) parent, tag, qName, attributes);
            } else if (parent instanceof Versions) {
               parseForVersions((Versions) parent, tag, qName, attributes);
            } else {
               throw new RuntimeException(String.format("Unknown entity(%s) under %s!", qName, parent.getClass().getName()));
            }
         }

         m_text.setLength(0);
        } else {
         throw new SAXException(String.format("Namespace(%s) is not supported by %s.", uri, this.getClass().getName()));
      }
   }
}
