package com.ebay.esf.tag.core;

public interface ITagEnv {
   public ITagEnv err(Object obj);

   public Object findAttribute(String name);

   public Object getProperty(String name);

   public String getError();

   public String getOutput();

   public Object getPageAttribute(String name);

   public void onError(String message, Throwable cause);

   public ITagEnv out(Object obj);

   public void setProperty(String name, Object value);

   public void setPageAttribute(String name, Object value);

   public String getContextPath();
}
