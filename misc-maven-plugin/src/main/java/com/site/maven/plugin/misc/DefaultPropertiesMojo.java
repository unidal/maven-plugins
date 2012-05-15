package com.site.maven.plugin.misc;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReflectionUtils;

/**
 * Set default properties for a maven project, suppose to be overriden by system
 * properties or maven properties defined in pom.xml or profiles.xml.
 * 
 * @goal default-properties
 */
public class DefaultPropertiesMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * @parameter expression="${profile}"
    */
   private String profile;

   /**
    * Default properties.
    * 
    * Sample configuration here:
    * 
    * <pre>
    *   &lt;properties&gt;
    *      &lt;!-- property(localhost) will be your host name resolved automatically --&gt;
    *      &lt;poolname&gt;${localhost}.site.com&lt;/poolname&gt;
    *      &lt;ws.host&gt;${poolname}&lt;/ws.host&gt;
    *      &lt;ws.port&gt;80&lt;/ws.port&gt;
    *      &lt;!-- Flex SDK 2 &amp; 3 installation directory --&gt;
    *      &lt;flex.sdk2.home&gt;${flex.basedir}/target/flex_sdk_2&lt;/flex.sdk2.home&gt;
    *      &lt;flex.sdk3.home&gt;${flex.basedir}/target/flex_sdk_3&lt;/flex.sdk3.home&gt;
    *   &lt;/properties&gt;
    * </pre>
    * 
    * @parameter
    */
   private Map<String, String> properties = new HashMap<String, String>();

   /**
    * Dynamic properties.
    * 
    * Sample configuration here:
    * 
    * <pre>
    *   &lt;dynamicProperties&gt;
    *      &lt;resolve-path property-name=&quot;flex.basedir&quot; from-dir=&quot;${basedir}&quot;&gt;
    *         &lt;contain-file&gt;flex_sdk_2.zip&lt;/contain-file&gt;
    *         &lt;contain-file&gt;flex_sdk_3.zip&lt;/contain-file&gt;
    *      &lt;/resolve-path&gt;
    *      &lt;resolve-localhost property-name=&quot;localhost&quot;/&gt;
    *   &lt;/dynamicProperties&gt;
    * </pre>
    * 
    * @parameter
    */
   private boolean dynamicProperties;

   /**
    * Resolve the machine name of local host and set to a property with
    * specified name
    */
   private ResolveLocalhost m_resolveLocalhost;

   /**
    * Resolve path starting from a directory and up, set the property with given
    * name to a directory, which contains files specified. It's useful for the
    * Flex builds, because we have to install the Flex SDK 2/3 before do Flex
    * compile.
    */
   private List<ResolvePath> m_resolvePath = new ArrayList<ResolvePath>();

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (project.getActiveProfiles().isEmpty() && profile != null) {
         activateProfile(profile);
      }

      if (dynamicProperties) {
         resolveLocalhost();
         resolvePath();
      }

      setDefaultProperties();
   }

   private void activateProfile(String id) throws MojoExecutionException {
      try {
         Properties p = getProfile(id).getProperties();

         for (Map.Entry<Object, Object> e : p.entrySet()) {
            properties.put((String) e.getKey(), (String) e.getValue());
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Can't get profile(" + id + ")", e);
      }
   }

   private Profile getProfile(String id) throws Exception {
      MavenProject current = project;

      while (current != null) {
         Model model = getProjectModel(current);

         for (Profile p : (List<Profile>) model.getProfiles()) {
            if (id.equals(p.getId())) {
               return p;
            }
         }

         current = current.getParent();
      }

      throw new IllegalArgumentException("No profile(" + id + ") found");
   }

   private Model getProjectModel(MavenProject mavenProject) throws IllegalAccessException {
      Field field = ReflectionUtils.getFieldByNameIncludingSuperclasses("model", mavenProject.getClass());

      field.setAccessible(true);
      return (Model) field.get(mavenProject);
   }

   private void resolvePath() throws MojoExecutionException {
      for (ResolvePath e : m_resolvePath) {
         if (!e.validate()) {
            getLog().warn("Invalid " + e);
         }

         File dir = e.getFromDir();
         while (dir != null) {
            boolean found = true;

            for (String file : e.getContainedFiles()) {
               if (!new File(dir, file).exists()) {
                  found = false;
                  break;
               }
            }

            if (found) {
               properties.put(e.getPropertyName(), dir.getAbsolutePath());
               break;
            } else {
               dir = dir.getParentFile();
            }
         }

         if (dir == null) {
            throw new MojoExecutionException("Fail to resolve path: " + e);
         }
      }
   }

   private void setDefaultProperties() {
      Properties sysProperties = System.getProperties();
      Properties projProperties = project.getProperties();
      Set<String> todos = new HashSet<String>();

      for (Map.Entry<String, String> e : properties.entrySet()) {
         String key = e.getKey();

         if (projProperties.containsKey(key)) {
            getLog().debug("Property defined in pom.xml or profiles.xml: " + key + ", ignored.");
         } else if (sysProperties.containsKey(key)) {
            getLog().debug("Property found in the system properties: " + key + ", ignored.");
         } else {
            todos.clear();

            String value = resolve(todos, sysProperties, projProperties, properties, e.getValue());

            projProperties.put(key, value);
            getLog().info("Set default property: " + key + "=" + value);
         }
      }
   }

   private void resolveLocalhost() {
      // Resolve local host name
      if (m_resolveLocalhost != null) {
         try {
            String propertyName = m_resolveLocalhost.getPropertyName();
            String hostName = InetAddress.getLocalHost().getHostName();

            properties.put(propertyName, hostName);
         } catch (UnknownHostException e) {
            getLog().warn("Fail to resolve local host machine name: " + e, e);
         }
      }
   }

   public static void main(String[] argv) {
      Properties sys = System.getProperties();
      Properties proj = new Properties();
      Map<String, String> p = new HashMap<String, String>();

      p.put("first", "${second}-1");
      p.put("second", "${third}-1");
      p.put("third", "3");

      System.out.println(new DefaultPropertiesMojo().resolve(new HashSet<String>(), sys, proj, p, "first=${first}"));
      System.out.println(new DefaultPropertiesMojo().resolve(new HashSet<String>(), sys, proj, p, "second=${second}"));
      System.out.println(new DefaultPropertiesMojo().resolve(new HashSet<String>(), sys, proj, p, "third=${third}"));
   }

   private String resolve(Set<String> todos, Properties systemProps, Properties projectProperties,
         Map<String, String> userProperties, String rawValue) {
      if (rawValue == null || rawValue.length() == 0) {
         return rawValue;
      }

      StringBuffer sb = new StringBuffer(rawValue.length());
      int start = 0;

      while (true) {
         int pos1 = rawValue.indexOf("${", start);
         int pos2 = rawValue.indexOf('}', pos1 + 1);

         if (pos1 < 0 || pos2 < 0) {
            sb.append(rawValue.substring(start));
            break;
         } else {
            String token = rawValue.substring(pos1 + 2, pos2);
            String value = systemProps.getProperty(token, null);

            if (value == null && projectProperties != null) {
               value = projectProperties.getProperty(token, null);
            }

            if (value == null && userProperties != null) {
               if (todos.contains(token)) {
                  value = "${" + token + "}";
               } else {
                  todos.add(token);
                  value = resolve(todos, systemProps, projectProperties, userProperties, userProperties.get(token));
               }
            }

            sb.append(rawValue.substring(start, pos1));

            if (value != null) {
               sb.append(value);
            } else {
               // not found, add it back
               sb.append("${").append(token).append('}');
            }

            start = pos2 + 1;
         }
      }

      return sb.toString();
   }

   public void setProperties(PlexusConfiguration config) {
      PlexusConfiguration[] children = config.getChildren();

      for (PlexusConfiguration child : children) {
         String name = child.getName();
         String value = child.getValue("");

         properties.put(name, value);
      }
   }

   public void setDynamicProperties(PlexusConfiguration config) {
      PlexusConfiguration resolveLocalhost = config.getChild("resolve-localhost", false);

      if (resolveLocalhost != null) {
         String propertyName = resolveLocalhost.getAttribute("property-name", "localhost");

         m_resolveLocalhost = new ResolveLocalhost();
         m_resolveLocalhost.setPropertyName(propertyName);
      }

      PlexusConfiguration[] resolvePaths = config.getChildren("resolve-path");

      for (PlexusConfiguration resolvePath : resolvePaths) {
         String propertyName = resolvePath.getAttribute("property-name", "");
         String fromDir = resolvePath.getAttribute("from-dir", "");
         PlexusConfiguration[] children = resolvePath.getChildren();
         List<String> containedFiles = new ArrayList<String>();
         ResolvePath path = new ResolvePath();

         for (PlexusConfiguration child : children) {
            containedFiles.add(child.getValue(""));
         }

         path.setPropertyName(propertyName);
         path.setFromDir(new File(fromDir));
         path.setContainedFiles(containedFiles);

         m_resolvePath.add(path);
      }

      dynamicProperties = true;
   }

   static final class ResolveLocalhost {
      private String m_propertyName;

      public String getPropertyName() {
         return m_propertyName;
      }

      public void setPropertyName(String propertyName) {
         m_propertyName = propertyName;
      }

      @Override
      public String toString() {
         return "resolve-localhost[property-name=" + m_propertyName + "]";
      }
   }

   static final class ResolvePath {
      private String m_propertyName;

      private File m_fromDir;

      private List<String> m_containedFiles;

      public boolean validate() {
         if (m_propertyName == null || m_propertyName.length() == 0) {
            return false;
         } else if (m_fromDir == null || !m_fromDir.isDirectory()) {
            return false;
         } else if (m_containedFiles == null || m_containedFiles.size() == 0) {
            return false;
         } else {
            return true;
         }
      }

      public List<String> getContainedFiles() {
         return m_containedFiles;
      }

      public void setContainedFiles(List<String> containedFiles) {
         m_containedFiles = containedFiles;
      }

      public File getFromDir() {
         return m_fromDir;
      }

      public void setFromDir(File fromDir) {
         m_fromDir = fromDir;
      }

      public String getPropertyName() {
         return m_propertyName;
      }

      public void setPropertyName(String propertyName) {
         m_propertyName = propertyName;
      }

      @Override
      public String toString() {
         return "resolve-path[property-name=" + m_propertyName + ", from-dir=" + m_fromDir + ", contained-files="
               + m_containedFiles + "]";
      }
   }
}
