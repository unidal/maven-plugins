package org.unidal.codegen.generator;

import java.net.URL;
import java.util.List;

import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.codegen.generator.GenerateContext.LogLevel;
import org.unidal.codegen.manifest.Manifest;
import org.unidal.codegen.manifest.ManifestParser;
import org.unidal.codegen.manifest.OperationMode;
import org.unidal.codegen.transformer.XslTransformer;
import org.unidal.lookup.annotation.Inject;

public class XslGenerator implements Generator {
   @Inject
   private XmlAggregator m_xmlAggregator;

   @Inject
   private XslTransformer m_xslTransformer;

   @Inject
   private ManifestParser m_manifestParser;

   public void generate(GenerateContext ctx) throws Exception {
      try {
         ctx.log(LogLevel.INFO, "Aggregating ...");
         String aggregatedXml = m_xmlAggregator.aggregate(ctx.getManifestXml());
         ctx.log(LogLevel.DEBUG, aggregatedXml);

         ctx.log(LogLevel.INFO, "Normalizing ...");
         String normalizedXml = m_xslTransformer.transform(ctx.getNormalizeXsl(), aggregatedXml);
         ctx.log(LogLevel.DEBUG, normalizedXml);

         ctx.log(LogLevel.INFO, "Decorating ...");
         String decoratedXml = m_xslTransformer.transform(ctx.getDecorateXsl(), normalizedXml);
         ctx.log(LogLevel.DEBUG, decoratedXml);

         ctx.log(LogLevel.INFO, "Manifesting ...");
         String manifestXml = m_xslTransformer.transform(ctx.getManifestXsl(), decoratedXml, ctx.getProperties());
         List<Manifest> manifests = m_manifestParser.parse(manifestXml);
         ctx.log(LogLevel.DEBUG, manifestXml);

         ctx.log(LogLevel.INFO, "Generating ...");
         if (!manifests.isEmpty()) {
            ctx.openStorage();

            for (Manifest manifest : manifests) {
               OperationMode op = manifest.getOp();

               switch (op) {
               case APPLY_TEMPLATE:
                  URL templateXsl = ctx.getTemplateXsl(manifest.getTemplate());
                  String content = m_xslTransformer.transform(templateXsl, decoratedXml, manifest.getProperties());

                  ctx.addFileToStorage(manifest, content);
                  break;
               case COPY_RESOURCES:
                  ctx.copyFileToStorage(manifest);
                  break;
               }
            }

            ctx.closeStorage();
         }
      } catch (Exception e) {
         throw e;
      }
   }
}
