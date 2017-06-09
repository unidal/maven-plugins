package org.unidal.maven.plugin.project.group.transform;

import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public interface IParser<T> {
   public GroupMetadata parse(IMaker<T> maker, ILinker linker, T node);

   public void parseForPlugin(IMaker<T> maker, ILinker linker, Plugin parent, T node);
}
