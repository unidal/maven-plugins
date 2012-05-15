package com.site.codegen.generator;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.site.codegen.generator.cat.consumer.ProblemReportTest;
import com.site.codegen.generator.cat.consumer.TransactionReportTest;
import com.site.codegen.generator.pom.PomTest;

@RunWith(Suite.class)
@SuiteClasses({

/* .cat.consumer */
ProblemReportTest.class,

TransactionReportTest.class,

/* .pom */
PomTest.class

})
public class AllIntgTests {

}
