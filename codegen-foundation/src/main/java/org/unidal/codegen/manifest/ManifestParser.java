package org.unidal.codegen.manifest;

import java.util.List;

public interface ManifestParser {
   public List<Manifest> parse(String content);
}
