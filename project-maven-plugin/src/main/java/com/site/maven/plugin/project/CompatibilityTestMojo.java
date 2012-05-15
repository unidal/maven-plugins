package com.site.maven.plugin.project;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

import com.site.helper.Files;
import com.site.helper.Scanners;
import com.site.helper.Scanners.FileMatcher;
import com.site.helper.Splitters;
import com.site.maven.plugin.common.AbstractMojoWithDependency;
import com.site.maven.plugin.project.model.entity.ConstructorModel;
import com.site.maven.plugin.project.model.entity.Failure;
import com.site.maven.plugin.project.model.entity.FieldModel;
import com.site.maven.plugin.project.model.entity.MethodModel;
import com.site.maven.plugin.project.model.entity.Report;
import com.site.maven.plugin.project.model.entity.TypeModel;
import com.site.maven.plugin.project.rule.IRuleErrorHandler;
import com.site.maven.plugin.project.rule.IRuleExemptionHandler;
import com.site.maven.plugin.project.rule.RuleConfigurator;
import com.site.maven.plugin.project.rule.RuleEngine;
import com.site.maven.plugin.project.rule.RuleExemption;
import com.site.maven.plugin.project.rule.RuleFailure;

/**
 * Compatibility test to compare current project classes with a previous version
 * project jar.
 * <p>
 * 
 * All classes (.class) from jar file will be checked one by one. A result file
 * will be logged to show whether current project code change is back compatible
 * or not.
 * <p>
 * 
 * Basically, any new files added in current project will not break the
 * compatibility. However following change will break compatibility
 * <ul>
 * <li>public/protected Interface or class name</li>
 * <li>public/protected method signature, including method name, arguments,
 * return type and throw clause</li>
 * <li>public/protected field, including field name, field type</li>
 * </ul>
 * <p>
 * 
 * <b>Notes</b>: All nest classes/interfaces should be checked as well.
 * 
 * @goal ctest
 * @phase process-classes
 * @author Frankie Wu
 */
public class CompatibilityTestMojo extends AbstractMojoWithDependency {
   /**
    * @component
    * @Required
    * @readonly
    */
   protected ArchiverManager m_archiverManager;

   /**
    * Baseline artifact. It should be defined as a dependency inside
    * configuration.
    * 
    * <xmp> <baseline> <groupId>...</groupId> <artifactId>...</artifactId>
    * <version>...</version> </baseline> </xmp>
    * 
    * <b>Notes</b>: version is required, but groupId and artifactId is optional,
    * current project groupId and artifactId will be used if ommited.
    * 
    * @parameter
    * @required
    */
   protected Dependency baseline;

   /**
    * Source artifact. Current project will be used if omitted.
    * 
    * <xmp> <source> <groupId>...</groupId> <artifactId>...</artifactId>
    * <version>...</version> </source> </xmp>
    * 
    * <b>Notes</b>: version is required, but groupId and artifactId is optional,
    * current project groupId and artifactId will be used if omitted.
    * 
    * @parameter
    * @optional
    */
   protected Dependency source;

   /**
    * Output directory for compatibility test
    * 
    * @parameter expression="${outputDir}"
    *            default-value="${basedir}/target/ctest"
    * @required
    */
   protected File outputDir;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="true"
    */
   protected boolean verbose;

   /**
    * Skip this mojo or not
    * 
    * @parameter expression="${ctest.skip}" default-value="false"
    */
   protected boolean skip;

   /**
    * Exemption list
    * 
    * @parameter expression="${exemptions}" default-value=""
    */
   protected String exemptions;

   private List<String> m_exemptionList;

   private RuleEngine m_engine;

   private IRuleErrorHandler m_errorHandler;

   private IRuleExemptionHandler m_exemptionHandler;

   private Report m_report;

   private boolean m_success;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      if (skip) {
         getLog().info("Compatibility test was skipped!");
         return;
      }

      validate();
      initialize();

      ArtifactEntry src = new ArtifactEntry(source);
      ArtifactEntry dst = new ArtifactEntry(baseline);

      if (dst.resolve() && src.resolve()) {
         getLog().info(String.format("Checking back compatibility for %s against %s", src, dst));

         String groupId = source == null ? m_project.getGroupId() : source.getGroupId();
         String artifactId = source == null ? m_project.getArtifactId() : source.getArtifactId();
         String version = source == null ? m_project.getVersion() : source.getVersion();
         File file = new File(outputDir, String.format("%s.%s_%s_%s.xml", groupId, artifactId, version,
               baseline.getVersion()));
         boolean passed = src.checkCompatibility(dst);

         try {
            m_report.setStatus(passed ? "PASSED" : "FAILURE");
            Files.forIO().writeTo(file, m_report.toString());
         } catch (IOException e) {
            throw new MojoExecutionException(String.format("Error when saving result file(%s)!", file), e);
         }

         if (!passed) {
            getLog().error(
                  String.format("Baseline: %s:%s:%s, source: %s:%s:%s", baseline.getGroupId(),
                        baseline.getArtifactId(), baseline.getVersion(), groupId, artifactId, version));
            getLog().error(String.format("Compatibility check failed! Check result at %s.", file));

            throw new MojoExecutionException("Incompatible artifacts found!");
         } else {
            getLog().info(
                  String.format("Baseline: %s:%s:%s, source: %s:%s:%s", baseline.getGroupId(),
                        baseline.getArtifactId(), baseline.getVersion(), groupId, artifactId, version));
            getLog().info(String.format("Compatibility check passed! Check result at %s.", file));
         }
      }
   }

   protected Failure findOrCreateFailure(Report report, String id) {
      Failure failure = report.findFailure(id);

      if (failure == null) {
         failure = new Failure(id);
         report.addFailure(failure);
      }

      return failure;
   }

   protected TypeModel findOrCreateType(Failure failure, String name) {
      TypeModel type = failure.findType(name);

      if (type == null) {
         type = new TypeModel(name);
         failure.addType(type);
      }

      return type;
   }

   protected void initialize() {
      m_exemptionList = Splitters.by(',').noEmptyItem().trim().split(exemptions);
      m_engine = new RuleEngine();
      m_errorHandler = new ErrorHandler();
      m_exemptionHandler = new ExemptionHandler();

      new RuleConfigurator().configure(m_engine.getRegistry());

      m_report = new Report();
      m_report.setGroupId(baseline.getGroupId());
      m_report.setArtifactId(baseline.getArtifactId());
      m_report.setBaselineVersion(baseline.getVersion());
      m_report.setVersion(source == null ? m_project.getVersion() : source.getVersion());
      m_report.setTimestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
   }

   protected void validate() throws MojoExecutionException {
      if (baseline.getGroupId() == null) {
         baseline.setGroupId(m_project.getGroupId());
      }

      if (baseline.getArtifactId() == null) {
         baseline.setArtifactId(m_project.getArtifactId());
      }

      if (baseline.getVersion() == null) {
         throw new MojoExecutionException(
               "<baseline><version>...</version></baseline> should be specified in the configuration");
      }

      baseline.setScope("compile");

      if (source != null) {
         if (source.getGroupId() == null) {
            source.setGroupId(m_project.getGroupId());
         }

         if (source.getArtifactId() == null) {
            source.setArtifactId(m_project.getArtifactId());
         }

         if (source.getVersion() == null) {
            throw new MojoExecutionException(
                  "<source><version>...</version></source> should be specified in the configuration");
         }

         source.setScope("compile");
      }
   }

   class ArtifactEntry {
      private Dependency m_dependency;

      private File m_file;

      private ClassLoader m_classloader;

      public ArtifactEntry(Dependency dependency) {
         m_dependency = dependency;

         initialize();
      }

      public boolean checkCompatibility(final ArtifactEntry target) throws MojoExecutionException {
         final File dstDir = target.getClassDir();
         final File srcDir = getClassDir();
         final ClassMatcher matcher = new ClassMatcher(this, target, srcDir);

         m_success = true;

         Scanners.forDir().scan(dstDir, matcher);
         matcher.complete();

         return m_success;
      }

      protected File getClassDir() throws MojoExecutionException {
         if (m_file.isDirectory()) {
            return m_file;
         } else if (m_file.isFile() && m_file.getPath().endsWith(".jar")) {
            try {
               UnArchiver unarchiver = m_archiverManager.getUnArchiver(m_file);
               String path = "ctest/" + m_dependency.getArtifactId() + "-" + m_dependency.getVersion();
               File file = new File(m_project.getBuild().getDirectory(), path);

               Files.forDir().delete(file);
               file.mkdirs();

               unarchiver.setSourceFile(m_file);
               unarchiver.setDestDirectory(file);
               unarchiver.extract();

               return file;
            } catch (Exception e) {
               e.printStackTrace();
               throw new MojoExecutionException("Error when extracting jar:" + m_file, e);
            }
         } else {
            throw new IllegalStateException(String.format("%s is not a jar or directory!", m_file));
         }
      }

      protected Class<?> loadClass(String className) throws ClassNotFoundException {
         return m_classloader.loadClass(className);
      }

      public boolean resolve() throws MojoExecutionException {
         final List<Artifact> artifacts = new ArrayList<Artifact>();
         final ArtifactHandler handler = new ArtifactHandler() {
            @Override
            public void handle(Artifact artifact) {
               artifacts.add(artifact);
            }
         };

         try {
            if (m_dependency != null) {
               resolveDependency(m_dependency, ScopeArtifactFilter.NO_TEST, handler);

               m_file = artifacts.get(0).getFile();
            } else {
               resolveProjectDependency(m_project, ScopeArtifactFilter.NO_TEST, handler, "compile");

               m_file = m_project.getArtifact().getFile();
            }
         } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
         }

         if (artifacts.isEmpty()) {
            getLog().warn(String.format("Artifact(%s) not found!", m_dependency));
            return false;
         } else {
            m_classloader = makeClassLoader(artifacts);
            return true;
         }
      }

      @Override
      public String toString() {
         StringBuilder sb = new StringBuilder(64);
         Dependency d = m_dependency;

         if (d != null) {
            sb.append(d.getGroupId()).append(':').append(d.getArtifactId()).append(':').append(d.getVersion());
         } else {
            Artifact a = m_project.getArtifact();

            sb.append(a.getGroupId()).append(':').append(a.getArtifactId()).append(':').append(a.getVersion());
         }

         return sb.toString();
      }
   }

   class ClassMatcher extends FileMatcher {
      private final ArtifactEntry m_source;

      private final ArtifactEntry m_target;

      private final File m_srcDir;

      private final StringBuilder m_indicator = new StringBuilder(100);

      private ClassMatcher(ArtifactEntry source, ArtifactEntry target, File srcDir) {
         m_source = source;
         m_target = target;
         m_srcDir = srcDir;
      }

      public void complete() {
         if (m_indicator.length() > 0) {
            getLog().info(m_indicator);
            m_indicator.setLength(0);
         }
      }

      private String getClassName(String path) {
         String str = path.substring(0, path.length() - 6);

         return str.replace('/', '.');
      }

      @Override
      public Direction matches(File base, String path) {
         if (path.endsWith(".class")) {
            if (verbose) {
               m_indicator.append('.');

               if (m_indicator.length() >= 100) {
                  getLog().info(m_indicator);
                  m_indicator.setLength(0);
               }
            }

            int pos = path.lastIndexOf('$');

            if (pos < 0 || path.charAt(pos + 1) < '0' || path.charAt(pos + 1) > '9') { // not
                                                                                       // an
                                                                                       // anonymous
                                                                                       // inner
                                                                                       // class
                                                                                       // like
                                                                                       // A$1.class
               try {
                  String className = getClassName(path);

                  if (!m_exemptionList.contains(className)) {
                     Class<?> dstClass = m_target.loadClass(className);
                     File srcFile = new File(m_srcDir, path);
                     Class<?> srcClass = null;

                     if (srcFile.isFile()) {
                        srcClass = m_source.loadClass(className);
                     }

                     m_engine.execute(srcClass, dstClass, m_errorHandler, m_exemptionHandler);
                  }
               } catch (Throwable e) {
                  if (m_indicator.length() > 0) {
                     getLog().info(m_indicator);
                     m_indicator.setLength(0);
                  }

                  m_success = false;
                  getLog().error(e.toString());
                  e.printStackTrace();
               }
            }
         }

         return Direction.NEXT;
      }
   }

   final class ErrorHandler implements IRuleErrorHandler {
      @Override
      public void onError(AnnotatedElement source, AnnotatedElement target, RuleFailure ruleFailure) {
         String message = ruleFailure.getMessage(source, target);

         if (verbose && message != null) {
            getLog().error(message);
         }

         Class<?> clazz;

         if (target instanceof Member) {
            Member member = (Member) target;

            clazz = member.getDeclaringClass();
         } else {
            clazz = (Class<?>) target;
         }

         Failure failure = findOrCreateFailure(m_report, ruleFailure.name());
         TypeModel type = findOrCreateType(failure, clazz.getName());

         if (target instanceof Method) {
            Method method = (Method) target;
            MethodModel model = new MethodModel();

            type.addMethod(model);
            model.setName(method.getName());
            model.setBaselineSignature(method.toGenericString());

            if (source != null) {
               model.setSignature(((Method) source).toGenericString());
            }
         } else if (target instanceof Field) {
            Field field = (Field) target;
            FieldModel model = new FieldModel();

            type.addField(model);
            model.setName(field.getName());
            model.setBaselineSignature(field.toGenericString());

            if (source != null) {
               model.setSignature(((Method) source).toGenericString());
            }
         } else if (target instanceof Constructor) {
            Constructor<?> constructor = (Constructor<?>) target;
            ConstructorModel model = new ConstructorModel();

            type.addConstructor(model);
            model.setName(constructor.getName());
            model.setBaselineSignature(constructor.toGenericString());

            if (source != null) {
               model.setSignature(((Method) source).toGenericString());
            }
         } else {
            type.setBaselineSignature(toGenericString(clazz));

            if (source != null) {
               type.setSignature(toGenericString((Class<?>) source));
            }
         }

         m_success = false;
      }

      protected String toGenericString(Class<?> clazz) {
         StringBuilder sb = new StringBuilder(256);

         sb.append(Modifier.toString(clazz.getModifiers()));

         if (sb.length() > 0) {
            sb.append(' ');
         }

         if (clazz.isInterface()) {
            sb.append("interface ");
         } else if (clazz.isEnum()) {
            sb.append("enum ");
         } else {
            sb.append("class ");
         }

         sb.append(clazz.getName());

         return sb.toString();
      }
   }

   final class ExemptionHandler implements IRuleExemptionHandler {
      @Override
      public void onExemption(AnnotatedElement source, AnnotatedElement target, RuleExemption exemption) {
         String message = exemption.getMessage(source, target);

         if (verbose && message != null) {
            getLog().info(message);
         }
      }
   }
}
