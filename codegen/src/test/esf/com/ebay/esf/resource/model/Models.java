package com.ebay.esf.resource.model;

import static com.ebay.esf.resource.model.Constants.ENTITY_ROOT;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import com.ebay.esf.dom.ITagNode;
import com.ebay.esf.resource.model.entity.Root;
import com.ebay.esf.resource.model.transform.DefaultJsonBuilder;
import com.ebay.esf.resource.model.transform.DefaultLinker;
import com.ebay.esf.resource.model.transform.DefaultValidator;
import com.ebay.esf.resource.model.transform.DefaultXmlBuilder;
import com.ebay.esf.resource.model.transform.ModelMerger;
import com.ebay.esf.resource.model.transform.TagNodeBasedMaker;
import com.ebay.esf.resource.model.transform.TagNodeBasedParser;
import com.ebay.esf.tag.core.TagXmlParser;
import com.site.helper.Files;

public class Models {
   public static ObjectModel forObject() {
      return ObjectModel.INSTANCE;
   }

   public static XmlModel forXml() {
      return XmlModel.INSTANCE;
   }

   public static JsonModel forJson() {
      return JsonModel.INSTANCE;
   }

   public static enum ObjectModel {
      INSTANCE;

      public Root clone(Root from) {
         Root cloned = new Root();
         ModelMerger merger = new ModelMerger(cloned);

         from.accept(merger);
         return cloned;
      }

      public void merge(Root to, Root... froms) {
         ModelMerger merger = new ModelMerger(to);

         for (Root from : froms) {
            from.accept(merger);
         }
      }

      public void validate(Root root) {
         new DefaultValidator().visitRoot(root);
      }
   }

   public static enum XmlModel {
      INSTANCE;

      public String build(Root root) {
         DefaultXmlBuilder visitor = new DefaultXmlBuilder();

         root.accept(visitor);
         return visitor.getString();
      }

      public Root parse(InputStream in, String charset) throws IOException, SAXException {
         String xml = Files.forIO().readFrom(in, charset);

         return parse(xml);
      }

      public Root parse(String xml) throws SAXException, IOException {
         ITagNode doc = new TagXmlParser().parse(xml);
         TagNodeBasedMaker maker = new TagNodeBasedMaker();
         DefaultLinker linker = new DefaultLinker();
         ITagNode rootNode = doc.getChildTagNode(ENTITY_ROOT);

         if (rootNode == null) {
            throw new RuntimeException(String.format("Root element(%s) is expected!", ENTITY_ROOT));
         }

         Root root = new TagNodeBasedParser().parse(maker, linker, rootNode);

         return root;
      }
   }

   public static enum JsonModel {
      INSTANCE;

      public String build(Root root) {
         DefaultJsonBuilder visitor = new DefaultJsonBuilder();

         root.accept(visitor);
         return visitor.getString();
      }

      public Root parse(InputStream in, String charset) throws IOException, SAXException {
         String json = Files.forIO().readFrom(in, charset);

         return parse(json);
      }

      public Root parse(String json) throws SAXException, IOException {
         ITagNode doc = new TagXmlParser().parse(json);
         TagNodeBasedMaker maker = new TagNodeBasedMaker();
         DefaultLinker linker = new DefaultLinker();
         ITagNode rootNode = doc.getChildTagNode(ENTITY_ROOT);

         if (rootNode == null) {
            throw new RuntimeException(String.format("Root element(%s) is expected!", ENTITY_ROOT));
         }

         Root root = new TagNodeBasedParser().parse(maker, linker, rootNode);

         return root;
      }
   }
}
