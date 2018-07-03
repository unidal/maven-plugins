package org.unidal.maven.plugin.wizard.webapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.helper.PropertyProviders;
import org.unidal.codegen.helper.PropertyProviders.ConsoleProvider;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;
import org.unidal.maven.plugin.wizard.model.entity.Module;
import org.unidal.maven.plugin.wizard.model.entity.Page;
import org.unidal.maven.plugin.wizard.model.entity.Webapp;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.unidal.maven.plugin.wizard.model.transform.DefaultSaxParser;
import org.xml.sax.SAXException;

@Named
public class WebAppWizardBuilder implements LogEnabled {
	@Inject
	private WizardMeta m_wizardMeta;

	private Logger m_logger;

	private MavenProject m_project;

	public Wizard build(MavenProject project, File manifestFile) throws IOException, SAXException {
		m_project = project;
		File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");
		Wizard wizard = buildWizard(wizardFile);

		if (!manifestFile.exists()) {
			saveXml(m_wizardMeta.getManifest("wizard.xml"), manifestFile);
		}

		return wizard;
	}

	private Wizard buildWizard(File wizardFile) throws IOException, SAXException {
		Wizard wizard;

		if (wizardFile.isFile()) {
			String content = Files.forIO().readFrom(wizardFile, "utf-8");

			wizard = DefaultSaxParser.parse(content);
		} else {
			String packageName = getPackageName();

			wizard = new Wizard();
			wizard.setPackage(packageName);
		}

		wizard.accept(new Builder());
		Files.forIO().writeTo(wizardFile, wizard.toString());
		m_logger.info("File " + wizardFile.getCanonicalPath() + " generated.");
		return wizard;
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	private String getPackageName() {
		String groupId = m_project.getGroupId();
		String artifactId = m_project.getArtifactId();
		int index = artifactId.lastIndexOf('-');
		String packageName = (groupId + "." + artifactId.substring(index + 1)).replace('-', '.');

		packageName = PropertyProviders.fromConsole().forString("package", "Please input project-level package name:",
				packageName, null);
		return packageName;
	}

	private void saveXml(Document doc, File file) throws IOException {
		File parent = file.getCanonicalFile().getParentFile();

		if (!parent.exists()) {
			parent.mkdirs();
		}

		Format format = Format.getPrettyFormat().setIndent("   ");
		XMLOutputter outputter = new XMLOutputter(format);
		FileWriter writer = new FileWriter(file);

		try {
			outputter.output(doc, writer);
			m_logger.info("File " + file.getCanonicalPath() + " generated.");
		} finally {
			writer.close();
		}
	}

	static class Builder extends BaseVisitor {
		private Webapp m_webapp;

		@Override
		public void visitModule(Module module) {
			List<String> pageNames = new ArrayList<String>(module.getPages().size());

			for (Page page : module.getPages()) {
				pageNames.add(page.getName());
			}

			ConsoleProvider console = PropertyProviders.fromConsole();
			String pageName = console.forString("page", "Select page name below or input a new one:", pageNames, null,
					null);
			Page page = module.findPage(pageName);

			if (page == null) {
				page = new Page(pageName);

				if (module.getPages().isEmpty()) {
					page.setDefault(true);
				}

				if (m_webapp.isModule()) {
					String defaultPackage = module.getPackage() + ".page";
					String packageName = console.forString("module.package", "Module package:", defaultPackage, null);

					page.setPackage(packageName);
				}

				String path = console.forString("page.path", "Page path:", pageName, null);
				String caption = Character.toUpperCase(pageName.charAt(0)) + pageName.substring(1);

				page.setPath(path);
				page.setTitle(caption);
				page.setDescription(caption);
				page.setTemplate("default");
				module.addPage(page);
			}

			visitPage(page);
		}

		@Override
		public void visitPage(Page page) {
		}

		@Override
		public void visitWebapp(Webapp webapp) {
			m_webapp = webapp;

			List<Module> modules = webapp.getModules();
			List<String> moduleNames = new ArrayList<String>(modules.size());

			for (Module module : modules) {
				moduleNames.add(module.getName());
			}

			ConsoleProvider console = PropertyProviders.fromConsole();
			String moduleName = console.forString("module", "Select module name below or input a new one:", moduleNames,
					null, null);
			Module module = webapp.findModule(moduleName);

			if (module == null) { // new module
				module = new Module(moduleName);

				if (webapp.isModule()) {
					String defaultPackage = webapp.getPackage() + "." + moduleName;
					String packageName = console.forString("module.package", "Module package:", defaultPackage, null);

					module.setPackage(packageName);
				}

				String path = console.forString("module.path", "Module path:", moduleName, null);

				module.setPath(path);
				module.setDefault(modules.isEmpty());
				webapp.addModule(module);
			}

			visitModule(module);
		}

		@Override
		public void visitWizard(Wizard wizard) {
			Webapp webapp = wizard.getWebapp();

			if (webapp == null) {
				ConsoleProvider console = PropertyProviders.fromConsole();

				webapp = new Webapp();
				wizard.setWebapp(webapp);

				boolean module = webapp.isModule();

				if (webapp.getModule() == null) {
					boolean war = PropertyProviders.fromConsole().forBoolean("war", "Is it a web project?", true);

					module = !war;
					webapp.setModule(module);
				}

				String packageName = wizard.getPackage();

				webapp.setPackage(packageName);

				if (!module) {
					String defaultName = packageName.substring(packageName.lastIndexOf('.') + 1);
					String name = console.forString("name", "Webapp name:", defaultName, null);

					webapp.setName(name);
					
					boolean cat = console.forBoolean("cat", "Support CAT?", true);
					boolean jstl = console.forBoolean("jstl", "Support JSTL?", true);
					boolean bootstrap = console.forBoolean("layout", "Support bootstrap layout?", true);
					boolean pluginManagement = console.forBoolean("pluginManagement",
							"Support POM plugin management for Java Compiler and Eclipse?", false);

					webapp.setWebres(false);
					webapp.setCat(cat);
					webapp.setJstl(jstl);
					webapp.setLayout(bootstrap ? "bootstrap" : null);
					webapp.setPluginManagement(pluginManagement);
				}
			}

			visitWebapp(webapp);
		}
	}
}