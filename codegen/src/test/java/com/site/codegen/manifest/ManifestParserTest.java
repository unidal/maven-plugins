package com.site.codegen.manifest;

import java.util.List;

import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.manifest.FileMode;
import com.site.codegen.manifest.Manifest;
import com.site.codegen.manifest.ManifestParser;
import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class ManifestParserTest extends ComponentTestCase {
   @Test
   public void testParseJdbcManifest() throws Exception {
      ManifestParser parser = lookup(ManifestParser.class);
      String content = IOUtil.toString(getClass().getResourceAsStream("jdbc_manifest.xml"));

      List<Manifest> manifests = parser.parse(content);
      assertEquals(10, manifests.size());
      
      Manifest m0 = manifests.get(0);
      assertEquals(FileMode.CREATE_OR_OVERWRITE, m0.getMode());
      assertEquals("/com/site/app/user/dal/User.java", m0.getPath());
      assertEquals("/META-INF/dal/jdbc/do.xsl", m0.getTemplate());
   }
   
   @Test
   public void testParseXmlManifest() throws Exception {
      ManifestParser parser = lookup(ManifestParser.class);
      String content = IOUtil.toString(getClass().getResourceAsStream("xml_manifest.xml"));
      
      List<Manifest> manifests = parser.parse(content);
      assertEquals(5, manifests.size());
      
      Manifest m0 = manifests.get(0);
      assertEquals(FileMode.CREATE_OR_OVERWRITE, m0.getMode());
      assertEquals("/com/site/app/game/xml/Game.java", m0.getPath());
      assertEquals("/META-INF/dal/xml/do.xsl", m0.getTemplate());
   }
}
