/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.codegen.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.model.BaseEntity;
import org.unidal.codegen.model.IVisitor;

public class OutputsModel extends BaseEntity<OutputsModel> {
   private List<OutputModel> m_outputs = new ArrayList<OutputModel>();

   public OutputsModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitOutputs(this);
   }

   public OutputsModel addOutput(OutputModel output) {
      m_outputs.add(output);
      return this;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof OutputsModel) {
         OutputsModel _o = (OutputsModel) obj;

         if (!equals(getOutputs(), _o.getOutputs())) {
            return false;
         }


         return true;
      }

      return false;
   }

   public OutputModel findOutput(String path) {
      for (OutputModel output : m_outputs) {
         if (!equals(output.getPath(), path)) {
            continue;
         }

         return output;
      }

      return null;
   }

   public List<OutputModel> getOutputs() {
      return m_outputs;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      for (OutputModel e : m_outputs) {
         hash = hash * 31 + (e == null ? 0 :e.hashCode());
      }


      return hash;
   }

   @Override
   public void mergeAttributes(OutputsModel other) {
   }

   public OutputModel removeOutput(String path) {
      int len = m_outputs.size();

      for (int i = 0; i < len; i++) {
         OutputModel output = m_outputs.get(i);

         if (!equals(output.getPath(), path)) {
            continue;
         }

         return m_outputs.remove(i);
      }

      return null;
   }

}
