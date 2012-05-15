package com.ebay.esf.dom;

public class TextNode extends BaseNode {
   private String m_nodeValue;

   public TextNode(String value, ITagNode parent) {
      super(parent);

      m_nodeValue = value;
   }

   public void appendText(String nodeValue) {
      if (nodeValue != null) {
         if (m_nodeValue != null) {
            m_nodeValue += nodeValue;
         } else {
            m_nodeValue = nodeValue;
         }
      }
   }

   @Override
   protected void asString(StringBuilder sb) {
      sb.append(encode(m_nodeValue));
   }

   public NodeType getNodeType() {
      return NodeType.TEXT;
   }

   public String getNodeValue() {
      return m_nodeValue;
   }
}
