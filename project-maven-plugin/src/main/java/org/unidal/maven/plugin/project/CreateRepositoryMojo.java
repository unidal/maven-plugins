package org.unidal.maven.plugin.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.DefaultDependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionException;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectDependenciesResolver;
import org.sonatype.aether.RepositorySystemSession;
import org.unidal.helper.Files;
import org.unidal.helper.Splitters;
import org.unidal.maven.plugin.common.SharedContext;
import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;
import org.xml.sax.SAXException;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * Create a Maven repository to hold some jars (build & plugin) referenced by
 * current project.
 * 
 * @goal create-repository
 */
public class CreateRepositoryMojo extends AbstractMojo {
   /**
    * Current project
    * 
    * @parameter expression="${project}"
    * @required
    * @readonly
    */
   private MavenProject m_project;

   /**
    * The current repository/network configuration of Maven.
    * 
    * @parameter default-value="${repositorySystemSession}"
    * @readonly
    */
   private RepositorySystemSession m_repositorySession;

   /**
    * @parameter expression="${localRepository}"
    * @readonly
    * @required
    */
   private ArtifactRepository m_localRepository;

   /**
    * @component
    * @required
    * @readonly
    */
   private ProjectDependenciesResolver m_dependencyResolver;

   /**
    * @component
    * @required
    * @readonly
    */
   private ProjectBuilder m_projectBuilder;

   /**
    * @component
    * @required
    * @readonly
    */
   private SharedContext m_sharedContext;

   /**
    * @parameter expression="${targetDir}"
    *            default-value="${user.dir}/target/repository"
    */
   private File m_targetDir;

   /**
    * @parameter expression="${include}"
    *            default-value="org.unidal,com.site,com.dianping"
    */
   private String include;

   /**
    * Verbose information or not
    * 
    * @parameter expression="${verbose}" default-value="false"
    */
   private boolean verbose;

   private void collectDependencies(Set<Artifact> all, List<String> pluginGroups, List<String> prefixes, MavenProject project,
         boolean withPlugin, int level) throws ProjectBuildingException {
      DependencyResolutionResult resolutionResult;

      try {
         DefaultDependencyResolutionRequest resolution = new DefaultDependencyResolutionRequest(project, m_repositorySession);

         resolutionResult = m_dependencyResolver.resolve(resolution);
      } catch (DependencyResolutionException e) {
         resolutionResult = e.getResult();
      }

      List<org.sonatype.aether.graph.Dependency> dependencies = resolutionResult.getResolvedDependencies();

      if (!dependencies.isEmpty()) {
         for (org.sonatype.aether.graph.Dependency d : dependencies) {
            org.sonatype.aether.artifact.Artifact artifact = d.getArtifact();
            Artifact a = RepositoryUtils.toArtifact(artifact);

            resolveDependency(all, pluginGroups, prefixes, a, level + 1);
         }
      }

      if (withPlugin) {
         for (Plugin plugin : m_project.getBuildPlugins()) {
            Artifact pluginArtifact = new DefaultArtifact(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(),
                  "compile", "jar", null, new DefaultArtifactHandler("jar"));

            resolveDependency(all, pluginGroups, prefixes, pluginArtifact, 0);

            for (Dependency dependency : plugin.getDependencies()) {
               Artifact dependencyArtifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
                     dependency.getVersion(), "compile", dependency.getType(), null, new DefaultArtifactHandler("jar"));

               resolveDependency(all, pluginGroups, prefixes, dependencyArtifact, 1);
            }
         }
      }
   }

   private void logLine(String id, int level) {
      StringBuilder sb = new StringBuilder(128);

      for (int i = 0; i < level - 1; i++) {
         sb.append("|  ");
      }

      if (level > 0) {
         sb.append("+- ");
      }

      sb.append(id);

      getLog().info(sb.toString());
   }

   public void execute() throws MojoExecutionException, MojoFailureException {
      List<String> prefixes = Splitters.by(',').trim().noEmptyItem().split(include);
      Set<Artifact> all = new HashSet<Artifact>();
      List<String> pluginGroups = new ArrayList<String>();
      List<String> files = new ArrayList<String>();

      m_sharedContext.setAttribute(getClass(), m_project.getArtifact().getId(), m_project);

      try {
         if (verbose) {
            logLine(m_project.getId(), 0);
         }

         collectDependencies(all, pluginGroups, prefixes, m_project, true, 0);

         for (Artifact artifact : all) {
            files.add(m_localRepository.pathOf(artifact));

            resolveParentPom(files, artifact);
         }

         if (files.size() > 0) {
            saveArtifacts(files, m_targetDir);
            rebuildPluginMeta(pluginGroups, m_targetDir);
         }
      } catch (Exception e) {
         throw new MojoFailureException("Error when creating repository! " + e, e);
      }
   }

   private void rebuildPluginMeta(List<String> pluginGroups, File targetDir) throws IOException, SAXException, IOException {
      Set<File> groupFiles = new LinkedHashSet<File>();
      Set<File> pluginFiles = new LinkedHashSet<File>();

      for (String pluginGroup : pluginGroups) {
         List<String> parts = Splitters.by(':').split(pluginGroup);
         int index = 0;
         String groupId = parts.get(index++);
         String artifactId = parts.get(index++);
         String version = parts.get(index++);
         String name = parts.get(index++);
         String prefix = artifactId.substring(0, artifactId.indexOf('-'));

         // update group maven-meta.xml
         File groupFile = new File(targetDir, groupId.replace('.', '/') + "/maven-meta.xml");
         GroupMetadata groupMeta;

         if (groupFile.isFile()) {
            groupMeta = org.unidal.maven.plugin.project.group.transform.DefaultSaxParser.parse(new FileInputStream(groupFile));
         } else {
            groupMeta = new GroupMetadata();
         }

         org.unidal.maven.plugin.project.group.entity.Plugin plugin = groupMeta.findPlugin(artifactId);

         if (plugin == null) {
            plugin = new org.unidal.maven.plugin.project.group.entity.Plugin(artifactId);
            plugin.setPrefix(prefix);
            plugin.setName(name);
            groupMeta.addPlugin(plugin);

            Files.forIO().writeTo(groupFile, groupMeta.toString());
            groupFiles.add(groupFile);
         }

         // update plugin maven-meta.xml
         File pluginFile = new File(targetDir, groupId.replace('.', '/') + "/" + artifactId + "/maven-meta.xml");
         PluginMetadata pluginMeta;

         if (pluginFile.isFile()) {
            pluginMeta = org.unidal.maven.plugin.project.plugin.transform.DefaultSaxParser.parse(new FileInputStream(pluginFile));
         } else {
            pluginMeta = new PluginMetadata();
            pluginMeta.setGroupId(groupId);
            pluginMeta.setArtifactId(artifactId);
            pluginMeta.setVersioning(new Versioning());
         }

         Versioning versioning = pluginMeta.getVersioning();

         versioning.setLastUpdated(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
         versioning.setLatest(version);

         if (!version.endsWith("-SNAPSHOT")) {
            versioning.setRelease(version);
         }

         Versions versions = versioning.getVersions();

         if (versions == null) {
            versions = new Versions();
            versioning.setVersions(versions);
         }

         if (!versions.getVersions().contains(version)) {
            versions.addVersion(version);

            Files.forIO().writeTo(pluginFile, pluginMeta.toString());
            pluginFiles.add(pluginFile);
         }
      }

      for (File groupFile : groupFiles) {
         getLog().info(String.format("Generate file %s", groupFile));
      }

      for (File pluginFile : pluginFiles) {
         getLog().info(String.format("Generate file %s", pluginFile));
      }
   }

   private void resolveDependency(Set<Artifact> all, List<String> pluginGroups, List<String> prefixes, Artifact artifact, int level)
         throws ProjectBuildingException {
      boolean eligible = false;
      Map<String, Object> buildingArtifacts = m_sharedContext.getAttributes(getClass());

      if (buildingArtifacts.containsKey(artifact.getId())) {
         return;
      }

      for (String prefix : prefixes) {
         if (artifact.getGroupId().startsWith(prefix)) {
            eligible = true;
            break;
         }
      }

      if (!eligible || all.contains(artifact)) {
         return;
      }

      all.add(artifact);

      if (verbose) {
         logLine(artifact.getId(), level);
      }

      ProjectBuildingRequest request = m_project.getProjectBuildingRequest();
      String path = m_localRepository.pathOf(artifact);
      File pomFile = new File(m_localRepository.getBasedir(), path.substring(0, path.length() - 4) + ".pom");
      MavenProject project = m_projectBuilder.build(pomFile, request).getProject();

      if ("maven-plugin".equals(project.getPackaging())) {
         String id = project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion() + ":" + project.getName();

         if (!pluginGroups.contains(id)) {
            pluginGroups.add(id);
         }
      }

      collectDependencies(all, pluginGroups, prefixes, project, false, level);
   }

   private void resolveParentPom(List<String> result, Artifact artifact) {
      ProjectBuildingRequest request = m_project.getProjectBuildingRequest();
      String repositoryBase = m_localRepository.getBasedir();
      String pomPath = null;

      try {
         if (artifact.getType() == "jar") {
            String jarPath = m_localRepository.pathOf(artifact);

            pomPath = jarPath.substring(0, jarPath.length() - 4) + ".pom";
         } else {
            pomPath = m_localRepository.pathOf(artifact);
         }

         if (result.contains(pomPath)) {
            return;
         }

         result.add(pomPath);

         File pomFile = new File(repositoryBase, pomPath);
         MavenProject project = m_projectBuilder.build(pomFile, request).getProject();
         Artifact parentArtifact = project.getParentArtifact();

         if (parentArtifact != null) {
            resolveParentPom(result, parentArtifact);
         }
      } catch (ProjectBuildingException e) {
         throw new IllegalStateException("Failed to build parent project for " + artifact.getId(), e);
      }
   }

   private void saveArtifacts(List<String> files, File targetBase) throws IOException {
      File sourceBase = new File(m_localRepository.getBasedir());

      for (String file : files) {
         File source = new File(sourceBase, file);
         File target = new File(targetBase, file);

         if (!target.isFile()) {
            target.getParentFile().mkdirs();

            Files.forDir().copyFile(source, target);
            getLog().info(String.format("Create file %s", target));

            File sourceSign = new File(sourceBase, file + ".sha1");
            File targetSign = new File(targetBase, file + ".sha1");

            if (sourceSign.isFile()) {
               Files.forDir().copyFile(sourceSign, targetSign);
               getLog().info(String.format("Create file %s", targetSign));
            }
         }
      }
   }
}
