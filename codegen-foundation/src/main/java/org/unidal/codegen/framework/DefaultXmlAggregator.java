package org.unidal.codegen.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

import org.unidal.codegen.model.CodegenModelHelper;
import org.unidal.codegen.model.entity.Any;
import org.unidal.codegen.model.entity.FileModel;
import org.unidal.codegen.model.entity.StructureModel;
import org.unidal.codegen.model.entity.ManifestModel;
import org.unidal.codegen.model.entity.NodeModel;
import org.unidal.codegen.model.transform.BaseVisitor;
import org.unidal.helper.Splitters;
import org.unidal.lookup.annotation.Named;

@Named(type = XmlAggregator.class)
public class DefaultXmlAggregator implements XmlAggregator {
   @Override
   public Any aggregate(File manifestXml, StructureModel structure, ManifestModel manifest) throws IOException {
      manifest.accept(new ManifestNormalizer(manifestXml));

      ModelMerger merger = new ModelMerger(structure);

      for (FileModel file : manifest.getFiles()) {
         Any data = CodegenModelHelper.fromXml(Any.class, new FileInputStream(file.getPath()));

         data.accept(merger);
      }

      return merger.getResult();
   }

   private static class ManifestNormalizer extends BaseVisitor {
      private File m_manifestFile;

      public ManifestNormalizer(File manifestFile) {
         m_manifestFile = manifestFile;
      }

      @Override
      public void visitFile(FileModel file) {
         String path = file.getPath();

         file.setPath(new File(m_manifestFile.getParentFile(), path).getAbsolutePath());
      }

      @Override
      public void visitManifest(ManifestModel manifest) {
         manifest.setPath(m_manifestFile.getAbsolutePath());

         super.visitManifest(manifest);
      }
   }

   private static class ModelMerger extends BaseVisitor {
      private Any m_result = new Any();

      private StructureModel m_structure;

      private Stack<NodeModel> m_metas = new Stack<NodeModel>();

      private Stack<Any> m_datas = new Stack<Any>();

      public ModelMerger(StructureModel structure) {
         m_structure = structure;
      }

      private Any findData(NodeModel meta, Any current, Any parent) {
         String name = current.getName();
         String key = meta.getKey();

         if (key != null && key.length() > 0) {
            // find the matched one
            List<String> list = Splitters.by(',').trim().split(key);

            for (String item : list) {
               String value = current.getAttribute(item);

               if (value != null) {
                  for (Any child : parent.getAllChildren(name)) {
                     if (value.equals(child.getAttribute(item))) {
                        return child;
                     }
                  }
               }
            }

            // append a new one
            Any child = new Any().setName(name);

            parent.addChild(child);
            return child;
         } else {
            // use first one
            Any child = parent.getFirstChild(name);

            if (child == null) {
               child = new Any().setName(name);
               parent.addChild(child);
            }

            return child;
         }
      }

      public Any getResult() {
         return m_result;
      }

      @Override
      public void visitAny(Any current) {
         // meta
         NodeModel meta;

         if (m_metas.isEmpty()) {
            meta = m_metas.push(m_structure.getNode());
         } else {
            meta = m_metas.peek().findNode(current.getName());

            if (meta == null) {
               String format = "Tag(%s) under tag(%s) is NOT declared in the structure file!";

               throw new IllegalStateException(String.format(format, current.getName(), m_datas.peek().getName()));
            }

            m_metas.push(meta);
         }

         // data
         Any original;

         if (m_datas.isEmpty()) {
            original = m_result.setName(current.getName());
         } else {
            original = findData(meta, current, m_datas.peek());
         }

         original.mergeAttributes(current);

         if (current.getValue() != null) {
            if (original.getValue() == null) {
               original.setValue(current.getValue());
            } else {
               original.setValue(original.getValue() + current.getValue());
            }
         }

         m_datas.push(original);

         // children
         super.visitAny(current);

         m_metas.pop();
         m_datas.pop();
      }
   }
}
