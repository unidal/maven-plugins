package org.unidal.maven.plugin.project.group;

import org.unidal.maven.plugin.project.group.entity.GroupMetadata;
import org.unidal.maven.plugin.project.group.entity.Plugin;

public interface IVisitor {

   public void visitMetadata(GroupMetadata metadata);

   public void visitPlugin(Plugin plugin);
}
