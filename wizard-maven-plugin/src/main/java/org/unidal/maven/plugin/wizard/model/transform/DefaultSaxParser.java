package org.unidal.maven.plugin.wizard.model.transform;

import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_DESCRIPTION;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_DRIVER;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_PASSWORD;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_PROPERTIES;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_SAMPLE_MODEL;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_URL;
import static org.unidal.maven.plugin.wizard.model.Constants.ELEMENT_USER;

import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_DATASOURCE;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_GROUP;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_JDBC;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_MODEL;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_MODULE;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_PAGE;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_TABLE;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_WEBAPP;
import static org.unidal.maven.plugin.wizard.model.Constants.ENTITY_WIZARD;

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

import org.unidal.maven.plugin.wizard.model.IEntity;
import org.unidal.maven.plugin.wizard.model.entity.Datasource;
import org.unidal.maven.plugin.wizard.model.entity.Group;
import org.unidal.maven.plugin.wizard.model.entity.Jdbc;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Table;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;

public class DefaultSaxParser extends DefaultHandler {

   private DefaultLinker m_linker = new DefaultLinker(true);

   private DefaultSaxMaker m_maker = new DefaultSaxMaker();

   private Stack<String> m_tags = new Stack<String>();

   private Stack<Object> m_objs = new Stack<Object>();

   private IEntity<?> m_entity;

   private StringBuilder m_text = new StringBuilder();

   public static Wizard parse(InputStream in) throws SAXException, IOException {
      return parseEntity(Wizard.class, new InputSource(removeBOM(in)));
   }

   public static Wizard parse(Reader reader) throws SAXException, IOException {
      return parseEntity(Wizard.class, new InputSource(removeBOM(reader)));
   }

   public static Wizard parse(String xml) throws SAXException, IOException {
      return parseEntity(Wizard.class, new InputSource(new StringReader(removeBOM(xml))));
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

         if (currentObj instanceof Page) {
            Page page = (Page) currentObj;

            if (ELEMENT_DESCRIPTION.equals(currentTag)) {
               page.setDescription(getText());
            }
         } else if (currentObj instanceof Datasource) {
            Datasource datasource = (Datasource) currentObj;

            if (ELEMENT_DRIVER.equals(currentTag)) {
               datasource.setDriver(getText());
            } else if (ELEMENT_URL.equals(currentTag)) {
               datasource.setUrl(getText());
            } else if (ELEMENT_USER.equals(currentTag)) {
               datasource.setUser(getText());
            } else if (ELEMENT_PASSWORD.equals(currentTag)) {
               datasource.setPassword(getText());
            } else if (ELEMENT_PROPERTIES.equals(currentTag)) {
               datasource.setProperties(getText());
            }
         } else if (currentObj instanceof Model) {
            Model model = (Model) currentObj;

            if (ELEMENT_SAMPLE_MODEL.equals(currentTag)) {
               model.setSampleModel(getText());
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

   private void parseForDatasource(Datasource parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_DRIVER.equals(qName) || ELEMENT_URL.equals(qName) || ELEMENT_USER.equals(qName) || ELEMENT_PASSWORD.equals(qName) || ELEMENT_PROPERTIES.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under datasource!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForGroup(Group parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_TABLE.equals(qName)) {
         Table table = m_maker.buildTable(attributes);

         m_linker.onTable(parentObj, table);
         m_objs.push(table);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under group!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForJdbc(Jdbc parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_DATASOURCE.equals(qName)) {
         Datasource datasource = m_maker.buildDatasource(attributes);

         m_linker.onDatasource(parentObj, datasource);
         m_objs.push(datasource);
      } else if (ENTITY_GROUP.equals(qName)) {
         Group group = m_maker.buildGroup(attributes);

         m_linker.onGroup(parentObj, group);
         m_objs.push(group);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under jdbc!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForModel(Model parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_SAMPLE_MODEL.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under model!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForModule(Module parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_PAGE.equals(qName)) {
         Page page = m_maker.buildPage(attributes);

         m_linker.onPage(parentObj, page);
         m_objs.push(page);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under module!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForPage(Page parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ELEMENT_DESCRIPTION.equals(qName)) {
         m_objs.push(parentObj);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under page!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForTable(Table parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      m_objs.push(parentObj);
      m_tags.push(qName);
   }

   private void parseForWebapp(Webapp parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_MODULE.equals(qName)) {
         Module module = m_maker.buildModule(attributes);

         m_linker.onModule(parentObj, module);
         m_objs.push(module);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under webapp!", qName));
      }

      m_tags.push(qName);
   }

   private void parseForWizard(Wizard parentObj, String parentTag, String qName, Attributes attributes) throws SAXException {
      if (ENTITY_WEBAPP.equals(qName)) {
         Webapp webapp = m_maker.buildWebapp(attributes);

         m_linker.onWebapp(parentObj, webapp);
         m_objs.push(webapp);
      } else if (ENTITY_JDBC.equals(qName)) {
         Jdbc jdbc = m_maker.buildJdbc(attributes);

         m_linker.onJdbc(parentObj, jdbc);
         m_objs.push(jdbc);
      } else if (ENTITY_MODEL.equals(qName)) {
         Model model = m_maker.buildModel(attributes);

         m_linker.onModel(parentObj, model);
         m_objs.push(model);
      } else {
         throw new SAXException(String.format("Element(%s) is not expected under wizard!", qName));
      }

      m_tags.push(qName);
   }

   private void parseRoot(String qName, Attributes attributes) throws SAXException {
      if (ENTITY_WIZARD.equals(qName)) {
         Wizard wizard = m_maker.buildWizard(attributes);

         m_entity = wizard;
         m_objs.push(wizard);
         m_tags.push(qName);
      } else if (ENTITY_WEBAPP.equals(qName)) {
         Webapp webapp = m_maker.buildWebapp(attributes);

         m_entity = webapp;
         m_objs.push(webapp);
         m_tags.push(qName);
      } else if (ENTITY_MODULE.equals(qName)) {
         Module module = m_maker.buildModule(attributes);

         m_entity = module;
         m_objs.push(module);
         m_tags.push(qName);
      } else if (ENTITY_PAGE.equals(qName)) {
         Page page = m_maker.buildPage(attributes);

         m_entity = page;
         m_objs.push(page);
         m_tags.push(qName);
      } else if (ENTITY_JDBC.equals(qName)) {
         Jdbc jdbc = m_maker.buildJdbc(attributes);

         m_entity = jdbc;
         m_objs.push(jdbc);
         m_tags.push(qName);
      } else if (ENTITY_DATASOURCE.equals(qName)) {
         Datasource datasource = m_maker.buildDatasource(attributes);

         m_entity = datasource;
         m_objs.push(datasource);
         m_tags.push(qName);
      } else if (ENTITY_GROUP.equals(qName)) {
         Group group = m_maker.buildGroup(attributes);

         m_entity = group;
         m_objs.push(group);
         m_tags.push(qName);
      } else if (ENTITY_TABLE.equals(qName)) {
         Table table = m_maker.buildTable(attributes);

         m_entity = table;
         m_objs.push(table);
         m_tags.push(qName);
      } else if (ENTITY_MODEL.equals(qName)) {
         Model model = m_maker.buildModel(attributes);

         m_entity = model;
         m_objs.push(model);
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

            if (parent instanceof Wizard) {
               parseForWizard((Wizard) parent, tag, qName, attributes);
            } else if (parent instanceof Webapp) {
               parseForWebapp((Webapp) parent, tag, qName, attributes);
            } else if (parent instanceof Module) {
               parseForModule((Module) parent, tag, qName, attributes);
            } else if (parent instanceof Page) {
               parseForPage((Page) parent, tag, qName, attributes);
            } else if (parent instanceof Jdbc) {
               parseForJdbc((Jdbc) parent, tag, qName, attributes);
            } else if (parent instanceof Datasource) {
               parseForDatasource((Datasource) parent, tag, qName, attributes);
            } else if (parent instanceof Group) {
               parseForGroup((Group) parent, tag, qName, attributes);
            } else if (parent instanceof Table) {
               parseForTable((Table) parent, tag, qName, attributes);
            } else if (parent instanceof Model) {
               parseForModel((Model) parent, tag, qName, attributes);
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
