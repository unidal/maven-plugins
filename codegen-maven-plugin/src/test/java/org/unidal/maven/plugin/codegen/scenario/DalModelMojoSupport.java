package org.unidal.maven.plugin.codegen.scenario;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.unidal.codegen.generator.Generator;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.DirMatcher;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.PlexusContainer;
import org.unidal.maven.plugin.codegen.AbstractCodegenMojo;
import org.unidal.maven.plugin.codegen.DalModelMojo;

public abstract class DalModelMojoSupport extends ComponentTestCase {
   protected static void setField(Object instance, String fieldName, Object value) throws Exception {
      Field field = Reflects.forField().getDeclaredField(instance, fieldName);

      field.setAccessible(true);
      field.set(instance, value);
   }

   protected <T extends AbstractCodegenMojo> MyMojoBuilder<T> builderOf(Class<T> mojoClass, MyHelper helper)
         throws Exception {
      MyMojoBuilder<T> builder = new MyMojoBuilder<T>();

      builder.initialize(getContainer(), mojoClass, helper);
      return builder;
   }

   protected void checkAll() throws Exception {
      final List<String> scenarios = new ArrayList<String>();
      MyHelper helper = new MyHelper("");

      Scanners.forDir().scan(helper.getBaseDir(), new DirMatcher() {
         @Override
         public Direction matches(File base, String path) {
            File file = new File(base, path);

            if (file.isDirectory() && new File(file, "model").isDirectory()) {
               scenarios.add(path);
            }

            return Direction.NEXT;
         }
      });

      for (String scenario : scenarios) {
         MyHelper h = new MyHelper(scenario);

         generateCode(h);
         compileSource(h);

         helper.getMessages().addAll(h.getMessages());
      }

      if (!helper.checkError()) {
         throw helper.buildError();
      }
   }

   protected void checkOne(String scenario) throws Exception {
      MyHelper helper = new MyHelper(scenario);

      generateCode(helper);
      compileSource(helper);
      runTests(helper);

      if (!helper.checkError()) {
         throw helper.buildError();
      }
   }

   private void compileSource(MyHelper helper) throws Exception {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
      StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);

      manager.setLocation(StandardLocation.SOURCE_PATH, helper.getSourcePath());
      manager.setLocation(StandardLocation.CLASS_OUTPUT, helper.getClassOutput());

      boolean success = compiler.getTask(null, manager, diagnostics, null, null, helper.sourceFiles()).call();

      manager.close();

      if (!success) {
         for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            helper.addMessage(d);
         }
      }
   }

   private File generateCode(MyHelper helper) throws Exception {
      File baseDir = helper.getBaseDir();
      DalModelMojo mojo = builderOf(DalModelMojo.class, helper) //
            .component("m_generator", Generator.class, "dal-model").build();
      final AtomicReference<File> manifest = new AtomicReference<File>();

      Scanners.forDir().scan(new File(baseDir, "model"), new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.endsWith("-manifest.xml")) {
               manifest.set(new File(base, path));
            }

            return Direction.NEXT;
         }
      });

      setField(mojo, "sourceDir", helper.getGeneratedSourcePath().toString());
      setField(mojo, "manifest", manifest.get().getPath());

      mojo.execute();
      return baseDir;
   }

   private void runTests(MyHelper helper) throws Exception {
      MyTestRunner runner = new MyTestRunner(helper);

      runner.prepare();
      runner.runTests();
   }

   private static class MyHelper {
      private String m_scenario;

      private File m_baseDir;

      private List<MyMessage> m_messages = new ArrayList<MyMessage>();

      private Log m_log;

      public MyHelper(String scenario) {
         m_scenario = scenario;
         m_baseDir = new File("src/test/dal-model/" + scenario);
         m_log = new MyLog(scenario);

         if (!m_baseDir.exists()) {
            throw new IllegalArgumentException(String.format("Scenario(%s) does not found!", scenario));
         }
      }

      public void addMessage(Diagnostic<? extends JavaFileObject> diagnostic) {
         m_messages.add(new MyMessage(this, diagnostic));
      }

      public void addMessage(Error error) {
         m_messages.add(new MyMessage(this, error));
      }

      public void addMessage(Exception exception) {
         m_messages.add(new MyMessage(this, exception));
      }

      public void addMessage(String message, Exception exception) {
         m_messages.add(new MyMessage(this, message, exception));
      }

      public Error buildError() {
         StringBuilder sb = new StringBuilder(256);
         int count = 0;
         Throwable cause = null;

         for (MyMessage message : m_messages) {
            if (!message.isSuccess()) {
               sb.append(message).append("\r\n");
               count++;

               if (cause == null && message.getCause() != null) {
                  cause = message.getCause();
               }
            }
         }

         sb.insert(0, String.format("Generated code failed to compile, %s errors found:\r\n", count));

         AssertionError error = new AssertionError(sb.substring(0, sb.length() - 2), cause);

         return error;
      }

      public boolean checkError() {
         boolean found = false;

         for (MyMessage message : m_messages) {
            // always show the message on the console
            System.out.println(message);

            if (!message.isSuccess()) {
               found = true;
            }
         }

         return !found;
      }

      public File getBaseDir() {
         return m_baseDir;
      }

      public List<File> getClassOutput() {
         return pathOf(true, "target/classes");
      }

      public File getCompiledClassesPath() {
         return new File(m_baseDir, "target/classes");
      }

      public File getGeneratedSourcePath() {
         return new File(m_baseDir, "target/generated-sources");
      }

      public Log getLog() {
         return m_log;
      }

      public List<MyMessage> getMessages() {
         return m_messages;
      }

      public String getScenario() {
         return m_scenario;
      }

      public List<File> getSourcePath() {
         return pathOf(false, "sources", "target/generated-sources", "test-sources");
      }

      private List<File> pathOf(boolean createIfNotExists, String... files) {
         List<File> lists = new ArrayList<File>();

         for (String file : files) {
            File path = new File(m_baseDir, file);

            if (path.exists()) {
               lists.add(path);
            } else if (createIfNotExists) {
               if (path.mkdirs()) {
                  lists.add(path);
               } else {
                  System.out.println(String.format("Unable to create directory(%s).", path));
               }
            }
         }

         return lists;
      }

      public List<JavaFileObject> sourceFiles() {
         final List<JavaFileObject> files = new ArrayList<JavaFileObject>();

         for (File path : getSourcePath()) {
            Scanners.forDir().scan(path, new SourceFileMatcher(files));
         }

         return files;
      }
   }

   private static class MyLog extends SystemStreamLog {
      private String m_scenario;

      public MyLog(String scenario) {
         m_scenario = scenario;
      }

      @Override
      public void info(CharSequence content) {
         System.out.println("[" + m_scenario + "] [INFO] " + content.toString());
      }
   }

   protected static class MyMessage {
      private String m_message;

      private Throwable m_cause;

      private boolean m_error;

      private boolean m_failed;

      public MyMessage(MyHelper helper, Diagnostic<? extends JavaFileObject> diagnostic) {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(helper.getScenario()).append("] [").append(diagnostic.getKind()).append("] ");

         if (diagnostic.getSource() != null) {
            sb.append(diagnostic.getSource().getName()).append(' ');
            sb.append(diagnostic.getLineNumber()).append(':').append(diagnostic.getColumnNumber()).append(' ');
         }

         sb.append(diagnostic.getCode());

         m_message = sb.toString();
         m_error = diagnostic.getKind().name().equals("ERROR");
      }

      public MyMessage(MyHelper helper, Error error) {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(helper.getScenario()).append("] [FAILED] ");
         append(sb, error);

         m_message = sb.toString();
         m_cause = error;
         m_failed = true;
      }

      public MyMessage(MyHelper helper, Exception exception) {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(helper.getScenario()).append("] [ERROR] ");
         append(sb, exception);

         m_message = sb.toString();
         m_cause = exception;
         m_error = true;
      }

      public MyMessage(MyHelper helper, String message, Exception exception) {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(helper.getScenario()).append("] [FAILED] ");
         sb.append(message);
         append(sb, exception);

         m_message = sb.toString();
         m_cause = exception;
         m_failed = true;
      }

      private void append(StringBuilder sb, Throwable t) {
         StringWriter sw = new StringWriter(2048);

         t.printStackTrace(new PrintWriter(sw));
         sb.append(sw.toString());
      }

      public Throwable getCause() {
         return m_cause;
      }

      public boolean isSuccess() {
         return !m_error && !m_failed;
      }

      @Override
      public String toString() {
         return m_message;
      }
   }

   protected static class MyMojoBuilder<T extends AbstractCodegenMojo> {
      private PlexusContainer m_container;

      private T m_mojo;

      private MyMojoBuilder() {
      }

      public T build() throws Exception {
         return m_mojo;
      }

      public MyMojoBuilder<T> component(String fieldName, Class<?> role) throws Exception {
         return component(fieldName, role, null);
      }

      public MyMojoBuilder<T> component(String fieldName, Class<?> role, String roleHint) throws Exception {
         Object component = m_container.lookup(role, roleHint);

         setField(m_mojo, fieldName, component);

         if (component instanceof LogEnabled) {
            ((LogEnabled) component).enableLogging(new ConsoleLogger());
         }

         if (component instanceof Initializable) {
            ((Initializable) component).initialize();
         }

         return this;
      }

      private void initialize(PlexusContainer container, Class<T> mojoClass, MyHelper helper) throws Exception {
         m_container = container;
         m_mojo = (T) mojoClass.getConstructor().newInstance();
         m_mojo.setLog(helper.getLog());

         MavenProject project = new MavenProject() {
            @Override
            public File getBasedir() {
               return new File(".").getAbsoluteFile();
            }
         };

         setField(m_mojo, "m_project", project);
      }
   }

   private static class MyTestClass {
      private Class<?> m_klass;

      private List<Method> m_beforeClassMethods = new ArrayList<Method>();

      private List<Method> m_afterClassMethods = new ArrayList<Method>();

      private List<Method> m_beforeMethods = new ArrayList<Method>();

      private List<Method> m_afterMethods = new ArrayList<Method>();

      private List<Method> m_testMethods = new ArrayList<Method>();

      public MyTestClass(Class<?> klass) {
         m_klass = klass;

         initialize();
      }

      private void handleException(MyHelper helper, Method method, Throwable t) {
         if (t instanceof Exception) {
            Exception e = (Exception) t;
            Test test = method.getAnnotation(Test.class);

            if (test != null) {
               if (test.expected() != Test.None.class) {
                  String message = String.format("Unexpected exception, expected<%s> but was<%s>", test.expected().getName(),
                        e.getClass().getName());

                  helper.addMessage(message, e);
               } else {
                  String message = String.format("Exception %s thrown. ", e.getClass().getName());

                  helper.addMessage(message, e);
               }
            } else if (Modifier.isStatic(method.getModifiers())) {
               helper.addMessage(e);
            } else {
               helper.addMessage(null, e);
            }
         } else if (t instanceof Error) {
            helper.addMessage((Error) t);
         }
      }

      private void initialize() {
         Method[] methods = m_klass.getMethods();

         for (Method method : methods) {
            if (method.getDeclaringClass() == Object.class) {
               continue;
            } else if (method.getParameterCount() != 0 || method.getReturnType() != Void.TYPE) {
               continue;
            }

            // no arguments and no return
            if (Modifier.isStatic(method.getModifiers())) { // static method
               BeforeClass bc = method.getAnnotation(BeforeClass.class);
               AfterClass ac = method.getAnnotation(AfterClass.class);

               if (bc != null) {
                  m_beforeClassMethods.add(method);
               } else if (ac != null) {
                  m_afterClassMethods.add(method);
               }
            } else { // member method
               Before bc = method.getAnnotation(Before.class);
               After ac = method.getAnnotation(After.class);
               Test test = method.getAnnotation(Test.class);
               Ignore ignore = method.getAnnotation(Ignore.class);

               if (bc != null) {
                  m_beforeMethods.add(method);
               } else if (ac != null) {
                  m_afterMethods.add(method);
               } else if (test != null && ignore == null) {
                  m_testMethods.add(method);
               }
            }
         }
      }

      public boolean isEligible() {
         Ignore ignore = m_klass.getAnnotation(Ignore.class);

         return ignore == null && !m_testMethods.isEmpty();
      }

      public void run(MyHelper helper) throws Exception {
         for (Method beforeClass : m_beforeClassMethods) {
            try {
               beforeClass.invoke(null);
            } catch (Exception e) {
               handleException(helper, beforeClass, e.getCause());
               return;
            }
         }

         for (Method method : m_testMethods) {
            // make new instance every time
            Object instance = m_klass.getDeclaredConstructor().newInstance();

            for (Method before : m_beforeMethods) {
               try {
                  before.invoke(instance);
               } catch (Exception e) {
                  handleException(helper, before, e.getCause());
                  return;
               }
            }

            try {
               method.invoke(instance);
            } catch (Error e) {
               helper.addMessage(e);
            } catch (Exception e) {
               handleException(helper, method, e.getCause());
            }

            for (Method after : m_afterMethods) {
               try {
                  after.invoke(instance);
               } catch (Exception e) {
                  handleException(helper, after, e.getCause());
                  return;
               }
            }
         }

         for (Method afterClass : m_afterClassMethods) {
            try {
               afterClass.invoke(null);
            } catch (Exception e) {
               // ignore it
            }
         }
      }
   }

   private static class MyTestRunner {
      private MyHelper m_helper;

      private URLClassLoader m_classloader;

      private List<MyTestClass> m_testClasses = new ArrayList<MyTestClass>();

      public MyTestRunner(MyHelper helper) {
         m_helper = helper;
      }

      public void prepare() throws Exception {
         File path = m_helper.getCompiledClassesPath();
         final List<String> classNames = new ArrayList<String>();

         m_classloader = new URLClassLoader(new URL[] { path.toURI().toURL() }, getClass().getClassLoader());

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

         for (String className : classNames) {
            MyTestClass testClass = tryLoadTestClass(className);

            if (testClass != null && testClass.isEligible()) {
               m_testClasses.add(testClass);
            }
         }
      }

      public void runTests() throws Exception {
         for (MyTestClass testClass : m_testClasses) {
            testClass.run(m_helper);
         }
      }

      private MyTestClass tryLoadTestClass(String className) throws Exception {
         try {
            Class<?> klass = m_classloader.loadClass(className);

            if (!klass.isInterface() && !klass.isEnum() && !klass.isAnnotation() && !klass.isSynthetic()) {
               return new MyTestClass(klass);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         return null;
      }
   }

   private static class SourceFileMatcher extends FileMatcher {
      private final List<JavaFileObject> m_files;

      private SourceFileMatcher(List<JavaFileObject> files) {
         m_files = files;
      }

      @Override
      public Direction matches(File base, String path) {
         if (path.endsWith(".java")) {
            try {
               final String content = Files.forIO().readFrom(new File(base, path), "utf-8");

               m_files.add(new SimpleJavaFileObject(new File(path).toURI(), Kind.SOURCE) {
                  @Override
                  public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
                     return content;
                  }
               });
            } catch (Exception e) {
               e.printStackTrace();
            }
         }

         return Direction.DOWN;
      }
   }
}
