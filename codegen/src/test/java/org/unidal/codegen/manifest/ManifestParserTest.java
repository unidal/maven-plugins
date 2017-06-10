package org.unidal.codegen.manifest;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.helper.Files;
import org.unidal.lookup.ComponentTestCase;

public class ManifestParserTest extends ComponentTestCase {
   @Test
   public void testParseJdbcManifest() throws Exception {
      ManifestParser parser = lookup(ManifestParser.class);
      String content = Files.forIO().readUtf8String(getClass().getResourceAsStream("jdbc_manifest.xml"));

      List<Manifest> manifests = parser.parse(content);
      Assert.assertEquals(10, manifests.size());
      
      Manifest m0 = manifests.get(0);
      Assert.assertEquals(FileMode.CREATE_OR_OVERWRITE, m0.getMode());
      Assert.assertEquals("/com/site/app/user/dal/User.java", m0.getPath());
      Assert.assertEquals("/META-INF/dal/jdbc/do.xsl", m0.getTemplate());
   }
   
   @Test
   public void testParseXmlManifest() throws Exception {
      ManifestParser parser = lookup(ManifestParser.class);
      String content = Files.forIO().readUtf8String(getClass().getResourceAsStream("xml_manifest.xml"));
      
      List<Manifest> manifests = parser.parse(content);
      Assert.assertEquals(5, manifests.size());
      
      Manifest m0 = manifests.get(0);
      Assert.assertEquals(FileMode.CREATE_OR_OVERWRITE, m0.getMode());
      Assert.assertEquals("/com/site/app/game/xml/Game.java", m0.getPath());
      Assert.assertEquals("/META-INF/dal/xml/do.xsl", m0.getTemplate());
   }
}
