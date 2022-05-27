package org.unidal.maven.plugin.source.pipeline;

import java.io.File;

import org.apache.maven.project.MavenProject;

public interface Source {
	String getClassName();

	File getFile();

	File getFolder();

	String getLine();

	MavenProject getModule();

	String getPackage();

	SourceScope getParentScope();

	MavenProject getProject();

	SourceScope getScope();

	String getSourceRoot();
	
	SourcePipeline pipeline();
}
