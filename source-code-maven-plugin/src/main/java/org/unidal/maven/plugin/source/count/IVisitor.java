/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.maven.plugin.source.count;

import org.unidal.maven.plugin.source.count.entity.ClassModel;
import org.unidal.maven.plugin.source.count.entity.CountModel;
import org.unidal.maven.plugin.source.count.entity.ProjectModel;
import org.unidal.maven.plugin.source.count.entity.RootModel;

public interface IVisitor {

   public void visitClass(ClassModel _class);

   public void visitCount(CountModel count);

   public void visitProject(ProjectModel project);

   public void visitRoot(RootModel root);
}
