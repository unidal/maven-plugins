package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.common.PropertyProviders.IValidator;
import org.unidal.maven.plugin.wizard.dom.Log4jXmlBuilder;
import org.unidal.maven.plugin.wizard.dom.PomXmlBuilder;
import org.unidal.maven.plugin.wizard.dom.WebXmlBuilder;

import org.unidal.helper.Files;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.helper.Splitters;

/**
 * Create the default CAT client configuration files.
 * 
 * @goal cat-config
 */
public class CatConfigMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Application domain.
    * 
    * @parameter expression="${domain}"
    */
   protected String domain;

   /**
    * CAT servers to connect to.
    * 
    * @parameter expression="${servers}"
    */
   protected String servers;

   /**
    * If the application is a web or service.
    * 
    * @parameter expression="${isService}"
    */
   protected String isService;

   protected Document createConfigFile() {
      Element config = new Element("config").setAttribute("mode", "client");
      Element parentServers = new Element("servers");
      List<String> endpoints = Splitters.by(',').trim().noEmptyItem().split(servers);

      for (String endpoint : endpoints) {
         int pos = endpoint.indexOf(':');
         String ip = null;
         int port = 2280;

         if (pos >= 0) {
            ip = endpoint.substring(0, pos);
            port = Integer.parseInt(endpoint.substring(pos + 1));
         } else {
            ip = endpoint;
         }

         Element server = new Element("server").setAttribute("ip", ip).setAttribute("port", String.valueOf(port));

         parentServers.addContent(server);
      }

      config.addContent(parentServers);
      config.addContent(new Element("domain").setAttribute("id", domain).setAttribute("enabled", "true"));
      return new Document(config);
   }

   protected Document createResourceFile() {
      Element config = new Element("config").setAttribute("mode", "client");

      config.addContent(new Element("domain").setAttribute("id", domain));
      return new Document(config);
   }

   protected List<String> detectLog4jConfigFile(File base) {
      final List<String> paths = new ArrayList<String>();

      Scanners.forDir().scan(base, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.equals("log4j.xml") || path.endsWith("/log4j.xml")) {
               paths.add(path);
            }

            return Direction.NEXT;
         }
      });

      return paths;
   }

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (prepare()) {
         try {
            Document config = createConfigFile();

            saveXml(config, new File("/data/appdatas/cat/client.xml"), false);

            if ("war".equals(m_project.getPackaging())) {
               Document resource = createResourceFile();
               File base = new File(m_project.getBasedir(), "src/main/resources");
               List<String> canidates = detectLog4jConfigFile(base);

               saveXml(resource, new File(m_project.getBasedir(), "src/main/resources/META-INF/cat/client.xml"), false);
               modifyPomFile(new File("pom.xml"));
               modifyWebFile(new File("src/main/webapp/WEB-INF/web.xml"), !"true".equals(isService));

               if (canidates.isEmpty()) {
                  getLog().warn(String.format("No log4j.xml file found at %s!", base.getCanonicalPath()));
               } else if (canidates.size() == 1) {
                  String log4jFile = PropertyProviders.fromConsole().forString("log4jFile", "Log4j configuration file:",
                        canidates.get(0), null);

                  modifyLog4jFile(new File(base, log4jFile));
               } else {
                  String log4jFile = PropertyProviders.fromConsole().forString("log4jFile",
                        "Log4j configuration files:" + canidates.toString(), null, null);

                  modifyLog4jFile(new File(base, log4jFile));
               }
            }
         } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute wizard.", e);
         }
      }
   }

   protected void modifyLog4jFile(File log4jFile) throws JDOMException, IOException {
      Document doc = buildDocument(log4jFile);
      Element root = doc.getRootElement();
      Log4jXmlBuilder b = new Log4jXmlBuilder();
      int index = b.indexOfLastElement(root, "appender");
      Element appender = new Element("appender").setAttribute("name", "catAppender") //
            .setAttribute("class", "com.dianping.cat.log4j.CatAppender");

      root.addContent(index + 1, appender);

      b.checkAppenderRefForRoot(root, "catAppender");
      b.checkAppenderRefForLoggers(root, "catAppender");

      if (b.isModified()) {
         saveXml(doc, log4jFile, true);
      }
   }

   protected void modifyPomFile(File pomFile) throws JDOMException, IOException {
      Document doc = buildDocument(pomFile);
      Element root = doc.getRootElement();
      PomXmlBuilder b = new PomXmlBuilder().setLog(getLog());
      Element dependencies = b.findOrCreateChild(root, "dependencies");
      String version = "0.6.2";

      if (!b.checkDependency(dependencies, "com.dianping.cat", "cat-core", version, null)) {
         b.checkDependency(dependencies, "com.dianping.zebra", "zebra-ds-monitor-client", "0.0.6.cat", null);
         b.checkDependency(dependencies, "com.dianping", "avatar-dao", "2.1.7", null);
         b.checkDependency(dependencies, "com.dianping", "avatar-cache", "2.2.4", null);
         b.checkDependency(dependencies, "com.dianping.dpsf", "dpsf-net", "1.6.1", null);
         b.checkDependency(dependencies, "com.dianping.hawk", "hawk-client", "0.6.7", null);
      }

      if (b.isModified()) {
         saveXml(doc, pomFile, true);
      }
   }

   protected void modifyWebFile(File webFile, boolean enableFilter) throws JDOMException, IOException {
      if (!webFile.exists()) {
         getLog().warn(String.format("File(%s) is not found, SKIPPED.", webFile.getCanonicalPath()));
         getLog().warn("You need do CAT integration manually.");
         return;
      }

      Document doc = buildDocument(webFile);
      Element root = doc.getRootElement();
      WebXmlBuilder b = new WebXmlBuilder();
      String version = root.getAttributeValue("version");

      if (version == null) {
         version = "2.3";
         b.setActiveNamespace(null);
      }

      b.findOrCreateListener(root, "com.dianping.cat.servlet.CatListener");

      if (enableFilter) {
         if ("2.3".equals(version)) {
            b.findOrCreateFilterMapping(root, "cat-filter", "/*");
         } else {
            b.findOrCreateFilterMapping(root, "cat-filter", "/*", "REQUEST", "FORWARD", "ERROR");
         }

         b.findOrCreateFilter(root, "cat-filter", "com.dianping.cat.servlet.CatFilter");
      }

      if (b.isModified()) {
         saveXml(doc, webFile, true);
         getLog().warn(String.format("File(%s) integrated, but you must have a double check.", webFile.getCanonicalPath()));
      }
   }

   protected Document buildDocument(File webFile) throws JDOMException, IOException {
      SAXBuilder builder = new SAXBuilder();

      builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

      Document doc = builder.build(webFile);
      return doc;
   }

   protected boolean prepare() throws MojoFailureException {
      if (!"war".equals(m_project.getPackaging())) {
         getLog().warn("A war project is required for the plugin to run against! IGNORED");
         return false;
      }

      if (domain == null) {
         domain = PropertyProviders.fromConsole().forString("domain", "Application domain(i.e. MobileApi):", null,
               new IValidator<String>() {
                  @Override
                  public boolean validate(String value) {
                     return !value.contains("-");
                  }
               });
      }

      if (isService == null) {
         isService = PropertyProviders.fromConsole().forString("isService", "Is it service?", "false", null);
      }

      if (servers == null) {
         servers = PropertyProviders.fromConsole().forString("servers",
               "CAT servers to connect to(format: <server>:port,<server>:<port>):", "192.168.7.70:2280", null);
      }

      return true;
   }

   protected void saveXml(Document doc, File file, boolean override) throws IOException {
      if (file.exists()) {
         if (!override) {
            getLog().info(String.format("File(%s) already exists. SKIPPED", file.getCanonicalPath()));
            return;
         } else {
            File backupFile = new File(file.getPath() + ".bak");

            if (!backupFile.exists()) {
               Files.forDir().copyFile(file, backupFile);
            }
         }
      }

      File parent = file.getCanonicalFile().getParentFile();

      if (!parent.exists()) {
         parent.mkdirs();
      }

      Format format = Format.getPrettyFormat().setIndent("   ");
      XMLOutputter outputter = new XMLOutputter(format);
      Writer writer = new OutputStreamWriter(new FileOutputStream(file), "utf-8");

      try {
         outputter.output(doc, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }
}
