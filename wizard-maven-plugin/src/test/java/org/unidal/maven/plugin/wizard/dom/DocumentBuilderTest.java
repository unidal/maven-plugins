package org.unidal.maven.plugin.wizard.dom;

import java.io.File;
import java.util.List;

import junit.framework.Assert;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;
import org.unidal.codegen.aggregator.XmlAggregator;
import org.unidal.lookup.ComponentTestCase;
import org.unidal.maven.plugin.wizard.dom.JDomBuilder.Function;
import org.unidal.maven.plugin.wizard.template.entity.Field;

public class DocumentBuilderTest extends ComponentTestCase {
   @Test
   public void testSelectNodes() throws Exception {
      XmlAggregator m_aggregator = lookup(XmlAggregator.class);

      File wizardFile = new File("src/test/resources/META-INF/wizard/jdbc/wizard.xml");
      JDomBuilder db = new JDomBuilder();
      Document wizardDocument = db.loadDocument(wizardFile);
      List<String> tables = db.selectAttributes(wizardDocument, "/wizard/jdbc/group/table/@name");

      String table = "DP_AdminLogin";
      String group = db.selectAttribute(wizardDocument, "/wizard/jdbc/group[table/@name=$name]/@name", "name", table);

      Assert.assertEquals(13, tables.size());
      Assert.assertEquals("user", group);

      File manifestFile = new File(String.format("src/test/resources/META-INF/dal/jdbc/%s-manifest.xml", group));
      String aggregatedXml = m_aggregator.aggregate(manifestFile);
      Document jdbcDocument = db.loadDocument(aggregatedXml);
      String packageName = db.selectAttribute(jdbcDocument, "/entities/@do-package");
      String entityName = db.selectAttribute(jdbcDocument, "/entities/entity[@table=$table]/@name", "table", table);
      List<Field> fields = db.selectNodes(jdbcDocument, "/entities/entity[@table=$table]/member", new Function<Element, Field>() {
         @Override
         public Field apply(Element e) {
            Field field = new Field();
            String key = e.getAttributeValue("key");

            field.setName(e.getAttributeValue("name"));
            field.setValueType(e.getAttributeValue("value-type"));

            if (key != null && "true".equals(key)) {
               field.setKey(true);
            }

            return field;
         }
      }, "table", table);

      Assert.assertEquals("com.dainping.cat.home.dal.user", packageName);
      Assert.assertEquals("dp-admin-login", entityName);
      Assert.assertEquals(7, fields.size());
   }
}
