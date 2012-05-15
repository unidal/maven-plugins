package com.site.codegen.meta;

import junit.framework.Assert;

import org.junit.Test;

public class TableMetaTest {
   @Test
   public void testNormalizeFieldName() {
      DefaultTableMeta meta = new DefaultTableMeta();

      Assert.assertEquals("field-name", meta.normalize("fieldName"));
      Assert.assertEquals("field-name", meta.normalize("FieldName"));
      Assert.assertEquals("field-name", meta.normalize("field-name"));
      Assert.assertEquals("field-name", meta.normalize("field-Name"));
      Assert.assertEquals("field-name", meta.normalize("Field-Name"));
      Assert.assertEquals("field-name", meta.normalize("field_name"));
      Assert.assertEquals("field-name", meta.normalize("field_Name"));
      Assert.assertEquals("field-name", meta.normalize("Field_Name"));
      
      Assert.assertEquals("field-name", meta.normalize("FIELD-NAME"));
      Assert.assertEquals("field-name", meta.normalize("FIELD_NAME"));
   }
}
