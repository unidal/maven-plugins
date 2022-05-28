package org.unidal.maven.plugin.source;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.maven.plugin.source.pipeline.DefaultSource;
import org.unidal.maven.plugin.source.pipeline.DefaultSourcePipeline;
import org.unidal.maven.plugin.source.pipeline.PipelineDriver;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;
import org.unidal.maven.plugin.source.upgrade.handler.ClassFileSink;
import org.unidal.maven.plugin.source.upgrade.handler.ComponentTestCaseClassHandler;
import org.unidal.maven.plugin.source.upgrade.handler.ContainerHolderClassHandler;
import org.unidal.maven.plugin.source.upgrade.handler.InitializableInterfaceHandler;
import org.unidal.maven.plugin.source.upgrade.handler.InjectAnnotationHandler;
import org.unidal.maven.plugin.source.upgrade.handler.NamedAnnotationHandler;

/**
 * Upgrade the project to make good use of Spring Boot/Cloud capabilities.
 * 
 * @aggregator true
 * @goal upgrade
 */
public class UpgradeMojo extends AbstractMojo {
	/**
	 * Current project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject m_project;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		DefaultSource source = new DefaultSource();
		DefaultSourcePipeline pipeline = source.pipeline();
		SourceHandlerContext ctx = pipeline.headContext();

		pipeline.addLast(new InjectAnnotationHandler());
		pipeline.addLast(new NamedAnnotationHandler());
		pipeline.addLast(new InitializableInterfaceHandler());
		pipeline.addLast(new ContainerHolderClassHandler());
		pipeline.addLast(new ComponentTestCaseClassHandler());
		
		pipeline.addLast(new ClassFileSink());

		try {
			new PipelineDriver().handleProject(source, ctx, m_project);
		} catch (IOException e) {
			throw new MojoExecutionException("Error when executing mojo! " + e, e);
		}
	}
}
