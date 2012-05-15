package com.ebay.esf.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Simple TagNode used for ESF TagXmlParser and Converter.
 */
public class TagNode extends BaseNode implements ITagNode {
   private String m_nodeName;

   private List<INode> m_childNodes;

   private Map<String, String> m_attributes;

   public TagNode(String name, ITagNode parent) {
      super(parent);

      m_nodeName = name;
   }

   public void add(INode node) {
      if (node.getParent() != null) {
         ((TagNode) node.getParent()).remove(node);
      }

      if (m_childNodes == null) {
         m_childNodes = new ArrayList<INode>();
      }

      m_childNodes.add(node);
      ((BaseNode) node).setParent(this);
   }

   public void appendText(String text) {
      if (m_childNodes != null && m_childNodes.size() > 0) {
         INode lastChild = m_childNodes.get(m_childNodes.size() - 1);

         if (lastChild.getNodeType() == NodeType.TEXT) {
            lastChild.appendText(text);
            return;
         }
      }

      // create a TextNode and append it
      new TextNode(text, this);
   }

   @Override
   protected void asString(StringBuilder sb) {
      sb.append('<').append(m_nodeName);

      if (m_attributes != null) {
         for (Map.Entry<String, String> e : m_attributes.entrySet()) {
            sb.append(' ').append(e.getKey());
            sb.append("=\"").append(encode(e.getValue())).append('"');
         }
      }

      sb.append('>');

      if (m_childNodes != null) {
         for (INode child : m_childNodes) {
            if (child instanceof BaseNode) {
               ((BaseNode) child).asString(sb);
            } else {
               sb.append(child);
            }
         }
      }

      sb.append("</").append(m_nodeName).append('>');
   }

   @Override
   public String getAttribute(String name) {
      if (m_attributes != null) {
         return m_attributes.get(name);
      } else {
         return null;
      }
   }

   public Map<String, String> getAttributes() {
      if (m_attributes == null) {
         return Collections.emptyMap();
      } else {
         return m_attributes;
      }
   }

   public List<INode> getChildNodes() {
      if (m_childNodes == null) {
         return Collections.emptyList();
      } else {
         return m_childNodes;
      }
   }

   @Override
   public ITagNode getChildTagNode(String childTagName) {
      if (m_childNodes != null) {
         for (INode node : m_childNodes) {
            if (node.getNodeType() != NodeType.TEXT) {
               ITagNode tag = (ITagNode) node;

               if (tag.getNodeName().equals(childTagName)) {
                  return tag;
               }
            }
         }
      }

      return null;
   }

   @Override
   public List<ITagNode> getChildTagNodes() {
      if (m_childNodes == null) {
         return Collections.emptyList();
      } else {
         List<ITagNode> nodes = new ArrayList<ITagNode>(m_childNodes.size());

         for (INode node : m_childNodes) {
            if (node.getNodeType() != NodeType.TEXT) {
               nodes.add((ITagNode) node);
            }
         }

         return nodes;
      }
   }

   @Override
   public List<ITagNode> getChildTagNodes(String childTagName) {
      if (m_childNodes == null) {
         return Collections.emptyList();
      } else {
         List<ITagNode> nodes = new ArrayList<ITagNode>(m_childNodes.size());

         for (INode node : m_childNodes) {
            if (node.getNodeType() != NodeType.TEXT) {
               ITagNode tag = (ITagNode) node;

               if (tag.getNodeName().equals(childTagName)) {
                  nodes.add(tag);
               }
            }
         }

         return nodes;
      }
   }

   public INode getFirstChild() {
      if (hasChildNodes()) {
         return m_childNodes.get(0);
      }

      return null;
   }

   @Override
   public List<ITagNode> getGrandchildTagNodes(String childTagName) {
      ITagNode childTag = getChildTagNode(childTagName);

      if (childTag == null) {
         return Collections.emptyList();
      } else {
         return childTag.getChildTagNodes();
      }
   }

   public INode getLastChild() {
      if (hasChildNodes()) {
         return m_childNodes.get(m_childNodes.size() - 1);
      }

      return null;
   }

   public String getNodeName() {
      return m_nodeName;
   }

   public NodeType getNodeType() {
      return NodeType.TAG;
   }

   public boolean hasAttributes() {
      return m_attributes != null && !m_attributes.isEmpty();
   }

   public boolean hasChildNodes() {
      return m_childNodes != null && !m_childNodes.isEmpty();
   }

   public void remove(INode node) {
      if (hasChildNodes()) {
         if (getChildNodes().remove(node)) {
            ((BaseNode) node).setParent(null);
         }
      }
   }

   public void setAttribute(String name, String value) {
      if (m_attributes == null) {
         m_attributes = new LinkedHashMap<String, String>();
      }

      m_attributes.put(name, value);
   }
}
