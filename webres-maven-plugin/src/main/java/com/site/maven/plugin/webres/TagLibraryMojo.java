package com.site.maven.plugin.webres;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.webres.helper.Splitters;
import org.unidal.webres.tag.build.TldGenerator;
import org.unidal.webres.tag.meta.TagLibMeta;

/**
 * Tag library generation plugin for definition class derived from
 * com.ebay.webres.tag.core.BaseTagLibDefinition.
 * 
 * @goal taglib
 * @phase process-classes
 * @requiresDependencyResolution compile
 * @author Frankie Wu
 */
public class TagLibraryMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Tag library class names list in the format of
    * <code>classname[:alias],...</code>
    * 
    * @parameter expression="${taglibs}"
    * @required
    */
   protected String taglibs;

   /**
    * Output directory for .tld files
    * 
    * @parameter expression="${outputDir}"
    *            default-value="${basedir}/target/tlds"
    * @required
    */
   protected File outputDir;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="true"
    */
   protected boolean verbose;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      List<String> items = Splitters.by(',').noEmptyItem().trim().split(taglibs);
      List<String> args = new ArrayList<String>();
      ClassLoader old = Thread.currentThread().getContextClassLoader();

      try {
         ClassLoader classLoader = makeClassLoader(m_project.getCompileClasspathElements());

         Thread.currentThread().setContextClassLoader(classLoader);

         for (String item : items) {
            int pos = item.indexOf(':');
            String className;
            String alias;

            if (pos > 0) {
               className = item.substring(0, pos);
               alias = item.substring(pos + 1);
            } else {
               className = item;
               alias = null;
            }

            try {
               Class<?> tldClass = classLoader.loadClass(className);
               TagLibMeta meta = tldClass.getAnnotation(TagLibMeta.class);

               if (meta == null) {
                  if (verbose) {
                     getLog().warn(
                           String.format("Class(%s) is not annotated by %s, SKKIPPED", className, TagLibMeta.class));
                  }
               } else {
                  args.add(alias != null ? alias : meta.shortName());
                  args.add(className);
               }
            } catch (ClassNotFoundException e) {
               if (verbose) {
                  getLog().warn(String.format("Unable to find class(%s) in the classpath, SKIPPED", className));
               }
            }
         }

         if (args.size() > 0) {
            args.add(outputDir.getPath());

            outputDir.mkdirs();
            TldGenerator.main(args.toArray(new String[0]));
         } else {
            getLog().info("No .tld file generated!");
         }
      } catch (Exception e) {
         throw new MojoExecutionException(e.getMessage(), e);
      } finally {
         Thread.currentThread().setContextClassLoader(old);
      }
   }
   

   protected ClassLoader makeClassLoader(List<String> classpathElements) {
      List<URL> urls = new ArrayList<URL>(classpathElements.size());

      try {
         for (String element : classpathElements) {
            File file = new File(element);

            urls.add(file.toURI().toURL());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      return new URLClassLoader(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
   }
}
