package org.unidal.maven.plugin.wizard.meta;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.maven.plugin.wizard.meta.DefaultModelMeta.Utils;

public class ModelMetaTest {
   private void checkValueTypes(String expected, String... values) {
      List<String> list = Arrays.asList(values);
      Set<String> types = new HashSet<String>(list);
      String actual = Utils.getValueType(types);

      Assert.assertEquals("Value types:" + list + ". ", expected, actual);
   }

   @Test
   public void guessValueType() {
      checkGuess("int", "123");
      checkGuess("long", "25314761301");
      checkGuess("double", "123.45");
      checkGuess("String", "123.45.67");
      
      checkGuess("Date", "2015-05-27");
      checkGuess("Date", "2015-05-27 16:45:12");
      checkGuess("Date", "2015-05-27T16:45:12");
   }

   private void checkGuess(String expected, String value) {
      String[] valueTypeAndFormat = Utils.guessValueTypeAndFormat(value);

      Assert.assertEquals("Type mismatch.", expected, valueTypeAndFormat[0]);
   }

   @Test
   public void testValueTypes() {
      checkValueTypes("long", "int", "long");
      checkValueTypes("double", "int", "double");
      checkValueTypes("String", "int", "boolean");
      checkValueTypes("String", "int", "String");

      checkValueTypes("long", "long", "int");
      checkValueTypes("double", "long", "double");
      checkValueTypes("String", "long", "Date");
      checkValueTypes("String", "long", "boolean");

      checkValueTypes("String", "Date", "boolean");
      checkValueTypes("String", "Date", "int");
      checkValueTypes("String", "Date", "long");
      checkValueTypes("String", "Date", "double");

      checkValueTypes("String", "String", "boolean");
      checkValueTypes("String", "String", "int");
      checkValueTypes("String", "String", "long");
      checkValueTypes("String", "String", "double");
   }
}
