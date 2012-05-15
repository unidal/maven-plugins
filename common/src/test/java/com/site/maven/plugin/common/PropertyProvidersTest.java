package com.site.maven.plugin.common;

import org.junit.Ignore;
import org.junit.Test;

import com.site.maven.plugin.common.PropertyProviders;

public class PropertyProvidersTest {
	@Test
	@Ignore
	public void testConsole() {
		String packageName = PropertyProviders.fromConsole().forString("package", "Package Name:", null, null);
		boolean webres = PropertyProviders.fromConsole().forBoolean("webres", "Support WebRes?", true);

		System.out.println(packageName + ":" + webres);
	}
}
