package org.unidal.maven.plugin.project.plugin;

import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public interface IVisitor {

   public void visitMetadata(PluginMetadata metadata);

   public void visitVersioning(Versioning versioning);

   public void visitVersions(Versions versions);
}
