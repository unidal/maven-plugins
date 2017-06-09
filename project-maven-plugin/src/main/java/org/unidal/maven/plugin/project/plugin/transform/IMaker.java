package org.unidal.maven.plugin.project.plugin.transform;

import org.unidal.maven.plugin.project.plugin.entity.PluginMetadata;
import org.unidal.maven.plugin.project.plugin.entity.Versioning;
import org.unidal.maven.plugin.project.plugin.entity.Versions;

public interface IMaker<T> {

   public PluginMetadata buildMetadata(T node);

   public String buildVersion(T node);

   public Versioning buildVersioning(T node);

   public Versions buildVersions(T node);
}
