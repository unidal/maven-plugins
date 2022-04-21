package org.unidal.codegen.framework;

import java.io.IOException;

import org.unidal.codegen.model.entity.OutputModel;

public interface FileStorage {
   public void copyResources(GenerationContext ctx, OutputModel output) throws IOException;

   public void writeFile(GenerationContext ctx, OutputModel output, String content) throws IOException;
}
