package org.unidal.maven.plugin.source;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;
import org.unidal.maven.plugin.source.pipeline.FileSource;
import org.unidal.maven.plugin.source.pipeline.Source;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerAdaptor;
import org.unidal.maven.plugin.source.pipeline.SourceHandlerContext;
import org.unidal.maven.plugin.source.pipeline.SourcePipeline;
import org.unidal.maven.plugins.source.model.entity.ClassModel;
import org.unidal.maven.plugins.source.model.entity.CountModel;
import org.unidal.maven.plugins.source.model.entity.ProjectModel;
import org.unidal.maven.plugins.source.model.entity.RootModel;
import org.unidal.maven.plugins.source.model.transform.BaseVisitor;

/**
 * Counts the source lines of the current Java project.
 * 
 * @aggregator true
 * @goal lines
 */
public class LinesMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      Set<MavenProject> projects = new HashSet<MavenProject>();
      RootModel root = new RootModel();

      projects.add(m_project);
      projects.addAll(m_project.getCollectedProjects());

      for (MavenProject project : projects) {
         processProject(project, root);
      }

      root.accept(new CountAggregator());
      root.accept(new CountPrinter());
   }

   private void processClass(File file, CountModel count) throws IOException {
      Source source = new FileSource(file);
      SourcePipeline pipeline = source.pipeline();

      pipeline.addLast(new LineHandler(count));

      source.scanFile();
   }

   private void processProject(MavenProject project, RootModel root) {
      List<String> main = project.getCompileSourceRoots();
      List<String> test = project.getTestCompileSourceRoots();
      ProjectModel model = new ProjectModel().setName(project.getArtifactId());

      root.addProject(model);

      for (String sourceRoot : main) {
         processSourceRoot(sourceRoot, model, true, false, sourceRoot.contains("/target/"));
      }

      for (String sourceRoot : test) {
         processSourceRoot(sourceRoot, model, false, true, sourceRoot.contains("/target/"));
      }
   }

   private void processSourceRoot(String sourceRoot, final ProjectModel project, final boolean main, final boolean test,
         final boolean codegen) {
      Scanners.forDir().scan(new File(sourceRoot), new FileMatcher() {
         @Override
         public Direction matches(File base, String path) {
            if (path.endsWith(".java")) {
               int pos1 = path.lastIndexOf('/');
               int pos2 = path.lastIndexOf('.');

               String packageName = path.substring(0, pos1).replace('/', '.');
               String className = path.substring(pos1 + 1, pos2);
               CountModel count = new CountModel();
               ClassModel model = new ClassModel().setPackage(packageName).setClazz(className);

               project.addClass(model);
               model.setCount(count);
               model.setMain(codegen ? false : main).setCodegen(codegen).setTest(test);

               try {
                  processClass(new File(base, path), count);
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }

            return Direction.DOWN;
         }
      });
   }

   private static class CountAggregator extends BaseVisitor {
      private CountModel m_root;

      private CountModel m_project;

      @Override
      public void visitCount(CountModel count) {
         m_root.add(count);
         m_project.add(count);
      }

      @Override
      public void visitProject(ProjectModel project) {
         m_project = new CountModel();

         super.visitProject(project);
         project.setCount(m_project);
      }

      @Override
      public void visitRoot(RootModel root) {
         m_root = new CountModel();

         super.visitRoot(root);
         root.setCount(m_root);
      }
   }

   private static class CountPrinter extends BaseVisitor {
      @Override
      public void visitProject(ProjectModel project) {
         System.out.println(project.getName() + ": " + project.getCount());
      }

      @Override
      public void visitRoot(RootModel root) {
         System.out.println("ALL: " + root.getCount());
         super.visitRoot(root);
      }
   }

   private static class LineHandler extends SourceHandlerAdaptor {
      private CountModel m_count;

      public LineHandler(CountModel count) {
         m_count = count;
      }

      @Override
      public void handleLine(SourceHandlerContext ctx, String line) {
         line = line.trim();

         m_count.incLines();

         if (line.isEmpty()) {
            m_count.incEmpty();
         } else if (line.startsWith("//")) {
            m_count.incComment();
         }
      }
   }
}
