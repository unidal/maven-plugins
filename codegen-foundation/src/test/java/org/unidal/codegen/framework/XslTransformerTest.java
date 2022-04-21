package org.unidal.codegen.framework;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.codegen.model.entity.Any;
import org.unidal.lookup.ComponentTestCase;

public class XslTransformerTest extends ComponentTestCase {
   private Map<String, String> buildProperties() {
      Map<String, String> properties = new HashMap<String, String>();

      properties.put("package", getClass().getPackage().getName());

      return properties;
   }

   private Any buildSource() {
      Any source = new Any().setName("entity");

      source.setAttribute("class-name", "HelloWorld");

      return source;
   }

   @Test
   public void test() throws Exception {
      XslTransformer transformer = lookup(XslTransformer.class);
      Any source = buildSource();

      URL template = getClass().getResource("xsl/template.xsl");
      String actual = transformer.transform(template, source.toString(), buildProperties());
      String expected = "package org.unidal.codegen.framework;\n\n" //
            + "public class HelloWorld {\n" //
            + "}\n";

      Assert.assertEquals(expected, actual);
   }
}
