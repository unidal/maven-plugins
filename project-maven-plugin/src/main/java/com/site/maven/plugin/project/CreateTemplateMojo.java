package com.site.maven.plugin.project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Resource;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Create a template project from current project.
 * 
 * @goal create-template
 */
public class CreateTemplateMojo extends AbstractMojo {
   private static final String TEMPLATE_DIR = "target/template";

   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject project;

   /**
    * Maven Project Helper
    * 
    * @component role="org.apache.maven.project.MavenProjectHelper"
    * @required
    * @readonly
    */
   protected MavenProjectHelper projectHelper;

   private void copyFile(File source, File target, Filter filter) throws IOException {
      String encoding = "utf-8";
      String content = FileUtils.fileRead(source, encoding);
      String filtered = filter.doFilter(content);

      target.getParentFile().mkdirs();

      FileUtils.fileWrite(target.getPath(), encoding, filtered);
   }

   private void copyFiles(File source, File target, Filter filter) throws IOException {
      if (source.isDirectory()) {
         String[] list = source.list();

         if (list != null) {
            for (String item : list) {
               copyFiles(new File(source, item), new File(target, item), filter);
            }
         }
      } else if (source.isFile()) {
         copyFile(source, target, filter);
      }
   }

   private void copyPom(Filter filter) throws IOException, XmlPullParserException {
      File baseDir = project.getBasedir();
      File pomFile = new File(baseDir, "pom.xml");
      File targetPomFile = new File(baseDir, TEMPLATE_DIR + "/pom.xml");
      Model model = new MavenXpp3Reader().read(new FileReader(pomFile));

      model.setGroupId("{group}");
      model.setArtifactId("{artifactId}");
      model.setVersion("{version}");
      model.setName(null);
      model.setDescription(null);

      new MavenXpp3Writer().write(new FileWriter(targetPomFile), model);
   }

   private File createZipArchive() throws ArchiverException, IOException {
      Archiver a = new ZipArchiver();
      File baseDir = project.getBasedir();
      File template = new File(baseDir, TEMPLATE_DIR);
      File jarFile = new File(baseDir, "target/" + project.getBuild().getFinalName() + "-template.jar");

      a.addDirectory(template);
      a.setDestFile(jarFile);
      a.createArchive();

      return jarFile;
   }

   private String detectJavaPackage() {
      String srcDir = project.getBuild().getSourceDirectory();
      StringBuilder sb = new StringBuilder(64);
      File base = new File(srcDir);

      while (true) {
         String[] list = base.list();

         // with only one directory
         if (list != null && list.length == 1) {
            String name = list[0];

            base = new File(base, name);

            if (base.isDirectory()) {
               sb.append('.').append(name);
               continue;
            }
         }

         break;
      }

      if (sb.length() == 0) {
         throw new RuntimeException(String.format("No package was detected starting from %s!", srcDir));
      } else {
         return sb.substring(1);
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      Build build = project.getBuild();
      String packageName = getFromConsole("package", detectJavaPackage());
      Filter filter = new ContentFilter(packageName);

      try {
         copyPom(filter);
         processFiles(packageName, filter, build.getSourceDirectory(), true);
         processFiles(packageName, filter, build.getTestSourceDirectory(), true);
         processFiles(packageName, filter, build.getScriptSourceDirectory(), false);

         List<Resource> resources = build.getResources();

         for (Resource resource : resources) {
            processFiles(packageName, filter, resource.getDirectory(), false);
         }

         if ("war".equals(project.getPackaging())) {
            processFiles(packageName, filter, new File(project.getBasedir(), "src/main/webapp").getPath(), false);
         }

         List<Resource> testResources = build.getTestResources();

         for (Resource testResource : testResources) {
            processFiles(packageName, filter, testResource.getDirectory(), false);
         }

         File jarFile = createZipArchive();

         if (projectHelper != null) {
            projectHelper.attachArtifact(project, project.getPackaging(), "template", jarFile);
         }
      } catch (Exception e) {
         throw new MojoExecutionException("Error when processing files!", e);
      }
   }

   private String getFromConsole(String name, String defaultValue) {
      boolean done = false;
      String value = null;

      while (!done) {
         if (defaultValue == null) {
            value = readLine("Please provide %s: ", name);

            if (value != null && value.length() > 0) {
               done = true;
            }
         } else {
            value = readLine("Please provide %s[%s]: ", name, defaultValue);

            if (value != null) {
               if (value.length() == 0) {
                  value = defaultValue;
               }

               done = true;
            }
         }
      }

      return value;
   }

   private String getRelativePath(File from, File base) {
      StringBuilder sb = new StringBuilder(64);

      while (from != null && !from.equals(base)) {
         sb.insert(0, from.getName());
         sb.insert(0, '/');

         from = from.getParentFile();
      }

      return sb.toString();
   }

   private void processFiles(String packageName, Filter filter, String sourcePath, boolean isJava) throws IOException {
      if (sourcePath == null || !new File(sourcePath).exists()) {
         return;
      }

      File source = new File(sourcePath);
      File baseDir = project.getBasedir();
      String relativePath = getRelativePath(source, baseDir);
      File to = new File(baseDir, TEMPLATE_DIR + relativePath);

      if (isJava) {
         File from = new File(source, packageName.replaceAll("\\.", "/"));

         copyFiles(from, to, filter);
      } else {
         copyFiles(source, to, filter);
      }
   }

   private String readLine(String format, Object... args) {
      if (System.console() != null) {
         return System.console().readLine(format, args);
      } else { // for debugg purpose
         return "";
      }
   }

   public static class ContentFilter implements Filter {
      private String m_packageName;

      public ContentFilter(String packageName) {
         m_packageName = packageName;
      }

      public String doFilter(String content) {
         return content.replaceAll(Pattern.quote(m_packageName), "{package}");
      }
   }

   public static interface Filter {
      public String doFilter(String content);
   }
}
