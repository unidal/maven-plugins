package org.unidal.maven.plugin.codegen.scenario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.unidal.codegen.framework.support.CodegenMojoSupport;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.maven.plugin.codegen.DalJdbcMojo;

/**
 * <pre>
 * <code>
 * &#64;RunWith(DalJdbcRunner.class) 
 * public class DalJdbcTests {
 * 
 * }
 * </code>
 * </pre>
 * 
 * @author qmwu2000
 */
public class DalJdbcRunner extends Suite {
   private File m_baseDir;

   private Map<String, RunnerSupport> m_supports = new HashMap<String, RunnerSupport>();

   public DalJdbcRunner(Class<?> klass) throws InitializationError {
      super(klass, Collections.<Runner> emptyList());

      m_baseDir = new File("src/test/dal-jdbc");
   }

   @Override
   protected List<Runner> getChildren() {
      List<String> scenarios = getScenarios();

      return new RunnerBuilder().buildScenarios(scenarios);
   }

   private List<String> getScenarios() {
      final List<String> scenarios = new ArrayList<String>();

      Scanners.forDir().scan(m_baseDir, new DirMatcher() {
         @Override
         public Direction matches(File base, String path) {
            File file = new File(base, path);

            if (file.isDirectory() && new File(file, "model").isDirectory()) {
               scenarios.add(path);
            }

            return Direction.NEXT;
         }
      });

      Collections.sort(scenarios);

      return scenarios;
   }

   private RunnerSupport getSupport(String scenario) {
      return m_supports.get(scenario);
   }

   private File getTestClassesPath(String scenario) {
      return new File(m_baseDir, String.format("%s/target/classes", scenario));
   }

   private URLClassLoader getTestClassloader(String scenario) throws MalformedURLException {
      File path = getTestClassesPath(scenario);
      URL[] urls = { path.toURI().toURL() };

      return new URLClassLoader(urls, getClass().getClassLoader());
   }

   private List<String> getTestMethods(String scenario, String testName) {
      List<String> methods = new ArrayList<String>();

      try {
         File file = getTestSourcePath(scenario, testName);
         BufferedReader reader = new BufferedReader(new FileReader(file));
         boolean test = false;

         while (true) {
            String line = reader.readLine();

            if (line == null) {
               break;
            }

            line = line.trim();

            if (line.startsWith("//")) {
               continue;
            } else if (line.startsWith("@Test")) {
               test = true;
            } else if (test && line.startsWith("public void ")) {
               int off = "public void ".length();
               int pos = line.indexOf("()");

               if (pos > off) {
                  String method = line.substring(off, pos);

                  methods.add(method);
               }
            }
         }

         reader.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      return methods;
   }

   private List<String> getTestNames(String scenario) {
      File path = getTestSourcesPath(scenario);
      final List<String> testNames = new ArrayList<String>();

      Scanners.forDir().scan(path, new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.endsWith("Test.java")) {
               String testName = path.substring(0, path.length() - ".java".length()).replace('/', '.');

               if (testName.lastIndexOf('$') < 0) {
                  testNames.add(testName);
               }
            }

            return Direction.DOWN;
         }
      });

      return testNames;
   }

   private File getTestSourcePath(String scenario, String testName) {
      return new File(m_baseDir, String.format("%s/test-sources/%s.java", scenario, testName.replace('.', '/')));
   }

   private File getTestSourcesPath(String scenario) {
      return new File(m_baseDir, String.format("%s/test-sources", scenario));
   }

   private static interface Job {
      /**
       * Do the real work.
       * 
       * @return true means successful, false means skipped
       * 
       * @throws Exception
       *            thrown if any error happens
       */
      public boolean run() throws Exception;
   }

   private class JobForCompileSources implements Job {
      private String m_scenario;

      public JobForCompileSources(String scenario) {
         m_scenario = scenario;
      }

      @Override
      public boolean run() throws Exception {
         RunnerSupport support = getSupport(m_scenario);

         support.compileSources();
         support.tearDown();
         return true;
      }
   }

   private class JobForGenerateSources implements Job {
      private String m_scenario;

      public JobForGenerateSources(String scenario) {
         m_scenario = scenario;
      }

      @Override
      public boolean run() throws Exception {
         RunnerSupport support = getSupport(m_scenario);

         support.setUp();
         support.generateSources();
         return true;
      }
   }

   private class JobForRunTestMethod extends JobManaged {
      private String m_scenario;

      private String m_testName;

      private String m_methodName;

      public JobForRunTestMethod(String scenario, String testName, String methodName) {
         m_scenario = scenario;
         m_testName = testName;
         m_methodName = methodName;
      }

      @Override
      public void run(RunNotifier notifier, Description description, boolean ignore) {
         try {
            URLClassLoader classloader = getTestClassloader(m_scenario);
            Class<?> testClass = classloader.loadClass(m_testName);
            Method method = testClass.getMethod(m_methodName);
            final FrameworkMethod fm = new FrameworkMethod(method);

            // leverage JUnit existing runner
            new BlockJUnit4ClassRunner(testClass) {
               @Override
               public Description getDescription() {
                  return Description.createTestDescription(m_testName, m_methodName);
               }

               @Override
               public void run(RunNotifier notifier) {
                  runChild(fm, notifier);
               }
            }.run(notifier);
         } catch (Exception e) {
            notifier.fireTestFailure(new Failure(description, e));
         }
      }
   }

   private static abstract class JobManaged implements Job {
      public boolean run() throws Exception {
         throw new UnsupportedOperationException("Not used here!");
      }

      public abstract void run(RunNotifier notifier, Description description, boolean ignore);
   }

   private static class LeafRunner extends Runner {
      private String m_displayName;

      private String m_category;

      private Job m_job;

      private boolean m_ignore;

      public LeafRunner(String category, String displayName, Job job) {
         this(category, displayName, job, false);
      }

      public LeafRunner(String category, String displayName, Job job, boolean ignore) {
         m_category = category;
         m_displayName = displayName;
         m_job = job;
         m_ignore = ignore;
      }

      @Override
      public Description getDescription() {
         return Description.createTestDescription(m_category, m_displayName);
      }

      @Override
      public void run(RunNotifier notifier) {
         if (m_job instanceof JobManaged) {
            ((JobManaged) m_job).run(notifier, getDescription(), m_ignore);
         } else {
            notifier.fireTestStarted(getDescription());

            try {
               if (m_ignore || m_job != null && !m_job.run()) {
                  notifier.fireTestIgnored(getDescription());
               }

               notifier.fireTestRunFinished(new Result());
            } catch (Throwable e) {
               notifier.fireTestFailure(new Failure(getDescription(), e));
            } finally {
               notifier.fireTestFinished(getDescription());
            }
         }
      }
   }

   private static class NodeRunner extends ParentRunner<Runner> {
      private String m_displayName;

      private List<Runner> m_children;

      public NodeRunner(String displayName, List<Runner> children) throws InitializationError {
         super(new TestClass(null));

         m_displayName = displayName;
         m_children = children;
      }

      @Override
      protected Description describeChild(Runner child) {
         return child.getDescription();
      }

      @Override
      protected List<Runner> getChildren() {
         return m_children;
      }

      @Override
      protected String getName() {
         return m_displayName;
      }

      @Override
      public void run(RunNotifier notifier) {
         final CountDownLatch latch = new CountDownLatch(m_children.size());

         notifier.addListener(new RunListener() {
            @Override
            public void testIgnored(Description description) throws Exception {
               latch.countDown();
            }
         });

         super.run(notifier);

         if (latch.getCount() == 0) { // all children are ignored
            notifier.fireTestIgnored(getDescription());
         }
      }

      @Override
      protected void runChild(Runner child, RunNotifier notifier) {
         child.run(notifier);
      }
   }

   private class RunnerBuilder {
      public List<Runner> buildScenario(String scenario) {
         m_supports.put(scenario, new RunnerSupport(scenario));

         List<Runner> children = new ArrayList<Runner>();
         List<String> testNames = getTestNames(scenario);

         try {
            children.add(new LeafRunner(scenario, "generate-sources", new JobForGenerateSources(scenario)));
            children.add(new LeafRunner(scenario, "compile-sources", new JobForCompileSources(scenario)));

            if (!testNames.isEmpty()) {
               children.add(new NodeRunner("run-tests", buildTests(scenario, testNames)));
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return children;
      }

      public List<Runner> buildScenarios(List<String> scenarios) {
         List<Runner> children = new ArrayList<Runner>();

         for (String scenario : scenarios) {
            try {
               children.add(new NodeRunner("scenario: " + scenario, buildScenario(scenario)));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         return children;
      }

      public List<Runner> buildTest(String scenario, String testName) {
         List<Runner> children = new ArrayList<Runner>();
         List<String> methods = getTestMethods(scenario, testName);

         for (String method : methods) {
            children.add(new LeafRunner(testName, method, new JobForRunTestMethod(scenario, testName, method)));
         }

         return children;
      }

      public List<Runner> buildTests(String scenario, List<String> testNames) {
         List<Runner> children = new ArrayList<Runner>();

         try {
            for (String testName : testNames) {
               children.add(new NodeRunner(testName, buildTest(scenario, testName)));
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return children;
      }
   }

   private static class RunnerSupport extends CodegenMojoSupport {
      private MyHelper m_helper;

      public RunnerSupport(String scenario) {
         m_helper = new MyHelper(DalJdbcMojo.class, "dal-jdbc", scenario);
      }

      public void compileSources() throws Exception {
         compileSources(m_helper);
         copyResources(m_helper);

         if (!m_helper.checkError()) {
            throw m_helper.buildError();
         }
      }

      public void generateSources() throws Exception {
         generateSources(m_helper);

         if (!m_helper.checkError()) {
            throw m_helper.buildError();
         }
      }
   }
}
