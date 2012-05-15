package com.ebay.esf.resource.modeltest;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ebay.esf.resource.model.Models;
import com.ebay.esf.resource.model.entity.CommonSlotRef;
import com.ebay.esf.resource.model.entity.Page;
import com.ebay.esf.resource.model.entity.Resource;
import com.ebay.esf.resource.model.entity.Root;
import com.ebay.esf.resource.model.entity.Slot;
import com.ebay.esf.resource.model.entity.SlotGroup;
import com.ebay.esf.resource.model.entity.SlotRef;

public class ModelTest {
   @Test
   public void testWithJava() {
      Root model = new Root();

      model.addCommonSlot(new Slot("sys1").addResource(new Resource("firstJs")).addResource(new Resource("thirdJs")));
      model.addPage(new Page("/jsp/home.jsp")
            //
            .addSlot(new Slot("HEAD").addResource(new Resource("trackingJs")))
            .addCommonSlotRef(new CommonSlotRef("sys1").setBeforeSlot("HEAD")) //
            .addCommonSlotRef(new CommonSlotRef("sys2").setAfterSlot("BODY")) //
            .addSlotGroup(new SlotGroup("group1") //
                  .setMainSlot("BODY") //
                  .addSlotRef(new SlotRef("Header")) //
                  .addSlotRef(new SlotRef("BODY"))) //
      );

      // validate it, or throw RuntimeException
      Models.forObject().validate(model);
   }

   @Test
   public void testForXml() throws IOException, SAXException {
      Root src = Models.forXml().parse(getClass().getResourceAsStream("model_final.xml"), "utf-8");

      // validate it, or throw RuntimeException
      Models.forObject().validate(src);

      Root src2 = Models.forXml().parse(src.toString());

      System.out.println(src2);

      // parse, build, parse and build again should work well
      // this proves parsing and building are consistent
      Assert.assertEquals(src.toString(), src2.toString());
   }
}
