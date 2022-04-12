package org.unidal.codegen.manifest;

import java.io.IOException;

public interface ManifestCreator {

   public String create(String generatedContent, String userContent) throws IOException;
}
