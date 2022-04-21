/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.codegen.model.entity;

import java.util.ArrayList;
import java.util.List;

import org.unidal.codegen.model.BaseEntity;
import org.unidal.codegen.model.IVisitor;

public class CodegenModel extends BaseEntity<CodegenModel> {
   private ManifestModel m_manifest;

   private StructureModel m_structure;

   private OutputsModel m_outputs;

   private List<Any> m_data = new ArrayList<Any>();

   public CodegenModel() {
   }

   @Override
   public void accept(IVisitor visitor) {
      visitor.visitCodegen(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CodegenModel) {
         CodegenModel _o = (CodegenModel) obj;

         if (!equals(getManifest(), _o.getManifest())) {
            return false;
         }

         if (!equals(getStructure(), _o.getStructure())) {
            return false;
         }

         if (!equals(getOutputs(), _o.getOutputs())) {
            return false;
         }

         if (!getData().equals(_o.getData())) {
            return false;
         }

         return true;
      }

      return false;
   }

   public List<Any> getData() {
      return m_data;
   }

   public ManifestModel getManifest() {
      return m_manifest;
   }

   public OutputsModel getOutputs() {
      return m_outputs;
   }

   public StructureModel getStructure() {
      return m_structure;
   }

   @Override
   public int hashCode() {
      int hash = 0;

      hash = hash * 31 + (m_manifest == null ? 0 : m_manifest.hashCode());
      hash = hash * 31 + (m_structure == null ? 0 : m_structure.hashCode());
      hash = hash * 31 + (m_outputs == null ? 0 : m_outputs.hashCode());

      return hash;
   }

   @Override
   public void mergeAttributes(CodegenModel other) {
   }

   public CodegenModel setData(List<Any> data) {
      m_data = data;
      return this;
   }

   public CodegenModel setManifest(ManifestModel manifest) {
      m_manifest = manifest;
      return this;
   }

   public CodegenModel setOutputs(OutputsModel outputs) {
      m_outputs = outputs;
      return this;
   }

   public CodegenModel setStructure(StructureModel structure) {
      m_structure = structure;
      return this;
   }

}
