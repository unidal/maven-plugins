package org.unidal.codegen.template;

import java.net.URL;

import javax.xml.transform.Templates;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class XslTemplateManagerTest extends ComponentTestCase {
   private void checkTemplate(String resource) throws Exception {
      XslTemplateManager manager = lookup(XslTemplateManager.class);
      URL url = getClass().getResource(resource);

      Assert.assertNotNull("Resource " + resource + " is not found.", url);

      Templates templates = manager.getTemplates(url);

      Assert.assertNotNull(templates);
      Assert.assertNotNull(templates.newTransformer());
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
}
