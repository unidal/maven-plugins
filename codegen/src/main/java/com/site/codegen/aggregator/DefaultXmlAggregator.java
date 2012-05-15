package com.site.codegen.aggregator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefaultXmlAggregator implements XmlAggregator {
   private String m_structureFile;

   public String aggregate(File manifestXml) {
      try {
         return aggregate(manifestXml.toURI().toURL());
      } catch (MalformedURLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;
   }

   public String aggregate(URL manifestXml) {
      if (m_structureFile == null) {
         throw new RuntimeException("Please config structureFile property first.");
      }

      Manifest manifest = parseManifest(manifestXml);
      List<File> files = manifest.getFiles();
      Node root = new Node(getNodeDefinition());

      try {
         for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            NodeParser parser = new NodeParser();

            parser.setRootNode(root);
            parser.parse(file.toURI().toURL());
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      StringBuilder sb = new StringBuilder(64 * 1024);

      root.buildXml(sb, -1, "   ");
      return sb.toString();
   }

   private NodeDefinition getNodeDefinition() {
      InputStream in = getClass().getResourceAsStream(m_structureFile);

      if (in == null) {
         in = Thread.currentThread().getContextClassLoader().getResourceAsStream(m_structureFile);
      }

      if (in == null) {
         throw new RuntimeException("Unable to find property file: " + m_structureFile);
      }

      try {
         NodeDefinitionParser parser = new NodeDefinitionParser();
         NodeDefinition root = new NodeDefinition("", null, false);

         parser.setRoot(root);
         parser.parse(in);

         return root;
      } catch (Exception e) {
         throw new RuntimeException(e);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException e) {
               // ignore it
            }
         }
      }
   }

   private Manifest parseManifest(URL manifestXml) {
      try {
         File dir = new File(manifestXml.getFile()).getParentFile();
         ManifestParser parser = new ManifestParser();
         Manifest manifest = new Manifest(dir);

         parser.setManifest(manifest);
         parser.parse(manifestXml);

         return manifest;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void setStructureFile(String structureFile) {
      m_structureFile = structureFile;
   }

   static class Manifest {
      private File m_baseDir;

      private List<File> m_files = new ArrayList<File>();

      public Manifest(File baseDir) {
         m_baseDir = baseDir;
      }

      public void addFile(String file) {
         m_files.add(new File(m_baseDir, file));
      }

      public List<File> getFiles() {
         return m_files;
      }
   }

   static class ManifestParser extends XmlHandler {
      private Stack<Object> m_objs = new Stack<Object>(); // ELEMENT object

      private Stack<String> m_tags = new Stack<String>(); // ELEMENT tag

      private Manifest m_manifest;

      @Override
      public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
         m_objs.pop();
         m_tags.pop();
      }

      public void setManifest(Manifest manifest) {
         m_manifest = manifest;
      }

      @Override
      public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
         String tag = localName;

         try {
            if (tag.equals("manifest")) {
               if (m_tags.isEmpty()) {
                  m_objs.push(m_manifest);
               } else {
                  throw new SAXException(tag + " must be first element of document");
               }
            } else if (tag.equals("file")) {
               Manifest parent = (Manifest) m_objs.peek();
               String file = attrs.getValue("path");

               parent.addFile(file);
               m_objs.push(tag);
            } else {
               String location = (m_objs.isEmpty() ? "" : " under " + m_objs.peek());

               throw new SAXException("Unknown tag(" + rawName + ") is found" + location);
            }
         } catch (RuntimeException e) {
            e.printStackTrace();

            throw new SAXException(e);
         }

         m_tags.push(tag);
      }
   }

   static class Node {
      private String m_name;

      private List<String> m_attrNames = new ArrayList<String>();

      private List<String> m_attrValues = new ArrayList<String>();

      private List<Node> m_children = new ArrayList<Node>();

      private String m_keyValue;

      private NodeDefinition m_definition;

      private String m_text;

      private boolean m_overwriteText;

      public Node(NodeDefinition definition) {
         m_definition = definition;
         m_name = definition.getName();
      }

      public Node(String name) {
         m_name = name;
      }

      public void addChild(Node child) {
         m_children.add(child);
      }

      public void buildXml(StringBuilder sb, int level, String indent) {
         synchronized (sb) {
            int offset = sb.length();

            for (int i = 0; i < level; i++) {
               sb.append(indent);
            }

            String allIndent = sb.substring(offset);

            if (m_name.length() > 0) {
               sb.append("<").append(m_name);
            }

            boolean noNamespace = (m_definition != null && m_definition.hasNoNamespace());
            int size = m_attrNames.size();
            for (int i = 0; i < size; i++) {
               String name = m_attrNames.get(i);
               if (noNamespace && (name.endsWith("xmlns") || name.indexOf(':') > 0)) {
                  continue;
               }

               sb.append(' ').append(name).append("=\"");
               sb.append(m_attrValues.get(i)).append("\"");
            }

            int len = m_children.size();

            if (len == 0) {
               if (m_text == null) {
                  sb.append("/>\r\n");
               } else {
                  sb.append("><![CDATA[\r\n");
                  sb.append(m_text);
                  sb.append(allIndent).append("]]></");
                  sb.append(m_name).append(">\r\n");
               }
            } else if (m_name.length() > 0) {
               sb.append(">\r\n");

               for (int i = 0; i < len; i++) {
                  Node child = m_children.get(i);

                  child.buildXml(sb, level + 1, indent);
               }

               sb.append(allIndent).append("</");
               sb.append(m_name).append(">\r\n");
            } else {
               for (int i = 0; i < len; i++) {
                  Node child = m_children.get(i);

                  child.buildXml(sb, level + 1, indent);
               }
            }
         }
      }

      public Node findChildByKeyValue(String nodeName, String keyValue) {
         if (keyValue == null) {
            return null;
         }

         int index = findIndexByKeyValue(nodeName, keyValue);

         if (index >= 0) {
            return m_children.get(index);
         } else {
            return null;
         }
      }

      private int findIndexByKeyValue(String nodeName, String keyValue) {
         int size = m_children.size();

         for (int i = 0; i < size; i++) {
            Node child = m_children.get(i);

            if (child.getName().equals(nodeName) && keyValue.equals(child.getKeyValue())) {
               return i;
            }
         }

         return -1;
      }

      public NodeDefinition getDefinition() {
         return m_definition;
      }

      public String getKeyValue() {
         return m_keyValue;
      }

      public String getName() {
         return m_name;
      }

      public Object getText() {
         return m_text;
      }

      public boolean isOverwriteText() {
         return m_overwriteText;
      }

      public void loadAttributes(Attributes attrs) {
         int length = attrs.getLength();

         for (int i = 0; i < length; i++) {
            String name = attrs.getQName(i);
            String value = attrs.getValue(i);

            m_attrNames.add(name);
            m_attrValues.add(value);
         }

         if (m_definition != null) {
            m_keyValue = m_definition.getKeyValue(attrs);
         }
      }

      public void mergeAttributes(Node child) {
         List<String> attrNames = child.m_attrNames;
         List<String> attrValues = child.m_attrValues;
         int size = attrNames.size();
         int len = m_attrNames.size();

         for (int i = 0; i < size; i++) {
            String name = attrNames.get(i);
            String value = attrValues.get(i);
            boolean found = false;

            for (int j = 0; j < len; j++) {
               String attrName = m_attrNames.get(j);

               if (attrName.equals(name)) {
                  m_attrValues.set(j, value);
                  found = true;
                  break;
               }
            }

            if (!found) {
               m_attrNames.add(name);
               m_attrValues.add(value);
            }
         }
      }

      public void setOverwriteText(boolean overwriteText) {
         m_overwriteText = overwriteText;
      }

      public void setText(String text) {
         if (m_overwriteText) {
            m_text = null;
            m_overwriteText = false;
         }

         if (m_text == null) {
            m_text = text;
         } else {
            m_text += text;
         }
      }

      @Override
      public String toString() {
         return m_name + "[" + m_keyValue + "]";
      }
   }

   static class NodeDefinition {
      private String m_name;

      private String m_keyName;

      private boolean m_noNamespace;

      private Map<String, NodeDefinition> m_children;

      public NodeDefinition(String name, String keyName, boolean noNamespace) {
         m_name = name;
         m_keyName = keyName;
         m_noNamespace = noNamespace;
         m_children = new HashMap<String, NodeDefinition>();
      }

      public void addChildNodeDefinition(NodeDefinition child) {
         m_children.put(child.getName(), child);
      }

      public NodeDefinition findChildNodeDefinition(String name) {
         return m_children.get(name);
      }

      public String getKeyValue(Attributes attrs) {
         if (m_keyName == null) {
            return "";
         }

         int length = attrs.getLength();
         String[] names = m_keyName.split(",");

         for (String name : names) {

            for (int i = 0; i < length; i++) {
               String attrName = attrs.getQName(i);

               if (attrName.equals(name)) {
                  String value = attrs.getValue(i);

                  return value;
               }
            }
         }

         return "";
      }

      public String getName() {
         return m_name;
      }

      public boolean hasNoNamespace() {
         return m_noNamespace;
      }

      @Override
      public String toString() {
         return m_name + "[" + m_keyName + "]";
      }
   }

   static class NodeDefinitionParser extends XmlHandler {
      private Stack<NodeDefinition> m_objs = new Stack<NodeDefinition>(); // ELEMENT

      // object

      private NodeDefinition m_root;

      @Override
      public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
         m_objs.pop();
      }

      public void setRoot(NodeDefinition root) {
         m_root = root;
      }

      @Override
      public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
         String tag = localName;

         try {
            if (tag.equals("root")) {
               if (m_objs.isEmpty()) {
                  m_objs.push(m_root);
               } else {
                  throw new SAXException(tag + " must be first element");
               }
            } else if (tag.equals("node")) {
               NodeDefinition parent = m_objs.peek();
               String name = attrs.getValue("name");

               if (name == null) {
                  throw new SAXException("attribute name is not specified under " + parent);
               }

               String keyName = attrs.getValue("key");
               String noNamespace = attrs.getValue("no-namespace");
               NodeDefinition child = new NodeDefinition(name, keyName, "true".equals(noNamespace));

               parent.addChildNodeDefinition(child);
               m_objs.push(child);
            } else {
               String location = (m_objs.isEmpty() ? "" : " under " + m_objs.peek());

               throw new SAXException("Unknown tag(" + rawName + ") is found" + location);
            }
         } catch (RuntimeException e) {
            e.printStackTrace();

            throw new SAXException(e);
         }
      }
   }

   static class NodeParser extends XmlHandler {
      // ELEMENT object
      private Stack<Node> m_nodes = new Stack<Node>();

      // ELEMENT tag
      private Stack<NodeDefinition> m_defs = new Stack<NodeDefinition>();

      private Node m_root;

      private NodeDefinition m_definition;

      @Override
      public void characters(char[] ch, int start, int length) throws SAXException {
         String text = new String(ch, start, length);

         if (text.trim().length() > 0) {
            Node current = m_nodes.peek();

            current.setText(text);
         }
      }

      @Override
      public void endElement(String namespaceURI, String localName, String rawName) throws SAXException {
         m_nodes.pop();
         m_defs.pop();
      }

      public void setRootNode(Node root) {
         m_root = root;
         m_definition = root.getDefinition();

         m_nodes.push(m_root);
         m_defs.push(m_definition);
      }

      @Override
      public void startElement(String namespaceURI, String localName, String rawName, Attributes attrs) throws SAXException {
         try {
            NodeDefinition parentDef = m_defs.peek();
            NodeDefinition childDef = (parentDef == null ? null : parentDef.findChildNodeDefinition(localName));
            Node parent = m_nodes.peek();
            Node child;

            if (childDef == null) {
               child = new Node(localName);
            } else {
               child = new Node(childDef);
            }

            child.loadAttributes(attrs);

            Node node = parent.findChildByKeyValue(localName, child.getKeyValue());

            if (node == null) {
               parent.addChild(child);
               m_nodes.push(child);
            } else {
               node.mergeAttributes(child);
               m_nodes.push(node);
            }

            m_nodes.peek().setOverwriteText(true);
            m_defs.push(childDef);
         } catch (RuntimeException e) {
            throw new SAXException(e);
         }
      }
   }
}
