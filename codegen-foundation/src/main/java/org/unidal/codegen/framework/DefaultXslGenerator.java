package org.unidal.codegen.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.codegen.model.CodegenModelHelper;
import org.unidal.codegen.model.entity.StructureModel;
import org.unidal.codegen.model.entity.ManifestModel;
import org.unidal.codegen.model.entity.OutputModel;
import org.unidal.codegen.model.entity.OutputsModel;
import org.unidal.codegen.model.entity.PropertyModel;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named(type = XslGenerator.class)
public class DefaultXslGenerator implements XslGenerator {
   @Inject
   private XmlAggregator m_aggregator;

   @Inject
   private XslTransformer m_transformer;

   @Inject
   private FileStorage m_storage;

   public void generate(GenerationContext ctx) throws Exception {
      File manifestFile = new File(ctx.getManifestXml().getPath());
      FileInputStream min = new FileInputStream(manifestFile);
      InputStream sin = ctx.getStructureXml().openStream();
      ManifestModel manifest = CodegenModelHelper.fromXml(ManifestModel.class, min);
      StructureModel structure = CodegenModelHelper.fromXml(StructureModel.class, sin);

      ctx.verbose("Aggregating ...");
      String aggregatedXml = m_aggregator.aggregate(manifestFile, structure, manifest).toString();
      ctx.debug(aggregatedXml);

      ctx.verbose("Normalizing ...");
      String normalizedXml = m_transformer.transform(ctx.getNormalizeXsl(), aggregatedXml, ctx.getProperties());
      ctx.debug(normalizedXml);

      ctx.verbose("Decorating ...");
      String decoratedXml = m_transformer.transform(ctx.getDecorateXsl(), normalizedXml, ctx.getProperties());
      ctx.debug(decoratedXml);

      ctx.verbose("Manifesting ...");
      String manifestXml = m_transformer.transform(ctx.getManifestXsl(), decoratedXml, ctx.getProperties());
      ctx.debug(manifestXml);

      ctx.verbose("Generating ...");
      OutputsModel outputs = CodegenModelHelper.fromXml(OutputsModel.class, manifestXml);

      if (!outputs.getOutputs().isEmpty()) {
         for (OutputModel output : outputs.getOutputs()) {
            OperationMode op = OperationMode.getByName(output.getOp());

            switch (op) {
            case APPLY_TEMPLATE:
               URL templateXsl = ctx.getTemplateXsl(output.getTemplate());
               Map<String, String> properties = toProperties(output.getProperties());
               String content = m_transformer.transform(templateXsl, decoratedXml, properties);

               m_storage.writeFile(ctx, output, content);
               break;
            case COPY_RESOURCES:
               m_storage.copyResources(ctx, output);
               break;
            default:
               throw new IllegalStateException(String.format("Unsupported op(%s)!", op));
            }
         }

         ctx.info(ctx.getGeneratedFiles().get() + " files generated.");
      }
   }

   private Map<String, String> toProperties(List<PropertyModel> properties) {
      Map<String, String> map = new HashMap<String, String>();

      for (PropertyModel property : properties) {
         map.put(property.getName(), property.getText());
      }

      return map;
   }
}
