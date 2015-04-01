package org.unidal.maven.plugin.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.lifecycle.internal.LifecycleDependencyResolver;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.helper.Files;
import org.unidal.helper.Files.AutoClose;
import org.unidal.helper.Files.IO;
import org.unidal.helper.Scanners;
import org.unidal.helper.Scanners.FileMatcher;

/**
 * Prepares web module resources for war packaging.
 * 
 * @goal web-module
 * @phase prepare-package
 * @requiresDependencyResolution runtime
 * @author Frankie Wu
 */
public class WebModuleMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * @component
    * @required
    * @readonly
    */
   private LifecycleDependencyResolver m_resolver;

   /**
    * @component
    * @required
    * @readonly
    */
   private LegacySupport m_legacySupport;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   protected boolean verbose;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${debug}" default-value="false"
    */
   protected boolean debug;

   /**
    * Skip this codegen or not
    * 
    * @parameter expression="${codegen.skip}" default-value="false"
    */
   protected boolean skip;

   private void copyDirWithoutOverwrite(File source, final File base, final AtomicInteger count) throws IOException {
      if (source.isDirectory()) {
         final List<String> paths = new ArrayList<String>();

         Scanners.forDir().scan(source, new FileMatcher() {
            @Override
            public Direction matches(File base, String path) {
               if (path.startsWith("/WEB-MODULE/")) {
                  paths.add(path);
               }

               return Direction.DOWN;
            }
         });

         for (String path : paths) {
            File from = new File(source, path);
            File to = new File(base, path);

            if (!to.exists()) {
               if (verbose) {
                  getLog().info("Copy " + to);
               }

               count.incrementAndGet();
               Files.forDir().copyFile(from, to);
            }
         }
      } else if (source.getPath().endsWith(".jar")) {
         ZipInputStream zis = new ZipInputStream(new FileInputStream(source));

         while (true) {
            ZipEntry entry = zis.getNextEntry();

            if (entry == null) {
               break;
            }

            String path = entry.getName();

            if (path.startsWith("WEB-MODULE/") && !path.endsWith("/")) {
               File to = new File(base, path.substring("WEB-MODULE/".length()));

               if (!to.exists()) {
                  if (verbose) {
                     getLog().info("Copy " + to);
                  }

                  to.getParentFile().mkdirs();
                  IO.INSTANCE.copy(zis, new FileOutputStream(to), AutoClose.OUTPUT);
                  count.incrementAndGet();
               }
            }
         }

         zis.close();
      }
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (skip) {
         getLog().info("The plugin was skipped explicitly.");
         return;
      }

      if (debug) {
         verbose = true;
      }

      try {
         m_resolver.resolveProjectDependencies(m_project, Arrays.asList("compile", "runtime"),
               Arrays.asList("compile", "runtime"), m_legacySupport.getSession(), false, new HashSet<Artifact>());

         Build build = m_project.getBuild();
         File base = new File(build.getDirectory(), build.getFinalName());

         if (verbose) {
            getLog().info("Target dir: " + base.getCanonicalFile());
         }

         final AtomicInteger count = new AtomicInteger();

         for (Artifact artifact : m_project.getArtifacts()) {
            copyDirWithoutOverwrite(artifact.getFile(), base, count);
         }

         getLog().info(count.get() + " files copied.");
      } catch (Exception e) {
         throw new MojoFailureException("Error when resolving project dependencies!2", e);
      }
   }
}
