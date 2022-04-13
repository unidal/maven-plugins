package org.unidal.maven.plugin.codegen.scenario;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
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

   protected <T extends AbstractCodegenMojo> MyMojoBuilder<T> builderOf(Class<T> mojoClass, String scenario)
         throws Exception {
      MyMojoBuilder<T> builder = new MyMojoBuilder<T>();

      builder.initialize(getContainer(), mojoClass, scenario);
      return builder;
   }

   protected List<MyMessage> checkAll() throws Exception {
      final List<String> scenarios = new ArrayList<String>();

      Scanners.forDir().scan(new MyHelper("").getBaseDir(), new DirMatcher() {
         @Override
         public Direction matches(File base, String path) {
            File file = new File(base, path);

            if (file.isDirectory() && new File(file, "model").isDirectory()) {
               scenarios.add(path);
            }

            return Direction.NEXT;
         }
      });

      List<MyMessage> messages = new ArrayList<MyMessage>();

      for (String scenario : scenarios) {
         messages.addAll(checkOne(scenario));
      }

      return messages;
   }

   protected List<MyMessage> checkOne(String scenario) throws Exception {
      MyHelper helper = new MyHelper(scenario);

      generate(helper);
      compile(helper);

      return helper.getMessages();
   }

   protected void compile(MyHelper helper) throws Exception {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
      StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);

      manager.setLocation(StandardLocation.SOURCE_PATH, helper.getSourcePath());
      manager.setLocation(StandardLocation.CLASS_OUTPUT, helper.getClassOutput());

      boolean result = compiler.getTask(null, manager, diagnostics, null, null, helper.sourceFiles()).call();

      manager.close();

      if (!result) {
         for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            helper.addMessage(new MyMessage(helper.getScenario(), d));
         }
      }
   }

   protected File generate(MyHelper manager) throws Exception {
      File baseDir = manager.getBaseDir();
      DalModelMojo mojo = builderOf(DalModelMojo.class, manager.getScenario()) //
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

      setField(mojo, "sourceDir", manager.getGeneratedSourcePath().toString());
      setField(mojo, "manifest", manifest.get().getPath());

      mojo.execute();
      return baseDir;
   }

   private static class MyHelper {
      private String m_scenario;

      private File m_baseDir;

      private List<MyMessage> m_messages = new ArrayList<MyMessage>();

      public MyHelper(String scenario) {
         m_scenario = scenario;
         m_baseDir = new File("src/test/dal-model/" + scenario);

         if (!m_baseDir.exists()) {
            throw new IllegalArgumentException(String.format("Scenario(%s) does not found!", scenario));
         }
      }

      public void addMessage(MyMessage message) {
         m_messages.add(message);
      }

      public File getBaseDir() {
         return m_baseDir;
      }

      public List<File> getClassOutput() {
         return pathOf(true, "target/classes");
      }

      public File getGeneratedSourcePath() {
         return new File(m_baseDir, "target/generated-sources");
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
      private String m_scenario;

      private Diagnostic<? extends JavaFileObject> m_diagnostic;

      public MyMessage(String scenario, Diagnostic<? extends JavaFileObject> diagnostic) {
         m_scenario = scenario;
         m_diagnostic = diagnostic;
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder(256);

         sb.append('[').append(m_scenario).append("] ");
         sb.append(m_diagnostic.getKind()).append(' ');
         sb.append(m_diagnostic.getCode()).append(' ');

         if (m_diagnostic.getSource() != null) {
            sb.append(m_diagnostic.getSource().getName()).append(' ');
            sb.append(m_diagnostic.getLineNumber()).append(':').append(m_diagnostic.getColumnNumber());
         }

         return sb.toString();
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

      private void initialize(PlexusContainer container, Class<T> mojoClass, String scenario) throws Exception {
         m_container = container;
         m_mojo = (T) mojoClass.getConstructor().newInstance();
         m_mojo.setLog(new MyLog(scenario));

         MavenProject project = new MavenProject() {
            @Override
            public File getBasedir() {
               return new File(".").getAbsoluteFile();
            }
         };

         setField(m_mojo, "m_project", project);
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
