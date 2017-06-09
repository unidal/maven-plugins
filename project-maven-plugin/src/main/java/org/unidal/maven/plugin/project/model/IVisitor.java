package org.unidal.maven.plugin.project.model;

import org.unidal.maven.plugin.project.model.entity.ConstructorModel;
import org.unidal.maven.plugin.project.model.entity.Failure;
import org.unidal.maven.plugin.project.model.entity.FieldModel;
import org.unidal.maven.plugin.project.model.entity.MethodModel;
import org.unidal.maven.plugin.project.model.entity.Report;
import org.unidal.maven.plugin.project.model.entity.TypeModel;

public interface IVisitor {

   public void visitConstructor(ConstructorModel constructor);

   public void visitFailure(Failure failure);

   public void visitField(FieldModel field);

   public void visitMethod(MethodModel method);

   public void visitReport(Report report);

   public void visitType(TypeModel type);
}
