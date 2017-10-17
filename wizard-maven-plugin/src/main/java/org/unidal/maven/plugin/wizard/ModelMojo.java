package org.unidal.maven.plugin.wizard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.Format.TextMode;
import org.jdom.output.XMLOutputter;
import org.unidal.codegen.meta.ModelMeta;
import org.unidal.codegen.meta.WizardMeta;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.helper.Transformers;
import org.unidal.helper.Transformers.IBuilder;
import org.unidal.maven.plugin.common.PropertyProviders;
import org.unidal.maven.plugin.common.PropertyProviders.IValidator;
import org.unidal.maven.plugin.pom.PomDelegate;
import org.unidal.maven.plugin.wizard.model.entity.Model;
import org.unidal.maven.plugin.wizard.model.entity.Wizard;
import org.unidal.maven.plugin.wizard.model.transform.BaseVisitor;
import org.unidal.maven.plugin.wizard.model.transform.DefaultSaxParser;
import org.unidal.tuple.Pair;
import org.xml.sax.SAXException;

/**
 * Enable project to build model.
 * 
 * @goal model
 * @author Frankie Wu
 */
public class ModelMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject m_project;

	/**
	 * Table meta component
	 * 
	 * @component
	 * @required
	 * @readonly
	 */
	protected ModelMeta m_modelMeta;

	/**
	 * Current project base directory
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 * @readonly
	 */
	protected File baseDir;

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
	 * @parameter expression="${manifest}" default-value= "${basedir}/src/main/resources/META-INF/wizard/model/manifest.xml"
	 * @required
	 */
	protected String manifest;

	/**
	 * Location of generated source directory
	 * 
	 * @parameter expression="${resource.base}" default-value="/META-INF/wizard/model"
	 * @required
	 */
	protected String resourceBase;

	/**
	 * @parameter expression="${outputDir}" default-value="${basedir}/src/main/resources/META-INF/dal/model"
	 * @required
	 */
	protected String outputDir;

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

	protected void buildManifest(Wizard wizard, Element manifestElement) {
		Set<String> all = new LinkedHashSet<String>();
		String text = manifestElement.getText();

		if (text != null) {
			List<String> lines = Splitters.by(',').noEmptyItem().trim().split(text);

			for (String line : lines) {
				all.add(line);
			}
		}

		StringBuilder sb = new StringBuilder();
		String indent = "                        ";

		sb.append("\r\n");

		for (Model model : wizard.getModels()) {
			String line = String.format("${basedir}/src/main/resources/META-INF/dal/model/%s-manifest.xml",
			      model.getName());

			all.add(line);
		}

		for (String line : all) {
			sb.append(indent);
			sb.append(line).append(",\r\n");
		}

		sb.append(indent.substring(3));
		manifestElement.addContent(new CDATA(sb.toString()));
	}

	protected Pair<Wizard, Model> buildWizard(File wizardFile) throws IOException, SAXException {
		Wizard wizard;

		if (wizardFile.isFile()) {
			String content = Files.forIO().readFrom(wizardFile, "utf-8");

			wizard = DefaultSaxParser.parse(content);
		} else {
			String packageName = getPackageName();

			wizard = new Wizard();
			wizard.setPackage(packageName);
		}

		WizardBuilder builder = new WizardBuilder();

		wizard.accept(builder);

		Files.forIO().writeTo(wizardFile, wizard.toString());
		getLog().info("File " + wizardFile.getCanonicalPath() + " generated.");
		return new Pair<Wizard, Model>(wizard, builder.getModel());
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			final File manifestFile = getFile(manifest);
			File wizardFile = new File(manifestFile.getParentFile(), "wizard.xml");
			Pair<Wizard, Model> pair = buildWizard(wizardFile);
			Model model = pair.getValue();

			generateModel(model);

			if (!manifestFile.exists()) {
				saveXml(m_wizardMeta.getManifest("wizard.xml"), manifestFile);
			}

			modifyPomFile(m_project.getFile(), pair.getKey());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Error when generating model meta: " + e, e);
		}
	}

	protected void generateModel(Model model) throws SQLException, IOException {
		String name = model.getName();
		File file = new File(model.getSampleModel());
		String sampleXml = Files.forIO().readFrom(file, "utf-8");

		Document codegenDoc = m_modelMeta.getCodegen(new StringReader(sampleXml));

		File outDir = getFile(outputDir);
		File outFile = new File(outDir, name + "-codegen.xml");

		if (!outDir.exists()) {
			outDir.mkdirs();
		}

		saveFile(codegenDoc, outFile);

		File modelFile = new File(outDir, name + "-model.xml");

		if (!modelFile.exists()) {
			Document modelDoc = m_modelMeta.getModel(model.getPackage());

			saveFile(modelDoc, modelFile);
		}

		File manifestFile = new File(outDir, name + "-manifest.xml");

		if (!manifestFile.exists()) {
			Document manifestDoc = m_modelMeta.getManifest(outFile.getName(), modelFile.getName());

			saveFile(manifestDoc, manifestFile);
		}
	}

	protected File getFile(String path) {
		File file;

		if (path.startsWith("/") || path.indexOf(':') == 1) {
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

	protected void modifyPomFile(File pomFile, Wizard wizard) throws JDOMException, IOException {
		Document doc = new SAXBuilder().build(pomFile);
		Element root = doc.getRootElement();
		PomDelegate b = new PomDelegate();

		Element build = b.findOrCreateChild(root, "build", null, "dependencies");
		Element plugins = b.findOrCreateChild(build, "plugins");
		Element codegenPlugin = b.checkPlugin(plugins, "org.unidal.maven.plugins", "codegen-maven-plugin", "3.0.0");
		Element codegenGenerate = b.checkPluginExecution(codegenPlugin, "dal-model", "generate-sources",
		      "generate dal model files");
		Element codegenGenerateConfiguration = b.findOrCreateChild(codegenGenerate, "configuration");
		Element manifestElement = b.findOrCreateChild(codegenGenerateConfiguration, "manifest");

		buildManifest(wizard, manifestElement);

		if (b.isModified()) {
			saveXml(doc, pomFile);
			getLog().info("You need run following command to setup eclipse environment:");
			getLog().info("   mvn eclipse:clean eclipse:eclipse");
		}
	}

	protected void saveFile(Document codegen, File file) throws IOException {
		Format format = Format.getPrettyFormat();
		XMLOutputter outputter = new XMLOutputter(format);
		FileWriter writer = new FileWriter(file);

		try {
			outputter.output(codegen, writer);
			getLog().info("File " + file.getCanonicalPath() + " generated.");
		} finally {
			writer.close();
		}
	}

	protected void saveXml(Document doc, File file) throws IOException {
		File parent = file.getCanonicalFile().getParentFile();

		if (!parent.exists()) {
			parent.mkdirs();
		}

		Format format = Format.getPrettyFormat().setIndent("   ").setTextMode(TextMode.TRIM_FULL_WHITE);
		XMLOutputter outputter = new XMLOutputter(format);
		FileWriter writer = new FileWriter(file);

		try {
			outputter.output(doc, writer);
			getLog().info("File " + file.getCanonicalPath() + " generated.");
		} finally {
			writer.close();
		}
	}

	class WizardBuilder extends BaseVisitor {
		private Model m_model;

		public Model getModel() {
			return m_model;
		}

		@SuppressWarnings({ "unchecked" })
		private String getRootName(File sampleFile) throws IOException {
			String xml = Files.forIO().readFrom(sampleFile, "utf-8");
			Document doc = m_modelMeta.getCodegen(new StringReader(xml));
			List<Element> children = doc.getRootElement().getChildren();

			if (!children.isEmpty()) {
				Element first = children.get(0);

				return first.getAttributeValue("name");
			}

			return null;
		}

		@Override
		public void visitWizard(final Wizard wizard) {
			List<String> names = Transformers.forList().transform(wizard.getModels(), new IBuilder<Model, String>() {
				@Override
				public String build(Model model) {
					return model.getName();
				}
			});
			String name = PropertyProviders.fromConsole().forString("model",
			      "Select model name below or input a sample xml file:", names, null, new IValidator<String>() {
				      @Override
				      public boolean validate(String value) {
					      if (wizard.findModel(value) != null) {
						      return true;
					      }

					      if (new File(value).isFile()) {
						      return true;
					      }

					      return false;
				      }
			      });
			Model model = wizard.findModel(name);

			if (model == null) {
				try {
					File sampleFile = new File(name);
					String rootName = getRootName(sampleFile);
					String prefix = PropertyProviders.fromConsole().forString("prefix", "Prefix name of target files:",
					      rootName, new IValidator<String>() {
						      @Override
						      public boolean validate(String value) {
							      if (wizard.findModel(value) != null) {
								      System.out.println("The prefix has been already used by others.");
								      return false;
							      }

							      return true;
						      }
					      });

					String defaultPackage = wizard.getPackage() + "." + prefix.substring(prefix.indexOf('-') + 1);
					String packageName = PropertyProviders.fromConsole().forString("package",
					      "Package name of generated model:", defaultPackage, null);

					model = new Model(prefix);

					model.setSampleModel(name);
					model.setPackage(packageName);
					wizard.addModel(model);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				File file = new File(model.getSampleModel());

				if (!file.exists()) {
					String sampleModel = PropertyProviders.fromConsole().forString("model.sample",
					      String.format("Sample model(%s) does not exist, please input a new one:", model.getSampleModel()),
					      null, null);

					model.setSampleModel(sampleModel);
				}
			}

			m_model = model;

			visitModel(model);
		}
	}
}
