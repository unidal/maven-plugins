package org.unidal.codegen.framework;

import java.io.File;
import java.io.IOException;

import org.unidal.codegen.model.entity.Any;
import org.unidal.codegen.model.entity.StructureModel;
import org.unidal.codegen.model.entity.ManifestModel;

public interface XmlAggregator {
   public Any aggregate(File manifestXml, StructureModel structure, ManifestModel manifest) throws IOException;
}
