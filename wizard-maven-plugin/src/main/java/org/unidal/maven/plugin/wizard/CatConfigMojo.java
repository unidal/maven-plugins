package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.site.helper.Files;
import com.site.helper.Splitters;

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

   protected void modifyPomFile() throws JDOMException, IOException {
      File pomFile = new File("pom.xml");
      Document doc = new SAXBuilder().build(pomFile);
      Element root = doc.getRootElement();
      PomFileBuilder b = new PomFileBuilder();
      Element dependencies = b.findOrCreateChild(root, "dependencies");
      String version = "0.3.4";

      b.checkDependency(dependencies, "com.dianping.cat", "cat-core", version, null);

      if (b.isPomFileModified()) {
         saveXml(doc, pomFile, true);
         getLog().info(String.format("Dependency(com.dianping.cat:cat-core:%s) added.", version));
      }
   }

   protected void modifyWebFile() throws JDOMException, IOException {
      File webFile = new File("src/main/webapp/WEB-INF/web.xml");

      if (!webFile.exists()) {
         getLog().warn(String.format("File(%s) is not found, SKIPPED.", webFile.getCanonicalPath()));
         getLog().warn("You need do CAT integration manually.");
         return;
      }

      Document doc = new SAXBuilder().build(webFile);
      Element root = doc.getRootElement();
      WebFileBuilder b = new WebFileBuilder();

      b.findOrCreateListener(root, "com.dianping.cat.servlet.CatListener");
      b.findOrCreateFilter(root, "cat-filter", "com.dianping.cat.servlet.CatFilter");
      b.findOrCreateFilterMapping(root, "cat-filter", "/*", "REQUEST", "FORWARD", "ERROR");

      if (b.isWebFileModified()) {
         saveXml(doc, webFile, true);
         getLog().warn(String.format("File(%s) integrated, but you must have a double check.", webFile.getCanonicalPath()));
      }
   }

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

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (prepare()) {
         try {
            Document config = createConfigFile();

            saveXml(config, new File("/data/appdatas/cat/client.xml"), false);

            if ("war".equals(m_project.getPackaging())) {
               Document resource = createResourceFile();

               saveXml(resource, new File(m_project.getBasedir(), "src/main/resources/META-INF/cat/client.xml"), false);
               modifyWebFile();
               modifyPomFile();
            }
         } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute wizard.", e);
         }
      }
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
      FileWriter writer = new FileWriter(file);

      try {
         outputter.output(doc, writer);
         getLog().info("File " + file.getCanonicalPath() + " generated.");
      } finally {
         writer.close();
      }
   }
}
