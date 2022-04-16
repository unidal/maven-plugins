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
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;

public class DalModelRunner extends Suite {
   private File m_baseDir;

   public DalModelRunner(Class<?> klass) throws InitializationError {
      super(klass, Collections.<Runner> emptyList());

      m_baseDir = new File("src/test/dal-model");
   }

   @Override
   protected List<Runner> getChildren() {
      List<Runner> children = new ArrayList<Runner>();
      List<String> scenarios = getScenarios();

      for (String scenario : scenarios) {
         try {
            children.add(new ScenarioRunner(scenario));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      return children;
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

   private File getTestClassesPath(String scenario) {
      return new File(m_baseDir, String.format("%s/target/classes", scenario));
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

   private class CompileSourcesRunner extends Runner {
      private String m_scenario;

      public CompileSourcesRunner(String scenario) {
         m_scenario = scenario;
      }

      @Override
      public Description getDescription() {
         return Description.createTestDescription(m_scenario, "compile-sources");
      }

      @Override
      public void run(RunNotifier notifier) {
         notifier.fireTestStarted(getDescription());
         notifier.fireTestRunFinished(new Result());
         notifier.fireTestFinished(getDescription());
      }
   }

   private class GenerateSourcesRunner extends Runner {
      private String m_scenario;

      public GenerateSourcesRunner(String scenario) {
         m_scenario = scenario;
      }

      @Override
      public Description getDescription() {
         return Description.createTestDescription(m_scenario, "generate-sources");
      }

      @Override
      public void run(RunNotifier notifier) {
         notifier.fireTestStarted(getDescription());

         notifier.fireTestRunFinished(new Result());
         notifier.fireTestFinished(getDescription());
      }
   }

   private class RunTestMethodRunner extends Runner {
      private RunTestRunner m_parent;

      private String m_testName;

      private String m_methodName;

      private Description m_description;

      public RunTestMethodRunner(RunTestRunner parent, String testName, String methodName) {
         m_parent = parent;
         m_testName = testName;
         m_methodName = methodName;
      }

      @Override
      public Description getDescription() {
         if (m_description == null) {
            m_description = Description.createTestDescription(m_testName, m_methodName);
         }

         return m_description;
      }

      @Override
      public void run(RunNotifier notifier) {
         try {
            URLClassLoader classloader = m_parent.getTestClassloader();
            Class<?> testClass = classloader.loadClass(m_testName);
            Method method = testClass.getMethod(m_methodName);
            final FrameworkMethod fm = new FrameworkMethod(method);

            // leverage JUnit existing runner
            new BlockJUnit4ClassRunner(testClass) {
               @Override
               public Description getDescription() {
                  return m_description;
               }

               @Override
               public void run(RunNotifier notifier) {
                  runChild(fm, notifier);
               }
            }.run(notifier);
         } catch (Exception e) {
            notifier.fireTestFailure(new Failure(m_description, e));
         }
      }
   }

   private class RunTestRunner extends ParentRunner<Runner> {
      private String m_scenario;

      private String m_testName;

      private URLClassLoader m_classloader;

      public RunTestRunner(String scenario, String testName) throws InitializationError {
         super(null);

         m_scenario = scenario;
         m_testName = testName;
      }

      @Override
      protected Description describeChild(Runner child) {
         return child.getDescription();
      }

      @Override
      protected List<Runner> getChildren() {
         List<String> methods = getTestMethods(m_scenario, m_testName);
         List<Runner> children = new ArrayList<Runner>();

         for (String method : methods) {
            children.add(new RunTestMethodRunner(this, m_testName, method));
         }

         return children;
      }

      @Override
      protected String getName() {
         return m_testName;
      }

      public URLClassLoader getTestClassloader() throws MalformedURLException {
         if (m_classloader == null) {
            File path = getTestClassesPath(m_scenario);
            URL[] urls = { path.toURI().toURL() };

            m_classloader = new URLClassLoader(urls, getClass().getClassLoader());
         }

         return m_classloader;
      }

      @Override
      protected void runChild(Runner child, RunNotifier notifier) {
         child.run(notifier);
      }
   }

   private class RunTestsRunner extends ParentRunner<Runner> {
      private String m_scenario;

      private List<String> m_testNames;

      public RunTestsRunner(String scenario, List<String> testNames) throws InitializationError {
         super(null);
         m_scenario = scenario;
         m_testNames = testNames;
      }

      @Override
      protected Description describeChild(Runner child) {
         return child.getDescription();
      }

      @Override
      protected List<Runner> getChildren() {
         List<Runner> children = new ArrayList<Runner>();

         try {
            for (String testName : m_testNames) {
               children.add(new RunTestRunner(m_scenario, testName));
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return children;
      }

      @Override
      protected String getName() {
         return "run-tests";
      }

      @Override
      protected void runChild(Runner child, RunNotifier notifier) {
         child.run(notifier);
      }
   }

   private class ScenarioRunner extends ParentRunner<Runner> {
      private String m_scenario;

      public ScenarioRunner(String scenario) throws InitializationError {
         super(null);

         m_scenario = scenario;
      }

      @Override
      protected Description describeChild(Runner child) {
         return child.getDescription();
      }

      @Override
      protected List<Runner> getChildren() {
         List<Runner> children = new ArrayList<Runner>();

         children.add(new GenerateSourcesRunner(m_scenario));
         children.add(new CompileSourcesRunner(m_scenario));

         try {
            List<String> testNames = getTestNames(m_scenario);

            if (!testNames.isEmpty()) {
               children.add(new RunTestsRunner(m_scenario, testNames));
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return children;
      }

      @Override
      protected String getName() {
         return "scenario: " + m_scenario;
      }

      @Override
      protected void runChild(Runner child, RunNotifier notifier) {
         child.run(notifier);
      }
   }
}
