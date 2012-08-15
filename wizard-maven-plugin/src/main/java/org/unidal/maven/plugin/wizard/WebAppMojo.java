package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.unidal.codegen.generator.AbstractGenerateContext;
import org.unidal.codegen.generator.GenerateContext;
import org.unidal.codegen.generator.Generator;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.DefaultDomParser;
import org.xml.sax.SAXException;

import com.site.helper.Files;

/**
 * Create a new page of web application project.
 * 
 * @goal webapp
 */
public class WebAppMojo extends AbstractMojo {
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
    * @component role="org.unidal.codegen.generator.Generator"
    *            role-hint="wizard-webapp"
    * @required
    * @readonly
    */
   protected Generator m_generator;

   /**
    * Wizard meta component
    * 
    * @component
    * @required
    * @readonly
    */
   protected WizardMeta m_meta;

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
    * @parameter expression="${manifest}" default-value=
    *            "${basedir}/src/main/resources/META-INF/wizard/webapp/manifest.xml"
    * @required
    */
   protected String manifest;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}"
    *            default-value="/META-INF/wizard/webapp"
    * @required
    */
   protected String resouceBase;

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

   private Wizard m_wizard;

   protected Wizard buildWizard(File wizardFile) throws IOException, SAXException {
      Wizard wizard;

      if (wizardFile.isFile()) {
         String content = Files.forIO().readFrom(wizardFile, "utf-8");

         wizard = new DefaultDomParser().parse(content);
      } else {
         Webapp webapp = new Webapp();

         String packageName = PropertyProviders.fromConsole().forString("package", "Java package name for webapp:", null, null);
         String defaultName = packageName.substring(packageName.lastIndexOf('.') + 1);
         String name = PropertyProviders.fromConsole().forString("name", "Name for webapp:", defaultName, null);
         boolean webres = PropertyProviders.fromConsole().forBoolean("webres", "Support WebRes framework?", false);
         boolean cat = PropertyProviders.fromConsole().forBoolean("cat", "Support CAT?", true);

         wizard = new Wizard();
         wizard.setWebapp(webapp);
         webapp.setPackage(packageName);
         webapp.setName(name);
         webapp.setWebres(webres);
         webapp.setCat(cat);
      }

      Webapp webapp = wizard.getWebapp();
      List<Module> modules = webapp.getModules();
      List<String> moduleNames = new ArrayList<String>(modules.size());

      for (Module module : modules) {
         moduleNames.add(module.getName());
      }

      String moduleName = PropertyProviders.fromConsole().forString("module", "Select module name below or input a new one:",
            moduleNames, null, null);
      Module module = webapp.findModule(moduleName);

      if (module == null) {
         String path = PropertyProviders.fromConsole().forString("module.path", "Module path:", moduleName.substring(0, 1), null);

         module = new Module(moduleName);

         module.setPath(path);
         module.setDefault(modules.isEmpty());
         webapp.addModule(module);
      }

      List<String> pageNames = new ArrayList<String>(module.getPages().size());

      for (Page page : module.getPages()) {
         pageNames.add(page.getName());
      }

      String pageName = PropertyProviders.fromConsole().forString("page", "Select page name below or input a new one:", pageNames,
            null, null);
      Page page = module.findPage(pageName);

      if (page == null) {
         String path = PropertyProviders.fromConsole().forString("page.path", "Page path:", pageName, null);

         page = new Page(pageName);

         if (module.getPages().isEmpty()) {
            page.setDefault(true);
         }

         String caption = Character.toUpperCase(pageName.charAt(0)) + pageName.substring(1);

         page.setPath(path);
         page.setTitle(caption);
         page.setDescription(caption);
         module.addPage(page);
      }

      return wizard;
   }

   protected void changePom(File pomFile, Webapp webapp) throws Exception {
      Document doc = new SAXBuilder().build(pomFile);
      Element root = doc.getRootElement();
      PomFileBuilder b = new PomFileBuilder();
      Element dependencies = b.findOrCreateChild(root, "dependencies");
      Element packaging = b.findOrCreateChild(root, "packaging", "dependencies", null);

      if (!"war".equals(packaging.getText())) {
         packaging.setText("war");
         getLog().info("Change project packaging type to war.");
      }

      if (!b.checkDependency(dependencies, "com.site.common", "web-framework", "1.0.15", null)) {
         b.checkDependency(dependencies, "com.site.common", "test-framework", "1.0.3", "test");

         if (webapp.isWebres()) {
            b.checkDependency(dependencies, "org.unidal.webres", "WebResServer", "1.2.0", null);
         }

         b.checkDependency(dependencies, "javax.servlet", "servlet-api", "2.5", "provided");
         b.checkDependency(dependencies, "junit", "junit", "4.8.1", "test");
         b.checkDependency(dependencies, "org.mortbay.jetty", "jetty", "6.1.14", "test");
         b.checkDependency(dependencies, "org.mortbay.jetty", "jsp-2.1", "6.1.14", "test");
      }

      Element build = b.findOrCreateChild(root, "build", null, "dependencies");
      Element pluginManagement = b.findOrCreateChild(build, "pluginManagement");
      Element pluginManagementPlugins = b.findOrCreateChild(pluginManagement, "plugins");
      Element compilerPlugin = b.checkPlugin(pluginManagementPlugins, null, "maven-compiler-plugin", "2.3.2");
      Element compilerConfiguration = b.findOrCreateChild(compilerPlugin, "configuration");

      b.findOrCreateChild(compilerConfiguration, "source").setText("1.6");
      b.findOrCreateChild(compilerConfiguration, "target").setText("1.6");

      Element eclipsePlugin = b.checkPlugin(pluginManagementPlugins, null, "maven-eclipse-plugin", "2.8");
      Element eclipseConfiguration = b.findOrCreateChild(eclipsePlugin, "configuration");

      b.findOrCreateChild(eclipseConfiguration, "downloadSources").setText("true");
      b.findOrCreateChild(eclipseConfiguration, "ajdtVersion").setText("none");

      Element additionalConfig = b.findOrCreateChild(eclipseConfiguration, "additionalConfig");
      Element file = b.findOrCreateChild(additionalConfig, "file");

      b.findOrCreateChild(file, "name").setText(".settings/org.eclipse.jdt.core.prefs");

      Element content = b.findOrCreateChild(file, "content");
      if (content.getChildren().isEmpty()) {
         content.addContent(new CDATA( //
               "org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.6\r\n" + //
                     "eclipse.preferences.version=1\r\n" + //
                     "org.eclipse.jdt.core.compiler.source=1.6\r\n" + //
                     "org.eclipse.jdt.core.compiler.compliance=1.6\r\n"));
      }

      Element plugins = b.findOrCreateChild(build, "plugins");
      Element codegenPlugin = b.checkPlugin(plugins, "org.unidal.maven.plugins", "codegen-maven-plugin", "1.1.4");
      Element codegenPlexus = b.checkPluginExecution(codegenPlugin, "plexus", "process-classes",
            "generate plexus component descriptor");
      Element codegenPlexusConfiguration = b.findOrCreateChild(codegenPlexus, "configuration");

      b.findOrCreateChild(codegenPlexusConfiguration, "className").setText(webapp.getPackage() + ".build.ComponentsConfigurator");

      if (b.isPomFileModified()) {
         saveXml(doc, pomFile);
         getLog().info(String.format("Added dependencies to POM file(%s).", pomFile));
         getLog().info("You need run following command to setup eclipse environment:");
         getLog().info("   mvn eclipse:clean eclipse:eclipse");
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final File manifestFile = getFile(manifest);
         File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");

         m_wizard = buildWizard(wizardFile);

         Reader reader = new StringReader(m_wizard.toString());

         if (!manifestFile.exists()) {
            saveXml(m_meta.getManifest("wizard.xml"), manifestFile);
         }

         saveXml(m_meta.getWizard(reader), wizardFile);

         final URL manifestXml = manifestFile.toURI().toURL();
         final GenerateContext ctx = new AbstractGenerateContext(m_project.getBasedir(), resouceBase, sourceDir) {
            public URL getManifestXml() {
               return manifestXml;
            }

            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().debug(message);
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
         m_project.addCompileSourceRoot(sourceDir);
         getLog().info(ctx.getGeneratedFiles() + " files generated.");

         changePom(m_project.getFile(), m_wizard.getWebapp());
      } catch (Exception e) {
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
