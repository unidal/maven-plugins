package com.ebay.esf.dom;

import java.util.List;
import java.util.Map;

public interface ITagNode extends INode {

   public void add(INode child);

   public String getAttribute(String name);

   public Map<String, String> getAttributes();

   public List<INode> getChildNodes();

   public ITagNode getChildTagNode(String childTagName);

   public List<ITagNode> getChildTagNodes();

   public List<ITagNode> getChildTagNodes(String childTagName);

   public INode getFirstChild();

   public List<ITagNode> getGrandchildTagNodes(String childTagName);

   public INode getLastChild();

   public String getNodeName();

   public boolean hasAttributes();

   public boolean hasChildNodes();

   public void remove(INode child);

   public void setAttribute(String name, String value);
}
