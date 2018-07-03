package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.GenerateContextSupport;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.builder.CatPluginWizardBuilder;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

/**
 * Create a cat plugin project.
 * 
 * @goal cat-plugin
 */
public class CatPluginMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Current project base directory
    * 
    * @parameter expression="${basedir}"
    * @required
    * @readonly
    */
   protected File baseDir;

   /**
    * XSL code generator implementation
    * 
    * @component role="org.unidal.codegen.generator.Generator" role-hint="wizard-webapp"
    * @required
    * @readonly
    */
   protected Generator m_generator;

   /**
    * XSL code generator implementation
    * 
    * @component role="org.unidal.codegen.aggregator.XmlAggregator" role-hint="wizard-webapp"
    * @required
    * @readonly
    */
   protected XmlAggregator m_aggregator;

   /**
    * Wizard meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected WizardMeta m_wizardMeta;

   /**
    * Current project base directory
    * 
    * @parameter expression="${sourceDir}" default-value="${basedir}"
    * @required
    */
   protected String sourceDir;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}" default-value= "${basedir}/src/main/resources/META-INF/wizard/webapp/manifest.xml"
    * @required
    */
   protected String manifest;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}" default-value="/META-INF/wizard/webapp"
    * @required
    */
   protected String resourceBase;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   protected boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   protected boolean debug;

   protected String m_resourceFolder = "src/main/resources";

   protected Wizard buildWizard(File wizardFile) throws IOException, SAXException {
      Wizard wizard;

      if (wizardFile.isFile()) {
         String content = Files.forIO().readFrom(wizardFile, "utf-8");

         wizard = DefaultSaxParser.parse(content);
      } else {
         String packageName = getPackageName();

         wizard = new Wizard();
         wizard.setPackage(packageName);
      }

      wizard.accept(new CatPluginWizardBuilder());
      Files.forIO().writeTo(wizardFile, wizard.toString());
      getLog().info("File " + wizardFile.getCanonicalPath() + " generated.");
      return wizard;
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (m_project.getPackaging().equals("pom")) {
         throw new MojoFailureException("This wizard can not be run against a POM project!");
      }

      try {
         final File manifestFile = getFile(manifest);
         File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");
         Wizard wizard = buildWizard(wizardFile);

         if (!manifestFile.exists()) {
            saveXml(m_wizardMeta.getManifest("wizard.xml"), manifestFile);
         }

         final URL manifestXml = manifestFile.toURI().toURL();
         final GenerateContext ctx = new GenerateContextSupport(resourceBase, new File(sourceDir)) {
            @Override
            protected void configure(Map<String, String> properties) {
               File templateFile = new File(manifestFile.getParentFile(), "template.xml");

               if (templateFile.exists()) {
                  properties.put("template-file", "file:" + templateFile.getPath());
               }
            }

            public URL getManifestXml() {
               return manifestXml;
            }

            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().info(message);
                  }
                  break;
               case INFO:
                  if (debug || verbose) {
                     getLog().info(message);
                  }
                  break;
               case ERROR:
                  getLog().error(message);
                  break;
               }
            }
         };

         m_generator.generate(ctx);
         getLog().info(ctx.getGeneratedFiles() + " files generated.");

         modifyPomFile(m_project.getFile(), wizard, wizard.getWebapp());
      } catch (Exception e) {
         e.printStackTrace();
         throw new MojoExecutionException("Code generating failed.", e);
      }
   }

   protected File getFile(String path) {
      File file;

      if (path.startsWith("/") || path.indexOf(':') > 0) {
         file = new File(path);
      } else {
         file = new File(baseDir, path);
      }

      return file;
   }

   protected String getPackageName() {
      String groupId = m_project.getGroupId();
      String artifactId = m_project.getArtifactId();
      int index = artifactId.lastIndexOf('-');
      String packageName = (groupId + "." + artifactId.substring(index + 1)).replace('-', '.');

      packageName = PropertyProviders.fromConsole().forString("package", "Please input project-level package name:",
            packageName, null);
      return packageName;
   }

   protected void modifyPomFile(File pomFile, Wizard wizard, Webapp webapp) throws Exception {
      Document doc = new SAXBuilder().build(pomFile);
      Element root = doc.getRootElement();
      PomDelegate b = new PomDelegate();
      Element dependencies = b.findOrCreateChild(root, "dependencies");
      Element packaging = b.findChild(root, "packaging");
      String projectType = packaging == null ? "jar" : packaging.getText();

      if (!webapp.isModule() && "jar".equals(projectType)) {
         getLog().info(String.format("Change project packaging type from %s to war.", projectType));

         b.findOrCreateChild(root, "packaging", "dependencies", null).setText("war");
      }

      // dependencies
      b.checkDependency(dependencies, "com.dianping.cat", "cat-foundation", "", null);

      if (!b.checkDependency(dependencies, "org.unidal.framework", "web-framework", "2.2.0", null)) {
         if (webapp.isJstl()) {
            b.checkDependency(dependencies, "javax.servlet", "jstl", "1.2", null);
         }

         if (webapp.isWebres()) {
            b.checkDependency(dependencies, "org.unidal.webres", "WebResServer", "1.2.1", null);
         }

         b.checkDependency(dependencies, "javax.servlet", "servlet-api", "2.5", "provided");
         b.checkDependency(dependencies, "junit", "junit", "4.8.1", "test");
         b.checkDependency(dependencies, "org.unidal.framework", "foundation-service", "2.2.0", null);
         b.checkDependency(dependencies, "org.unidal.framework", "test-framework", "2.2.0", "test");
         b.checkDependency(dependencies, "org.mortbay.jetty", "jetty", "6.1.14", "test");
         b.checkDependency(dependencies, "org.mortbay.jetty", "jsp-2.1", "6.1.14", "test");
      }

      Element build = b.findOrCreateChild(root, "build", null, "dependencies");

      // pluginManagement
      if (webapp.isPluginManagement()) {
         Element pluginManagement = b.findOrCreateChild(build, "pluginManagement");
         Element pluginManagementPlugins = b.findOrCreateChild(pluginManagement, "plugins");
         Element compilerPlugin = b.checkPlugin(pluginManagementPlugins, null, "maven-compiler-plugin", "2.5.1");
         Element compilerConfiguration = b.findOrCreateChild(compilerPlugin, "configuration");

         b.findOrCreateChild(compilerConfiguration, "source").setText("1.6");
         b.findOrCreateChild(compilerConfiguration, "target").setText("1.6");

         Element eclipsePlugin = b.checkPlugin(pluginManagementPlugins, null, "maven-eclipse-plugin", "2.9");
         Element eclipseConfiguration = b.findOrCreateChild(eclipsePlugin, "configuration");

         b.findOrCreateChild(eclipseConfiguration, "downloadSources").setText("true");
         b.findOrCreateChild(eclipseConfiguration, "ajdtVersion").setText("none");

         Element additionalConfig = b.findOrCreateChild(eclipseConfiguration, "additionalConfig");
         Element file = b.findOrCreateChild(additionalConfig, "file");

         b.findOrCreateChild(file, "name").setText(".settings/org.eclipse.jdt.core.prefs");

         Element contentElement = b.findOrCreateChild(file, "content");
         if (contentElement.getChildren().isEmpty()) {
            contentElement.addContent(new CDATA( //
                  "org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.6\r\n" + //
                        "eclipse.preferences.version=1\r\n" + //
                        "org.eclipse.jdt.core.compiler.source=1.6\r\n" + //
                        "org.eclipse.jdt.core.compiler.compliance=1.6\r\n"));
         }
      }

      // plugins
      Element plugins = b.findOrCreateChild(build, "plugins");
      Element plexusPlugin = b.checkPlugin(plugins, "org.unidal.maven.plugins", "plexus-maven-plugin", "2.2.0");
      Element plexus = b.checkPluginExecution(plexusPlugin, "plexus", null, "generate plexus component descriptor");
      Element codegenPlexusConfiguration = b.findOrCreateChild(plexus, "configuration");

      b.findOrCreateChild(codegenPlexusConfiguration, "className").setText(
            wizard.getPackage() + ".build.ComponentsConfigurator");

      // properties
      Element properties = b.findOrCreateChild(root, "properties");
      Element sourceEncoding = b.findOrCreateChild(properties, "project.build.sourceEncoding");

      if (sourceEncoding.getText().length() == 0) {
         sourceEncoding.setText("utf-8");
      }

      if (b.isModified()) {
         saveXml(doc, pomFile);
         getLog().info(String.format("Added dependencies to POM file(%s).", pomFile));
         getLog().info("You need run following command to setup eclipse environment:");
         getLog().info("   mvn eclipse:clean eclipse:eclipse");
      }
   }

   protected void saveXml(Document doc, File file) throws IOException {
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
