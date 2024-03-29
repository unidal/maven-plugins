/* THIS FILE WAS AUTO GENERATED BY codegen-maven-plugin, DO NOT EDIT IT */
package org.unidal.maven.plugin.source.lines.transform;

import static org.unidal.maven.plugin.source.lines.Constants.ATTR_CLASS;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_COMMENT;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_EMPTY;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_FILES;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_GENERATED;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_GENERATED_FILES;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_LINES;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_NAME;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_PACKAGE;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_TEST;
import static org.unidal.maven.plugin.source.lines.Constants.ATTR_TEST_FILES;

import org.xml.sax.Attributes;

import org.unidal.maven.plugin.source.lines.entity.ClassModel;
import org.unidal.maven.plugin.source.lines.entity.CountModel;
import org.unidal.maven.plugin.source.lines.entity.ProjectModel;
import org.unidal.maven.plugin.source.lines.entity.RootModel;

public class DefaultXmlMaker {

   public ClassModel buildClass(Attributes attributes) {
      String _package = attributes.getValue(ATTR_PACKAGE);
      String _class = attributes.getValue(ATTR_CLASS);
      String generated = attributes.getValue(ATTR_GENERATED);
      String test = attributes.getValue(ATTR_TEST);
      ClassModel class_ = new ClassModel(_package, _class);

      if (generated != null) {
         class_.setGenerated(convert(Boolean.class, generated, null));
      }

      if (test != null) {
         class_.setTest(convert(Boolean.class, test, null));
      }

      return class_;
   }

   public CountModel buildCount(Attributes attributes) {
      String lines = attributes.getValue(ATTR_LINES);
      String empty = attributes.getValue(ATTR_EMPTY);
      String comment = attributes.getValue(ATTR_COMMENT);
      String files = attributes.getValue(ATTR_FILES);
      String generatedFiles = attributes.getValue(ATTR_GENERATED_FILES);
      String testFiles = attributes.getValue(ATTR_TEST_FILES);
      CountModel count = new CountModel();

      if (lines != null) {
         count.setLines(convert(Integer.class, lines, 0));
      }

      if (empty != null) {
         count.setEmpty(convert(Integer.class, empty, 0));
      }

      if (comment != null) {
         count.setComment(convert(Integer.class, comment, 0));
      }

      if (files != null) {
         count.setFiles(convert(Integer.class, files, 0));
      }

      if (generatedFiles != null) {
         count.setGeneratedFiles(convert(Integer.class, generatedFiles, 0));
      }

      if (testFiles != null) {
         count.setTestFiles(convert(Integer.class, testFiles, 0));
      }

      return count;
   }

   public ProjectModel buildProject(Attributes attributes) {
      String name = attributes.getValue(ATTR_NAME);
      ProjectModel project = new ProjectModel(name);

      return project;
   }

   public RootModel buildRoot(Attributes attributes) {
      RootModel root = new RootModel();

      return root;
   }

   @SuppressWarnings("unchecked")
   protected <T> T convert(Class<T> type, String value, T defaultValue) {
      if (value == null || value.length() == 0) {
         return defaultValue;
      }

      if (type == Boolean.class || type == Boolean.TYPE) {
         return (T) Boolean.valueOf(value);
      } else if (type == Integer.class || type == Integer.TYPE) {
         return (T) Integer.valueOf(value);
      } else if (type == Long.class || type == Long.TYPE) {
         return (T) Long.valueOf(value);
      } else if (type == Short.class || type == Short.TYPE) {
         return (T) Short.valueOf(value);
      } else if (type == Float.class || type == Float.TYPE) {
         return (T) Float.valueOf(value);
      } else if (type == Double.class || type == Double.TYPE) {
         return (T) Double.valueOf(value);
      } else if (type == Byte.class || type == Byte.TYPE) {
         return (T) Byte.valueOf(value);
      } else if (type == Character.class || type == Character.TYPE) {
         return (T) (Character) value.charAt(0);
      } else {
         return (T) value;
      }
   }
}
