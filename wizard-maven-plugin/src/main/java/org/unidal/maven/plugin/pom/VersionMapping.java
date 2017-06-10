package org.unidal.maven.plugin.pom;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Named;
import org.unidal.tuple.Triple;

@Named
public class VersionMapping implements Initializable, LogEnabled {
   private Map<String, Triple<String, String, String>> m_latestVersions = new HashMap<String, Triple<String, String, String>>();

   private Logger m_logger;

   @Override
   public void initialize() throws InitializationException {
      map("javax.servlet", "servlet-api", "2.5");
      map("javax.servlet", "jstl", "1.2");

      map("org.unidal.framework", "web-framework", "3.0.4");
      map("org.unidal.webres", "WebResServer", "1.2.1");
   }

   private void map(String groupId, String artifactId, String version) {
      map(artifactId, groupId, artifactId, version);
   }

   private void map(String id, String groupId, String artifactId, String version) {
      m_logger.info(String.format("%s => %s:%s:%s", id, groupId, artifactId, version));
      m_latestVersions.put(id, new Triple<String, String, String>(groupId, artifactId, version));
   }

   @Override
   public void enableLogging(Logger logger) {
      m_logger = logger;
   }

   public Triple<String, String, String> findById(String id) {
      return m_latestVersions.get(id);
   }
}
