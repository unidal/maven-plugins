package org.unidal.codegen.generator.model.cat;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.dianping.cat.consumer.transaction.model.entity.TransactionReport;
import com.dianping.cat.consumer.transaction.model.transform.DefaultSaxParser;

public class NativeTest {
   @Test
   public void test() throws SAXException, IOException {
      InputStream in = getClass().getResourceAsStream("/transaction.xml");
      TransactionReport report = DefaultSaxParser.parse(in);
      
      System.out.println(report);
   }
}
