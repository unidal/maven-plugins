package org.unidal.maven.plugin.source;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.maven.plugin.source.lines.handler.LineCounter;
import org.unidal.maven.plugin.source.pipeline.DefaultSource;
import org.unidal.maven.plugin.source.pipeline.DefaultSourcePipeline;
import org.unidal.maven.plugin.source.pipeline.PipelineDriver;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;

/**
 * Counts the source lines for the current Java project.
 * 
 * @aggregator true
 * @goal lines
 */
public class LinesMojo extends AbstractMojo {
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

		pipeline.addLast(new LineCounter());

		try {
			new PipelineDriver().handleProject(source, ctx, m_project);
		} catch (IOException e) {
			throw new MojoExecutionException("Error when executing mojo! " + e, e);
		}
	}
}
