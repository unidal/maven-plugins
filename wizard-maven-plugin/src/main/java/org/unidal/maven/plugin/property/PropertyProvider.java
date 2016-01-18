package org.unidal.maven.plugin.property;

public interface PropertyProvider {
   public String getProperty(String property, String defaultValue);
}
