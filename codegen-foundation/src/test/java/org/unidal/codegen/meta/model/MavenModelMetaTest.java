package org.unidal.codegen.meta.model;

import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.meta.ModelMeta;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class MavenModelMetaTest extends ComponentTestCase {
   private void checkModelMeta(String sourceResource, String targetResource) throws Exception {
      ModelMeta xmlMeta = lookup(ModelMeta.class);
      XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
      StringWriter writer = new StringWriter(8192);
      String source = Files.forIO().readFrom(getClass().getResourceAsStream(sourceResource), "utf-8");
      Document root = xmlMeta.getCodegen(new StringReader(source));

      outputter.output(root, writer);

      String result = writer.toString();
      String expected = Files.forIO().readFrom(getClass().getResourceAsStream(targetResource), "utf-8");

      Assert.assertEquals(expected.replaceAll("\\r", ""), result.replaceAll("\\r", ""));
   }

   @Test
   public void testGroupMavenMetadata() throws Exception {
      checkModelMeta("group-maven-metadata.xml", "group-maven-metadata-meta.xml");
   }

   @Test
   public void testPluginMavenMetadata() throws Exception {
      checkModelMeta("plugin-maven-metadata.xml", "plugin-maven-metadata-meta.xml");
   }
}
