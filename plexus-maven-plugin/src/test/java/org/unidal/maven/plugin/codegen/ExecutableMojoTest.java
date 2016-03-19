package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class ExecutableMojoTest {
	@Test
	public void copyBootstrapFiles() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();

		mojo.copyBootstrapClasses(new File("target/work"));
	}

	@Test
	public void detectTestServerClass() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();
		String testClass = mojo.detectTestClass(new File("target/test-classes"), "/ExecutableMojoTest.class");

		System.out.println(testClass);
	}

	@Test
	public void extractWar() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();

		mojo.extractWar(new File("target/work.jar"), new File("cat.war"));
	}

	@Test
	public void packageIntoJar() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();

		mojo.packageIntoJar(new File("target/work.jar"), new File("cat.jar"));
	}

	@Test
	public void reviseManifest() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();

		mojo.reviseManifest(new File("target/work"));
	}

	@Test
	public void copyClassFile() throws IOException {
		ExecutableMojo mojo = new ExecutableMojo();

		mojo.copyClassFile(new File("target/work"), "target/test-classes", "bootstrap.BootstrapTest");
	}
}
