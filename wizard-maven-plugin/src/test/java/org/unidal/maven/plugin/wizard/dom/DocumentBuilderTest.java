package org.unidal.maven.plugin.wizard.dom;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.jdom.Document;
import org.junit.Test;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.pom.DomAccessor;

public class DocumentBuilderTest extends ComponentTestCase {
   @Test
   public void testSelectNodes() throws Exception {

      File wizardFile = new File("src/test/resources/META-INF/wizard/jdbc/wizard.xml");
      DomAccessor db = new DomAccessor();
      Document wizardDocument = db.loadDocument(wizardFile);
      List<String> tables = db.selectAttributes(wizardDocument, "/wizard/jdbc/group/table/@name");

      String table = "DP_AdminLogin";
      String group = db.selectAttribute(wizardDocument, "/wizard/jdbc/group[table/@name=$name]/@name", "name", table);

      Assert.assertEquals(13, tables.size());
      Assert.assertEquals("user", group);

      File manifestFile = new File(String.format("src/test/resources/META-INF/dal/jdbc/%s-manifest.xml", group));
      XmlAggregator m_aggregator = lookup(XmlAggregator.class, "dal-jdbc");
      String aggregatedXml = m_aggregator.aggregate(manifestFile);

      Document jdbcDocument = db.loadDocument(aggregatedXml);
      String packageName = db.selectAttribute(jdbcDocument, "/entities/@do-package");
      String entityName = db.selectAttribute(jdbcDocument, "/entities/entity[@table=$table]/@name", "table", table);

      Assert.assertEquals("com.dainping.cat.home.dal.user", packageName);
      Assert.assertEquals("dp-admin-login", entityName);
   }
}
