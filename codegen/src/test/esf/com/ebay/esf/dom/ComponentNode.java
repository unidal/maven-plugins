package com.ebay.esf.dom;

public class ComponentNode extends TagNode {
   public static final String ESF_TAG_COMPONENT = "esf_tag_component";

   public ComponentNode(ITagNode parent) {
      super(ESF_TAG_COMPONENT, parent);
   }

   @Override
   public NodeType getNodeType() {
      return NodeType.COMPONENT;
   }
}
