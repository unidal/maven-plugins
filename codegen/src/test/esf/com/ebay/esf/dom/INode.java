package com.ebay.esf.dom;

public interface INode {
   public void appendText(String text);

   public NodeType getNodeType();

   public INode getParent();
}
