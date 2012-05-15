package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public enum RuleFailure {
   CLASS_REMOVED(-1, "Class removed: {0}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         return new Object[] { target };
      }
   },

   CLASS_VISIBILITY_DEGRADED(-2, "Class visibility degraded: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         return new Object[] { source, target };
      }
   },

   CLASS_EXTENSIBILITY_DEGRADED(-3, "Class extensibility degraded: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         return new Object[] { source, target };
      }
   },

   CLASS_TYPE_CHANGED(-4, "Class type changed: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         return new Object[] { source, target };
      }
   },

   FIELD_REMOVED(-11, "Field removed: {0}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Field m2 = (Field) target;

         return new Object[] { m2.toGenericString() };
      }
   },

   FIELD_VISIBILITY_DEGRADED(-12, "Field visibility degraded: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Field m1 = (Field) source;
         Field m2 = (Field) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   },

   FIELD_MEMBERSHIP_CHANGED(-13, "Field membership changed: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Field m1 = (Field) source;
         Field m2 = (Field) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   },

   CONSTRUCTOR_REMOVED(-21, "Constructor removed: {0}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Constructor<?> m2 = (Constructor<?>) target;

         return new Object[] { m2.toGenericString() };
      }
   },

   CONSTRUCTOR_VISIBILITY_DEGRADED(-22, "Constructor visibility degraded: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Constructor<?> m1 = (Constructor<?>) source;
         Constructor<?> m2 = (Constructor<?>) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   },

   CONSTRUCTOR_MEMBERSHIP_CHANGED(-23, "Constructor membership changed: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Constructor<?> m1 = (Constructor<?>) source;
         Constructor<?> m2 = (Constructor<?>) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   },

   METHOD_REMOVED(-31, "Method removed: {0}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Method m2 = (Method) target;

         return new Object[] { m2.toGenericString() };
      }
   },

   METHOD_VISIBILITY_DEGRADED(-32, "Method visibility degraded: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Method m1 = (Method) source;
         Method m2 = (Method) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   },

   METHOD_MEMBERSHIP_CHANGED(-33, "Method membership changed: {0}, baseline: {1}") {
      @Override
      protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
         Method m1 = (Method) source;
         Method m2 = (Method) target;

         return new Object[] { m1.toGenericString(), m2.toGenericString() };
      }
   };

   private static final Object[] EMPTY = new Object[0];

   private static Map<Integer, RuleFailure> MAP;

   private int m_errorCode;

   private MessageFormat m_messageFormat;

   static {
      MAP = new HashMap<Integer, RuleFailure>();

      for (RuleFailure error : values()) {
         MAP.put(error.getErrorCode(), error);
      }
   }

   private RuleFailure(int errorCode, String message) {
      m_errorCode = errorCode;
      m_messageFormat = new MessageFormat(message);
   }

   public static RuleFailure getByErrorCode(int errorCode) {
      return MAP.get(errorCode);
   }

   protected Object[] buildArguments(AnnotatedElement source, AnnotatedElement target) {
      return EMPTY;
   }

   public int getErrorCode() {
      return m_errorCode;
   }

   public String getMessage(AnnotatedElement source, AnnotatedElement target) {
      return m_messageFormat.format(buildArguments(source, target));
   }

   @Override
   public String toString() {
      return String.format("RuleError(%s, %s)", m_errorCode, m_messageFormat.toPattern());
   }
}
