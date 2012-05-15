package com.ebay.webres.resource.api;

public interface IResourceUrn {
   public String getScheme();
   
   public String getNamespace();

   public String getPathInfo();

   public String getResourceId();

   public String getResourceTypeName();

   public void setPathInfo(String pathInfo);
}