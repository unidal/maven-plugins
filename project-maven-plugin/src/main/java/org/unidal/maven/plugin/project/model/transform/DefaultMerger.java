package org.unidal.maven.plugin.project.model.transform;

import java.util.Stack;

import org.unidal.maven.plugin.project.model.IEntity;
import org.unidal.maven.plugin.project.model.IVisitor;
import org.unidal.maven.plugin.project.model.entity.ConstructorModel;
import org.unidal.maven.plugin.project.model.entity.Failure;
import org.unidal.maven.plugin.project.model.entity.FieldModel;
import org.unidal.maven.plugin.project.model.entity.MethodModel;
import org.unidal.maven.plugin.project.model.entity.Report;
import org.unidal.maven.plugin.project.model.entity.TypeModel;

public class DefaultMerger implements IVisitor {

   private Stack<Object> m_objs = new Stack<Object>();

   private Report m_report;

   public DefaultMerger() {
   }

   public DefaultMerger(Report report) {
      m_report = report;
      m_objs.push(report);
   }

   public Report getReport() {
      return m_report;
   }

   protected Stack<Object> getObjects() {
      return m_objs;
   }

   public <T> void merge(IEntity<T> to, IEntity<T> from) {
      m_objs.push(to);
      from.accept(this);
      m_objs.pop();
   }

   protected void mergeConstructor(ConstructorModel to, ConstructorModel from) {
      to.mergeAttributes(from);
   }

   protected void mergeFailure(Failure to, Failure from) {
      to.mergeAttributes(from);
   }

   protected void mergeField(FieldModel to, FieldModel from) {
      to.mergeAttributes(from);
   }

   protected void mergeMethod(MethodModel to, MethodModel from) {
      to.mergeAttributes(from);
   }

   protected void mergeReport(Report to, Report from) {
      to.mergeAttributes(from);
   }

   protected void mergeType(TypeModel to, TypeModel from) {
      to.mergeAttributes(from);
   }

   @Override
   public void visitConstructor(ConstructorModel from) {
      ConstructorModel to = (ConstructorModel) m_objs.peek();

      mergeConstructor(to, from);
      visitConstructorChildren(to, from);
   }

   protected void visitConstructorChildren(ConstructorModel to, ConstructorModel from) {
   }

   @Override
   public void visitFailure(Failure from) {
      Failure to = (Failure) m_objs.peek();

      mergeFailure(to, from);
      visitFailureChildren(to, from);
   }

   protected void visitFailureChildren(Failure to, Failure from) {
      for (TypeModel source : from.getTypes()) {
         TypeModel target = to.findType(source.getName());

         if (target == null) {
            target = new TypeModel(source.getName());
            to.addType(target);
         }

         m_objs.push(target);
         source.accept(this);
         m_objs.pop();
      }
   }

   @Override
   public void visitField(FieldModel from) {
      FieldModel to = (FieldModel) m_objs.peek();

      mergeField(to, from);
      visitFieldChildren(to, from);
   }

   protected void visitFieldChildren(FieldModel to, FieldModel from) {
   }

   @Override
   public void visitMethod(MethodModel from) {
      MethodModel to = (MethodModel) m_objs.peek();

      mergeMethod(to, from);
      visitMethodChildren(to, from);
   }

   protected void visitMethodChildren(MethodModel to, MethodModel from) {
   }

   @Override
   public void visitReport(Report from) {
      Report to = (Report) m_objs.peek();

      mergeReport(to, from);
      visitReportChildren(to, from);
   }

   protected void visitReportChildren(Report to, Report from) {
      for (Failure source : from.getFailures()) {
         Failure target = to.findFailure(source.getType());

         if (target == null) {
            target = new Failure(source.getType());
            to.addFailure(target);
         }

         m_objs.push(target);
         source.accept(this);
         m_objs.pop();
      }
   }

   @Override
   public void visitType(TypeModel from) {
      TypeModel to = (TypeModel) m_objs.peek();

      mergeType(to, from);
      visitTypeChildren(to, from);
   }

   protected void visitTypeChildren(TypeModel to, TypeModel from) {
      for (FieldModel source : from.getFields()) {
         FieldModel target = null;

         if (target == null) {
            target = new FieldModel();
            to.addField(target);
         }

         m_objs.push(target);
         source.accept(this);
         m_objs.pop();
      }

      for (ConstructorModel source : from.getConstructors()) {
         ConstructorModel target = null;

         if (target == null) {
            target = new ConstructorModel();
            to.addConstructor(target);
         }

         m_objs.push(target);
         source.accept(this);
         m_objs.pop();
      }

      for (MethodModel source : from.getMethods()) {
         MethodModel target = null;

         if (target == null) {
            target = new MethodModel();
            to.addMethod(target);
         }

         m_objs.push(target);
         source.accept(this);
         m_objs.pop();
      }
   }
}
