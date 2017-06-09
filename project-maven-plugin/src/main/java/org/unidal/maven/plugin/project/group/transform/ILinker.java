package org.unidal.maven.plugin.project.group.transform;

import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public interface ILinker {

   public boolean onPlugin(GroupMetadata parent, Plugin plugin);
}
