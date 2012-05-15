package com.site.maven.plugin.codegen;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.site.maven.plugin.codegen.plexus.ProfileTest;

@RunWith(Suite.class)
@SuiteClasses({

MojoTest.class,

ProfileTest.class

})
public class AllTests {

}
