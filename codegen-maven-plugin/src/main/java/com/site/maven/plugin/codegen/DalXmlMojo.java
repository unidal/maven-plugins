package com.site.maven.plugin.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.site.codegen.generator.AbstractGenerateContext;
import com.site.codegen.generator.GenerateContext;
import com.site.codegen.generator.Generator;
import com.site.codegen.manifest.ManifestCreator;
import com.site.codegen.meta.XmlMetaHelper;

/**
 * DAL code generator for XML
 * 
 * @goal dal-xml
 * @author Frankie Wu
 */
public class DalXmlMojo extends AbstractMojo {
   /**
    * ManifestCreator implementation
    * 
    * @component role="com.site.codegen.manifest.ManifestCreator"
    * @required
    * @readonly
    */
   protected ManifestCreator m_manifestCreator;

   /**
    * XmlMeta implementation
    * 
    * @component role="com.site.codegen.meta.XmlMetaHelper"
    * @required
    * @readonly
    */
   protected XmlMetaHelper m_xmlMetaHelper;

   /**
    * Code generator implementation
    * 
    * @component role="com.site.codegen.generator.Generator" role-hint="dal-xml"
    * @required
    * @readonly
    */
   protected Generator m_generator;

   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   protected MavenProject m_project;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${source.dir}"
    *            default-value="${basedir}/target/generated-sources/dal-xml"
    * @required
    */
   protected String sourceDir;

   /**
    * Location of generated source directory
    * 
    * @parameter expression="${resource.base}" default-value="/META-INF/dal/xml"
    * @required
    */
   protected String resouceBase;

   /**
    * Location of manifest.xml file
    * 
    * @parameter expression="${manifest}"
    */
   protected String manifest;

   /**
    * Location of source file (file or URL)
    * 
    * @parameter expression="${source.input}"
    */
   protected String sourceInput;

   /**
    * Package for generated source
    * 
    * @parameter expression="${source.package}"
    */
   protected String sourcePackage;

   /**
    * Encoding of source file
    * 
    * @parameter expression="${source.encoding}"
    */
   protected String sourceEncoding = "utf-8";

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

   private String configurationTips(String[] variables, String[] properties) {
      StringBuilder sb = new StringBuilder(256);

      sb.append("<configuration>\r\n");

      for (String variable : variables) {
         sb.append("   <").append(variable).append(">VALUE<").append(variable).append(">\r\n");
      }

      sb.append("</configuration>\r\n");
      sb.append("\r\n-OR-\r\n\r\n");
      sb.append("on the command line, specify: '");

      boolean first = true;
      for (String property : properties) {
         if (first) {
            first = false;
         } else {
            sb.append(' ');
         }

         sb.append("-D").append(property).append("=VALUE");
      }

      sb.append("'\r\n");

      return sb.toString();
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      if (skip) {
         getLog().info("Model codegen was skipped explicitly.");
         return;
      }

      try {
         File manifestFile = new File(getManifest());

         if (!manifestFile.exists()) {
            throw new MojoFailureException(String.format("Manifest(%s) not found!", manifestFile.getCanonicalPath()));
         }

         final URL manifestXml = manifestFile.toURI().toURL();
         final GenerateContext ctx = new AbstractGenerateContext(m_project.getBasedir(), resouceBase, sourceDir) {
            public URL getManifestXml() {
               return manifestXml;
            }

            public void log(LogLevel logLevel, String message) {
               switch (logLevel) {
               case DEBUG:
                  if (debug) {
                     getLog().debug(message);
                  }
                  break;
               case INFO:
                  if (debug || verbose) {
                     getLog().info(message);
                  }
                  break;
               case ERROR:
                  getLog().error(message);
                  break;
               }
            }
         };

         m_generator.generate(ctx);
         m_project.addCompileSourceRoot(sourceDir);
         getLog().info(ctx.getGeneratedFiles() + " files generated.");
      } catch (Exception e) {
         throw new MojoFailureException("Code generating failed.", e);
      }
   }

   private String getManifest() throws MojoExecutionException {
      if (sourceInput != null && sourcePackage != null) {
         String userContent = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root do-package=\"" + sourcePackage + "\"/>";

         try {
            Reader reader = getSourceReader();
            String xmlMetaContent = m_xmlMetaHelper.getXmlMetaContent(reader);

            return m_manifestCreator.create(xmlMetaContent, userContent);
         } catch (Exception e) {
            throw new MojoExecutionException("Can't create manifest file by " + sourceInput + " and " + sourcePackage,
                  e);
         }
      } else if (manifest != null) {
         return manifest;
      }

      throw new MojoExecutionException("One of followings must be specified:\r\n"
            + configurationTips(new String[] { "manifest" }, new String[] { "manifest" })
            + "\r\nor\r\n\r\n"
            + configurationTips(new String[] { "sourceInput", "sourcePackage" }, new String[] { "source.input",
                  "source.package" }));
   }

   private Reader getSourceReader() throws UnsupportedEncodingException, FileNotFoundException, IOException,
         MalformedURLException {
      Reader reader;

      if (new File(sourceInput).exists()) {
         reader = new InputStreamReader(new FileInputStream(sourceInput), sourceEncoding);
      } else if (new File(m_project.getBasedir(), sourceInput).exists()) {
         reader = new InputStreamReader(new FileInputStream(new File(m_project.getBasedir(), sourceInput)),
               sourceEncoding);
      } else {
         URLConnection conn = new URL(sourceInput).openConnection();

         reader = new InputStreamReader(conn.getInputStream(), sourceEncoding);
      }

      return reader;
   }
}
