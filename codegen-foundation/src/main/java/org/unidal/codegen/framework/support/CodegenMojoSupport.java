package org.unidal.codegen.framework.support;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.unidal.codegen.framework.XslGenerator;
import org.unidal.helper.Files;
import org.unidal.helper.Reflects;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.lookup.PlexusContainer;

public class CodegenMojoSupport extends ComponentTestCase {
   protected static void setField(Object instance, String fieldName, Object value) throws Exception {
      Field field = Reflects.forField().getDeclaredField(instance, fieldName);

      field.setAccessible(true);
      field.set(instance, value);
   }

   protected <T extends Mojo> MyMojoBuilder<T> builderOf(Class<T> mojoClass, MyHelper helper) throws Exception {
      MyMojoBuilder<T> builder = new MyMojoBuilder<T>();

      builder.initialize(getContainer(), mojoClass, helper);
      return builder;
   }

   protected void compileSources(MyHelper helper) throws Exception {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
      StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);

      manager.setLocation(StandardLocation.SOURCE_PATH, helper.getSourcesPath());
      manager.setLocation(StandardLocation.CLASS_OUTPUT, helper.getClassOutput());

      Files.forDir().delete(helper.getClassOutput().get(0), true);

      boolean success = compiler.getTask(null, manager, diagnostics, null, null, helper.sourceFiles()).call();

      manager.close();

      if (!success) {
         for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            helper.addMessage(d);
         }
      }
   }

   protected void copyResources(MyHelper helper) throws IOException {
      File resourcesPath = helper.getTestResourcesPath();

      if (resourcesPath.isDirectory()) {
         File classOutput = helper.getClassOutput().get(0);

         Files.forDir().copyDir(resourcesPath, classOutput);
      }
   }

   protected void generateSources(MyHelper helper) throws Exception {
      File baseDir = helper.getBaseDir();
      Mojo mojo = builderOf(helper.getMojoClass(), helper) //
            .component("m_generator", XslGenerator.class, null).build();
      final AtomicReference<File> manifest = new AtomicReference<File>();

      Scanners.forDir().scan(new File(baseDir, "model"), new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.equals("manifest.xml") || path.endsWith("-manifest.xml")) {
               manifest.set(new File(base, path));
            }

            return Direction.NEXT;
         }
      });

      setField(mojo, "sourceDir", helper.getGeneratedSourcesPath().toString());
      setField(mojo, "manifest", manifest.get().getPath());

      mojo.execute();
   }

   protected static class MyHelper {
      private String m_type;

      private String m_scenario;

      private File m_baseDir;

      private List<MyMessage> m_messages = new ArrayList<MyMessage>();

      private Log m_log;

      private Class<? extends Mojo> m_mojoClass;

      public MyHelper(Class<? extends Mojo> mojoClass, String type, String scenario) {
         m_mojoClass = mojoClass;
         m_type = type;
         m_scenario = scenario;
         m_baseDir = new File(String.format("src/test/%s/%s", type, scenario));
         m_log = new MyLog(scenario);

         if (!m_baseDir.exists()) {
            throw new IllegalArgumentException(
                  String.format("Scenario(%s) of mojo(%s) does not found!", scenario, type));
         }

         Files.forDir().delete(new File(m_baseDir, "target"), true);
      }

      public void addMessage(Diagnostic<? extends JavaFileObject> diagnostic) {
         m_messages.add(new MyMessage(this, diagnostic));
      }

      public Exception buildError() {
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

         return new RuntimeException(sb.substring(0, sb.length() - 2), cause);
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

      public File getGeneratedSourcesPath() {
         return new File(m_baseDir, "target/generated-sources");
      }

      public Log getLog() {
         return m_log;
      }

      public Class<? extends Mojo> getMojoClass() {
         return m_mojoClass;
      }

      public String getScenario() {
         return m_scenario;
      }

      public List<File> getSourcesPath() {
         return pathOf(false, "sources", "target/generated-sources", "test-sources", "../sources");
      }

      public File getTestResourcesPath() {
         return new File(m_baseDir, "test-resources");
      }

      public String getType() {
         return m_type;
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

         for (File path : getSourcesPath()) {
            Scanners.forDir().scan(path, new FileMatcher() {
               @Override
               public Direction matches(File base, String path) {
                  if (path.endsWith(".java")) {
                     try {
                        final String content = Files.forIO().readFrom(new File(base, path), "utf-8");

                        files.add(new SimpleJavaFileObject(new File(base, path).toURI(), Kind.SOURCE) {
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
            });
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

   private static class MyMessage {
      private String m_message;

      private Throwable m_cause;

      private boolean m_error;

      public MyMessage(MyHelper helper, Diagnostic<? extends JavaFileObject> diagnostic) {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(helper.getScenario()).append("] [").append(diagnostic.getKind()).append("] ");

         if (diagnostic.getSource() != null) {
            sb.append(diagnostic.getSource().getName()).append(' ');
            sb.append(diagnostic.getLineNumber()).append(':').append(diagnostic.getColumnNumber()).append(' ');
         }

         sb.append(diagnostic.getMessage(Locale.US));

         m_message = sb.toString();
         m_error = diagnostic.getKind().name().equals("ERROR");
      }

      public Throwable getCause() {
         return m_cause;
      }

      public boolean isSuccess() {
         return !m_error;
      }

      @Override
      public String toString() {
         return m_message;
      }
   }

   public static class MyMojoBuilder<T extends Mojo> {
      private PlexusContainer m_container;

      private T m_mojo;

      private MyMojoBuilder() {
      }

      public T build() throws Exception {
         return m_mojo;
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
}
