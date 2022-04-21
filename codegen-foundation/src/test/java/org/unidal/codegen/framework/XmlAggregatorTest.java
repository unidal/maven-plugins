package org.unidal.codegen.framework;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.model.CodegenModelHelper;
import org.unidal.codegen.model.entity.Any;
import org.unidal.codegen.model.entity.StructureModel;
import org.unidal.codegen.model.entity.ManifestModel;
import org.unidal.lookup.ComponentTestCase;

public class XmlAggregatorTest extends ComponentTestCase {
   @Test
   public void test() throws Exception {
      URL url = getClass().getResource("meta-manifest.xml");
      InputStream sin = getClass().getResourceAsStream("structure.xml");
      InputStream in = getClass().getResourceAsStream("meta.xml");

      StructureModel structure = CodegenModelHelper.fromXml(StructureModel.class, sin);
      ManifestModel manifest = CodegenModelHelper.fromXml(ManifestModel.class, url.openStream());
      XmlAggregator aggregator = lookup(XmlAggregator.class);

      Any result = aggregator.aggregate(new File(url.getPath()), structure, manifest);
      Any expected = CodegenModelHelper.fromXml(Any.class, in);

      Assert.assertEquals(expected.toString(), result.toString());
   }
}
