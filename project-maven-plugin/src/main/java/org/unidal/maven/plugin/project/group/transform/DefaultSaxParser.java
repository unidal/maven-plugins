package org.unidal.maven.plugin.project.group.transform;

import static org.unidal.maven.plugin.project.group.Constants.ELEMENT_ARTIFACTID;
import static org.unidal.maven.plugin.project.group.Constants.ELEMENT_NAME;
import static org.unidal.maven.plugin.project.group.Constants.ELEMENT_PREFIX;

import static org.unidal.maven.plugin.project.group.Constants.ENTITY_METADATA;
import static org.unidal.maven.plugin.project.group.Constants.ENTITY_PLUGIN;
import static org.unidal.maven.plugin.project.group.Constants.ENTITY_PLUGINS;

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

import org.unidal.maven.plugin.project.group.IEntity;
import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public class DefaultSaxParser extends DefaultHandler {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DefaultSaxMaker m_maker = new DefaultSaxMaker();

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private IEntity<?> m_entity;

   private StringBuilder m_text = new StringBuilder();

   public static GroupMetadata parse(InputStream in) throws SAXException, IOException {
      return parseEntity(GroupMetadata.class, new InputSource(removeBOM(in)));
   }

   public static GroupMetadata parse(Reader reader) throws SAXException, IOException {
      return parseEntity(GroupMetadata.class, new InputSource(removeBOM(reader)));
   }

   public static GroupMetadata parse(String xml) throws SAXException, IOException {
      return parseEntity(GroupMetadata.class, new InputSource(new StringReader(removeBOM(xml))));
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

         if (currentObj instanceof Plugin) {
            Plugin plugin = (Plugin) currentObj;

            if (ELEMENT_NAME.equals(currentTag)) {
               plugin.setName(getText());
            } else if (ELEMENT_PREFIX.equals(currentTag)) {
               plugin.setPrefix(getText());
            } else if (ELEMENT_ARTIFACTID.equals(currentTag)) {
               plugin.setArtifactId(getText());
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

   private void parseForMetadata(GroupMetadata parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_PLUGINS.equals(qName)) {
         m_objs.push(parentObj);
      } else if (ENTITY_PLUGIN.equals(qName)) {
         Plugin plugin = m_maker.buildPlugin(attributes);

         m_linker.onPlugin(parentObj, plugin);
         m_objs.push(plugin);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under metadata!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForPlugin(Plugin parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_NAME.equals(qName) || ELEMENT_PREFIX.equals(qName) || ELEMENT_ARTIFACTID.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under plugin!", qName));
      }

      m_tags.push(qName);
   }

   private void parseRoot(String qName, Attributes attributes) throws SAXException {
      if (ENTITY_METADATA.equals(qName)) {
         GroupMetadata metadata = m_maker.buildMetadata(attributes);

         m_entity = metadata;
         m_objs.push(metadata);
         m_tags.push(qName);
      } else if (ENTITY_PLUGIN.equals(qName)) {
         Plugin plugin = m_maker.buildPlugin(attributes);

         m_entity = plugin;
         m_objs.push(plugin);
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

            if (parent instanceof GroupMetadata) {
               parseForMetadata((GroupMetadata) parent, tag, qName, attributes);
            } else if (parent instanceof Plugin) {
               parseForPlugin((Plugin) parent, tag, qName, attributes);
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
