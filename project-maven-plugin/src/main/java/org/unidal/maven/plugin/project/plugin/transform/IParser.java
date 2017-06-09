package org.unidal.maven.plugin.project.plugin.transform;

import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public interface IParser<T> {
   public PluginMetadata parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForVersioning(IMaker<T> maker, ILinker linker, Versioning parent, T node);

   public void parseForVersions(IMaker<T> maker, ILinker linker, Versions parent, T node);
}
