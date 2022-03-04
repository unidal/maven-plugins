package org.unidal.codegen.generator.model.cat2;

import java.io.File;

import org.junit.Test;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.lookup.ComponentTestCase;

public class CatAggregateTest extends ComponentTestCase {
   @Test
   public void test() {
      XmlAggregator aggregator = lookup(XmlAggregator.class, "dal-model");
      String xml = aggregator.aggregate(new File(getClass().getResource("client-config-manifest.xml").getPath()));

      System.out.println(xml);
   }
}
