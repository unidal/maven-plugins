package org.unidal.maven.plugin.project.plugin.transform;

import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public interface ILinker {

   public boolean onVersioning(PluginMetadata parent, Versioning versioning);

   public boolean onVersions(Versioning parent, Versions versions);
}
