package bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BootstrapTest {
	@Test
	public void testUnzipWar() throws IOException {
		Bootstrap b = new Bootstrap();
		File warRoot = new File("target/work");
		File warFile = new File("cat.war");

		b.unzipWar(warRoot, warFile);
	}

	@Test
	public void testSetup() throws IOException {
		Bootstrap b = new Bootstrap();
		List<String> entries = b.setup(new File("target/work"), "cat.war");

		System.out.println("Jars: " + (entries.size() - 1));
	}

	@Test
	public void testHackClassLoader() throws IOException {
		Bootstrap b = new Bootstrap();

		b.hackClassLoader(Arrays.asList("cat.war"));

		System.out.println(getClass().getResource("/WEB-INF/web.xml"));

		b.startup(new File("target/work"), null);
	}
}
