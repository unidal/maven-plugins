package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public enum RuleExemption {
   INVISIBLE_MEMBER_CLASS_SKIPPED(1, "Invisible member class({0}) skipped") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Class<?> clazz = (Class<?>) target;

         return new Object[] { clazz.getName() };
      }

      @Override
      public String getMessage(AnnotatedElement source, AnnotatedElement target) {
         // No message shown
         return null;
      }
   },

   INVISIBLE_FIELD_SKIPPED(11, "Invisible field({0}) skipped") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Field field = (Field) target;

         return new Object[] { field.getName() };
      }

      @Override
      public String getMessage(AnnotatedElement source, AnnotatedElement target) {
         // No message shown
         return null;
      }
   },

   INVISIBLE_CONSTRUCTOR_SKIPPED(21, "Invisible constructor({0}) skipped") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Constructor<?> constructor = (Constructor<?>) target;

         return new Object[] { constructor.getName() };
      }

      @Override
      public String getMessage(AnnotatedElement source, AnnotatedElement target) {
         // No message shown
         return null;
      }
   },

   INVISIBLE_METHOD_SKIPPED(31, "Invisible method({0}) skipped") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Method method = (Method) target;

         return new Object[] { method.getName() };
      }

      @Override
      public String getMessage(AnnotatedElement source, AnnotatedElement target) {
         // No message shown
         return null;
      }
   };

   private static final Object[] EMPTY = new Object[0];

   private static Map<Integer, RuleExemption> MAP;

   private int m_reasonCode;

   private MessageFormat m_messageFormat;

   static {
      MAP = new HashMap<Integer, RuleExemption>();

      for (RuleExemption exemption : values()) {
         MAP.put(exemption.getReasonCode(), exemption);
      }
   }

   private RuleExemption(int errorCode, String message) {
      m_reasonCode = errorCode;
      m_messageFormat = new MessageFormat(message);
   }

   public static RuleExemption getByReasonCode(int reasonCode) {
      return MAP.get(reasonCode);
   }

   protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
      return EMPTY;
   }

   public String getMessage(AnnotatedElement source, AnnotatedElement target) {
      return m_messageFormat.format(buildArguments(source, target));
   }

   public int getReasonCode() {
      return m_reasonCode;
   }

   @Override
   public String toString() {
      return String.format("RuleError(%s, %s)", m_reasonCode, m_messageFormat.toPattern());
   }
}
