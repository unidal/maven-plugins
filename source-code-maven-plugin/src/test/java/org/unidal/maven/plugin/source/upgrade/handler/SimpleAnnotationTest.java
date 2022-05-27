package org.unidal.maven.plugin.source.upgrade.handler;

import org.junit.Assert;
import org.junit.Test;

public class SimpleAnnotationTest {
	@Test
	public void testInject() {
		Assert.assertEquals("@Inject{}", new SimpleAnnotation("@Inject").toString());
		Assert.assertEquals("@Inject{optional=true}", new SimpleAnnotation("@Inject(optional = true)").toString());
		Assert.assertEquals("@Inject{value=\"default\"}", new SimpleAnnotation("@Inject(\"default\")").toString());
	}

	@Test
	public void testNamed() {
		Assert.assertEquals("@Named{type=MyClasss.class}",
		      new SimpleAnnotation("@Named(type = MyClasss.class)").toString());
		Assert.assertEquals("@Named{type=MyClasss.class, value=MyClass.ID}",
		      new SimpleAnnotation("@Named(type = MyClasss.class, value=MyClass.ID)").toString());
		Assert.assertEquals("@Named{type=MyClasss.class, value=\"local\"}",
		      new SimpleAnnotation("@Named(type = MyClasss.class, value=\"local\")").toString());
		Assert.assertEquals("@Named{type=MyClasss.class, instantiateStrategy=\"per-lookup\"}",
		      new SimpleAnnotation("@Named(type = MyClasss.class, instantiateStrategy = \"per-lookup\")").toString());
		Assert.assertEquals("@Named{type=MyClasss.class, instantiateStrategy=Named.PER_LOOKUP}",
		      new SimpleAnnotation("@Named(type = MyClasss.class, instantiateStrategy = Named.PER_LOOKUP)").toString());
	}
}
