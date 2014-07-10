package org.unidal.codegen.generator.model.all;

import java.io.InputStream;

import org.junit.Test;

import com.dianping.zebra.admin.sqlMap.entity.SqlMap;
import com.dianping.zebra.admin.sqlMap.transform.DefaultSaxParser;

public class SqlMapTest {
   @Test
   public void test() throws Exception {
      InputStream in = getClass().getResourceAsStream("sqlMap.xml");
      SqlMap map = DefaultSaxParser.parse(in);

      System.out.println(map);
   }
}
