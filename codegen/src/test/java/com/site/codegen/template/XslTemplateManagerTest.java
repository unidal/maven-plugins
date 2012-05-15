package com.site.codegen.template;

import java.net.URL;

import javax.xml.transform.Templates;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.site.codegen.template.XslTemplateManager;
import com.site.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class XslTemplateManagerTest extends ComponentTestCase {
   private void checkTemplate(String resource) throws Exception {
      XslTemplateManager manager = lookup(XslTemplateManager.class);
      URL url = getClass().getResource(resource);

      assertNotNull("Resource " + resource + " is not found.", url);

      Templates templates = manager.getTemplates(url);

      assertNotNull(templates);
      assertNotNull(templates.newTransformer());
   }

   @Test
   public void testJdbcDoXsl() throws Exception {
      checkTemplate("/META-INF/dal/jdbc/do.xsl");
   }

   @Test
   public void testJdbcManifestXsl() throws Exception {
      checkTemplate("/META-INF/dal/jdbc/manifest.xsl");
   }

   @Test
   public void testJdbcNormalizeXsl() throws Exception {
      checkTemplate("/META-INF/dal/jdbc/normalize.xsl");
   }

   @Test
   public void testXmlDoXsl() throws Exception {
      checkTemplate("/META-INF/dal/xml/do.xsl");
   }

   @Test
   public void testXmlManifestXsl() throws Exception {
      checkTemplate("/META-INF/dal/xml/manifest.xsl");
   }

   @Test
   public void testXmlNormalizeXsl() throws Exception {
      checkTemplate("/META-INF/dal/xml/normalize.xsl");
   }
}
