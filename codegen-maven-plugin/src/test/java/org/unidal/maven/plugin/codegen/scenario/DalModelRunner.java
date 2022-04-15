package org.unidal.maven.plugin.codegen.scenario;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite;
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

      return scenarios;
   }

   private File getTestClassesPath(String scenario) {
      return new File(m_baseDir, scenario + "/target/classes");
   }

   private class ScenarioRunner extends ParentRunner<Runner> {
      private String m_scenario;

      private URLClassLoader m_classloader;

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
         try {
            new DalModelMojoSupport().run(m_scenario);
         } catch (Exception e1) {
            e1.printStackTrace();
         }

         List<Runner> children = new ArrayList<Runner>();
         File path = getTestClassesPath(m_scenario);
         final List<String> classNames = new ArrayList<String>();

         Scanners.forDir().scan(path, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (path.endsWith("Test.class")) {
                  String className = path.substring(0, path.length() - 6).replace('/', '.');

                  if (className.lastIndexOf('$') < 0) {
                     classNames.add(className);
                  }
               }

               return Direction.DOWN;
            }
         });

         try {
            URL[] urls = { path.toURI().toURL() };
            m_classloader = new URLClassLoader(urls, getClass().getClassLoader());

            for (String className : classNames) {
               Class<?> klass = m_classloader.loadClass(className);

               if (!klass.isInterface() && !klass.isEnum() && !klass.isAnnotation() && !klass.isSynthetic()) {
                  children.add(new BlockJUnit4ClassRunner(klass));
               }
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
