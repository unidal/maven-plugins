package org.unidal.maven.plugin.project.group.transform;

import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public interface IMaker<T> {

   public GroupMetadata buildMetadata(T node);

   public Plugin buildPlugin(T node);
}
