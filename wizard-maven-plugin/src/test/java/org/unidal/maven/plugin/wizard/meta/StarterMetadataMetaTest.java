package org.unidal.maven.plugin.wizard.meta;

import java.io.StringReader;
import java.io.StringWriter;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class StarterMetadataMetaTest extends ComponentTestCase {
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
   public void testGameModel() throws Exception {
      checkModelMeta("starter-metadata.xml", "starter-metadata-meta.xml");
   }
}
