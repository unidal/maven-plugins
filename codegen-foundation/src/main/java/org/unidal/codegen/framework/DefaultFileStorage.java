package org.unidal.codegen.framework;

import java.io.File;
import java.io.IOException;

import org.unidal.codegen.model.entity.OutputModel;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Named;

@Named(type = FileStorage.class)
public class DefaultFileStorage implements FileStorage {
   @Override
   public void copyResources(GenerationContext ctx, OutputModel output) throws IOException {
      // System.out.println(output.getTemplate() + "=>" + output.getPath());
   }

   @Override
   public void writeFile(GenerationContext ctx, OutputModel output, String content) throws IOException {
      FileMode mode = FileMode.getByName(output.getMode());
      File target = ctx.getFile(output.getPath());

      switch (mode) {
      case CREATE_IF_NOT_EXISTS:
         if (target.exists()) {
            break;
         }
      case CREATE_OR_OVERWRITE:
         target.getParentFile().mkdirs();

         Files.forIO().writeTo(target, content);

         ctx.verbose(String.format("File %s generated.", target));
         ctx.getGeneratedFiles().incrementAndGet();
         break;
      }
   }
}
