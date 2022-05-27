package org.unidal.maven.plugin.source.pipeline;

import java.io.File;
import java.util.Stack;

import org.apache.maven.project.MavenProject;

public class DefaultSource implements Source {
	private DefaultSourcePipeline m_pipeline;

	private MavenProject m_project;

	private MavenProject m_module;

	// source root or resource root
	private String m_sourceRoot;

	private File m_folder;

	private String m_package;

	private String m_className;

	private File m_file;

	private String m_line;

	private Stack<SourceScope> m_scopes = new Stack<>();

	public DefaultSource() {
		m_pipeline = new DefaultSourcePipeline(this);
	}

	@Override
	public String getClassName() {
		return m_className;
	}

	@Override
	public File getFile() {
		return m_file;
	}

	@Override
	public File getFolder() {
		return m_folder;
	}

	public String getLine() {
		return m_line;
	}

	@Override
	public MavenProject getModule() {
		return m_module;
	}

	@Override
	public String getPackage() {
		return m_package;
	}

	@Override
	public SourceScope getParentScope() {
		if (m_scopes.size() > 1) {
			return m_scopes.get(m_scopes.size() - 2);
		} else {
			return null;
		}
	}

	@Override
	public MavenProject getProject() {
		return m_project;
	}

	@Override
	public SourceScope getScope() {
		return m_scopes.peek();
	}

	@Override
	public String getSourceRoot() {
		return m_sourceRoot;
	}

	@Override
	public DefaultSourcePipeline pipeline() {
		return m_pipeline;
	}

	public SourceScope pop() {
		return m_scopes.pop();
	}

	public void push(SourceScope scope) {
		m_scopes.push(scope);
	}

	public void setClassName(String className) {
		m_className = className;
	}

	public void setFile(File file) {
		m_file = file;
	}

	public void setFolder(File folder) {
		m_folder = folder;
	}

	public void setLine(String line) {
		m_line = line;
	}

	public void setModule(MavenProject module) {
		m_module = module;
	}

	public void setPackage(String package1) {
		m_package = package1;
	}

	public void setProject(MavenProject project) {
		m_project = project;
	}

	public void setSourceRoot(String sourceRoot) {
		m_sourceRoot = sourceRoot;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("FileSource:\r\n");
		sb.append("  scopes: ").append(m_scopes).append("\r\n");
		sb.append("  root: ").append(m_project.getName()).append("\r\n");
		sb.append("  current: ").append(m_module.getName()).append("\r\n");

		if (m_sourceRoot != null) {
			sb.append("  sourceRoot: ").append(m_sourceRoot).append("\r\n");
		}

		if (m_folder != null) {
			sb.append("  folder: ").append(m_folder).append("\r\n");
		}

		if (m_file != null) {
			sb.append("  file: ").append(m_file).append("\r\n");
		}

		if (m_package != null) {
			sb.append("  package: ").append(m_package).append("\r\n");
		}

		if (m_className != null) {
			sb.append("  class: ").append(m_className).append("\r\n");
		}

		return sb.toString();
	}

}
