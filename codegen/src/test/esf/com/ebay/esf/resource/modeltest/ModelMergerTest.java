package com.ebay.esf.resource.modeltest;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ebay.esf.resource.model.Models;
import com.ebay.esf.resource.model.entity.Root;

public class ModelMergerTest {
   @Test
   public void testClone() throws IOException, SAXException {
      Root src = Models.forXml().parse(getClass().getResourceAsStream("model_final.xml"), "utf-8");
      Root dst = Models.forObject().clone(src);

      Assert.assertEquals(src.toString(), dst.toString());

      String expected = "{\r\n" + //
            "   \"common-slots\": [\r\n" + //
            "      {\r\n" + //
            "         id: \"sys1\",\r\n" + //
            "         resource: [\r\n" + //
            "            {urn: \"js:local:/js/home/first.js\"},\r\n" + //
            "            {urn: \"js:local:/js/home/third2.js\"}]\r\n" + //
            "      },\r\n" + //
            "      {id: \"sys2\"}],\r\n" + //
            "   pages: [\r\n" + //
            "      {\r\n" + //
            "         \"request-uri\": \"/jsp/home.jsp\",\r\n" + //
            "         slot: [\r\n" + //
            "            {\r\n" + //
            "               id: \"HEAD\",\r\n" + //
            "               resource: [\r\n" + //
            "                  {urn: \"js:local:/js/home/first.js\"},\r\n" + //
            "                  {urn: \"js:global:/META-INF/js/tracking.js\"}]\r\n" + //
            "            },\r\n" + //
            "            {\r\n" + //
            "               id: \"BODY\",\r\n" + //
            "               resource: [\r\n" + //
            "                  {urn: \"js:local:/js/home/third.js\"},\r\n" + //
            "                  {urn: \"js:local:/js/home/third2.js\",\"old-urn\": \"js:local:/js/home/third.js\"}]\r\n" + //
            "            },\r\n" + //
            "            {\r\n" + //
            "               id: \"BOTTOM\",\r\n" + //
            "               override: \"true\",\r\n" + //
            "               resource: [\r\n" + //
            "                  {urn: \"js:local:/js/home/big.js\"},\r\n" + //
            "                  {urn: \"js:local:/js/home/second.js\"}]\r\n" + //
            "            },\r\n" + //
            "            {\r\n" + //
            "               id: \"ABOVE_FOLD\",\r\n" + //
            "               resource: [\r\n" + //
            "                  {urn: \"js:global:/path/to/other.js\",enabled: \"false\"},\r\n" + //
            "                  {urn: \"js:resrepo:/logical/path/to/another.js\"},\r\n" + //
            "                  {urn: \"js:global:/path/to/other2.js\"}]\r\n" + //
            "            },\r\n" + //
            "            {id: \"rtm\"}],\r\n" + //
            "         \"common-slot-ref\": [\r\n" + //
            "            {id: \"sys1\",\"before-slot\": \"HEAD\"},\r\n" + //
            "            {id: \"sys2\",\"after-slot\": \"BODY\"}],\r\n" + //
            "         \"slot-group\": [\r\n" + //
            "            {\r\n" + //
            "               id: \"group1\",\r\n" + //
            "               \"main-slot\": \"BODY\",\r\n" + //
            "               \"slot-ref\": [\r\n" + //
            "                  {id: \"rtm\"},\r\n" + //
            "                  {id: \"BODY\"}]\r\n" + //
            "            }]\r\n" + //
            "      }]\r\n" + //
            "}";

      Assert.assertEquals(expected, Models.forJson().build(src));
   }

   @Test
   public void testMergeOne() throws IOException, SAXException {
      Root src = Models.forXml().parse(getClass().getResourceAsStream("model_final.xml"), "utf-8");

      // only merge once, the result should be same as input
      Root dst1 = new Root();

      Models.forObject().merge(dst1, src);

      Assert.assertEquals(src.toString(), dst1.toString());

      // merge same model twice, result keeps same
      Root dst2 = new Root();

      Models.forObject().merge(dst2, src, src);

      Assert.assertEquals(src.toString(), dst2.toString());
      Assert.assertEquals(dst1.toString(), dst2.toString());
   }

   @Test
   public void testMergeMultiple() throws IOException, SAXException {
      Root src1 = Models.forXml().parse(getClass().getResourceAsStream("model_base.xml"), "utf-8");
      Root src2 = Models.forXml().parse(getClass().getResourceAsStream("model_override.xml"), "utf-8");
      Root expected = Models.forXml().parse(getClass().getResourceAsStream("model_final.xml"), "utf-8");

      Root dst = new Root();

      Models.forObject().merge(dst, src1, src2);

      Assert.assertEquals(expected.toString(), dst.toString());
   }
}
