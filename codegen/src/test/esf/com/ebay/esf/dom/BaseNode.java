package com.ebay.esf.dom;

public abstract class BaseNode implements INode {
   private INode m_parentNode;

   public BaseNode(ITagNode parent) {
      m_parentNode = parent;

      if (parent != null) {
         parent.add(this);
      }
   }

   protected abstract void asString(StringBuilder sb);

   protected String encode(String str) {
      int len = str.length();
      StringBuilder sb = new StringBuilder(len + 16);

      for (int i = 0; i < len; i++) {
         char ch = str.charAt(i);

         switch (ch) {
         case '<':
            sb.append("&lt;");
            break;
         case '>':
            sb.append("&gt;");
            break;
         case '"':
            sb.append("&quot;");
            break;
         default:
            sb.append(ch);
            break;
         }
      }

      return sb.toString();
   }

   public INode getParent() {
      return m_parentNode;
   }

   void setParent(INode node) {
      m_parentNode = node;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(1024);

      asString(sb);
      return sb.toString();
   }
}
