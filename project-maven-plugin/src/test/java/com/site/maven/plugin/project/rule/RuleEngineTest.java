package com.site.maven.plugin.project.rule;

import static com.site.maven.plugin.project.rule.RuleExemption.INVISIBLE_MEMBER_CLASS_SKIPPED;
import static com.site.maven.plugin.project.rule.RuleFailure.CLASS_EXTENSIBILITY_DEGRADED;
import static com.site.maven.plugin.project.rule.RuleFailure.CLASS_REMOVED;
import static com.site.maven.plugin.project.rule.RuleFailure.CLASS_TYPE_CHANGED;
import static com.site.maven.plugin.project.rule.RuleFailure.CLASS_VISIBILITY_DEGRADED;
import static com.site.maven.plugin.project.rule.RuleFailure.FIELD_MEMBERSHIP_CHANGED;
import static com.site.maven.plugin.project.rule.RuleFailure.FIELD_REMOVED;
import static com.site.maven.plugin.project.rule.RuleFailure.FIELD_VISIBILITY_DEGRADED;
import static com.site.maven.plugin.project.rule.RuleFailure.METHOD_MEMBERSHIP_CHANGED;
import static com.site.maven.plugin.project.rule.RuleFailure.METHOD_REMOVED;
import static com.site.maven.plugin.project.rule.RuleFailure.METHOD_VISIBILITY_DEGRADED;

import java.lang.reflect.AnnotatedElement;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class RuleEngineTest {
   private RuleEngine m_engine;

   private boolean m_handled;

   @Before
   public void before() {
      m_engine = new RuleEngine();

      new RuleConfigurator().configure(m_engine.getRegistry());
   }

   protected void checkClass(final Class<?> source, final Class<?> target, final int expectedCode, final String expectedMessage) {
      m_handled = false;

      IRuleErrorHandler errorHandler = new IRuleErrorHandler() {
         @Override
         public void onError(AnnotatedElement source, AnnotatedElement target, RuleFailure failure) {
            m_handled = true;
            Assert.assertEquals(expectedCode, failure.getErrorCode());
            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, failure.getMessage(source, target));
            }
         }
      };

      IRuleExemptionHandler exemptionHandler = new IRuleExemptionHandler() {
         @Override
         public void onExemption(AnnotatedElement source, AnnotatedElement target, RuleExemption exemption) {
            m_handled = true;
            Assert.assertEquals(expectedCode, exemption.getReasonCode());

            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, exemption.getMessage(source, target));
            }
         }
      };

      m_engine.execute(source, target, errorHandler, exemptionHandler);

      if (!m_handled) {
         if (expectedCode < 0) {
            Assert.fail("Rule failure expected: " + expectedMessage);
         } else if (expectedCode > 0) {
            Assert.fail("Rule exemption expected: " + expectedMessage);
         }
      }
   }

   protected void checkClass(final Class<?> source, final Class<?> target, final RuleExemption exemption,
         final String expectedMessage) {
      checkClass(source, target, exemption.getReasonCode(), expectedMessage);
   }

   protected void checkClass(final Class<?> source, final Class<?> target, final RuleFailure failure, final String expectedMessage) {
      checkClass(source, target, failure.getErrorCode(), expectedMessage);
   }

   protected void checkMethod(final Class<?> source, final Class<?> target, final int expectedCode, final String expectedMessage) {
      m_handled = false;

      IRuleErrorHandler errorHandler = new IRuleErrorHandler() {
         @Override
         public void onError(AnnotatedElement source, AnnotatedElement target, RuleFailure failure) {
            m_handled = true;
            Assert.assertEquals(expectedCode, failure.getErrorCode());
            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, failure.getMessage(source, target));
            }
         }
      };

      IRuleExemptionHandler exemptionHandler = new IRuleExemptionHandler() {
         @Override
         public void onExemption(AnnotatedElement source, AnnotatedElement target, RuleExemption exemption) {
            m_handled = true;
            Assert.assertEquals(expectedCode, exemption.getReasonCode());

            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, exemption.getMessage(source, target));
            }
         }
      };

      m_engine.execute(source, target, errorHandler, exemptionHandler);

      if (!m_handled) {
         if (expectedCode < 0) {
            Assert.fail(String.format("Rule failure expected: %s(%s)!", expectedMessage, expectedCode));
         } else if (expectedCode > 0) {
            Assert.fail(String.format("Rule exemption expected: %s(%s)!", expectedMessage, expectedCode));
         }
      }
   }

   protected void checkMethod(final Class<?> source, final Class<?> target, final RuleExemption exemption,
         final String expectedMessage) {
      checkMethod(source, target, exemption.getReasonCode(), expectedMessage);
   }

   protected void checkMethod(final Class<?> source, final Class<?> target, final RuleFailure failure, final String expectedMessage) {
      checkMethod(source, target, failure.getErrorCode(), expectedMessage);
   }

   protected void checkField(final Class<?> source, final Class<?> target, final int expectedCode, final String expectedMessage) {
      m_handled = false;

      IRuleErrorHandler errorHandler = new IRuleErrorHandler() {
         @Override
         public void onError(AnnotatedElement source, AnnotatedElement target, RuleFailure failure) {
            m_handled = true;
            Assert.assertEquals(expectedCode, failure.getErrorCode());
            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, failure.getMessage(source, target));
            }
         }
      };

      IRuleExemptionHandler exemptionHandler = new IRuleExemptionHandler() {
         @Override
         public void onExemption(AnnotatedElement source, AnnotatedElement target, RuleExemption exemption) {
            m_handled = true;
            Assert.assertEquals(expectedCode, exemption.getReasonCode());

            if (expectedMessage != null) {
               Assert.assertEquals(expectedMessage, exemption.getMessage(source, target));
            }
         }
      };

      m_engine.execute(source, target, errorHandler, exemptionHandler);

      if (!m_handled) {
         if (expectedCode < 0) {
            Assert.fail(String.format("Rule failure expected: %s(%s)!", expectedMessage, expectedCode));
         } else if (expectedCode > 0) {
            Assert.fail(String.format("Rule exemption expected: %s(%s)!", expectedMessage, expectedCode));
         }
      }
   }

   protected void checkField(final Class<?> source, final Class<?> target, final RuleExemption exemption,
         final String expectedMessage) {
      checkField(source, target, exemption.getReasonCode(), expectedMessage);
   }

   protected void checkField(final Class<?> source, final Class<?> target, final RuleFailure failure, final String expectedMessage) {
      checkField(source, target, failure.getErrorCode(), expectedMessage);
   }

   @Test
   public void testClassBasic() {
      checkClass(C1.class, C1.class, 0, null);
      checkClass(C2.class, C2.class, 0, null);

      checkClass(null, C3.class, INVISIBLE_MEMBER_CLASS_SKIPPED, null);
      checkClass(null, C4.class, INVISIBLE_MEMBER_CLASS_SKIPPED, null);
      checkClass(null, C5.class, INVISIBLE_MEMBER_CLASS_SKIPPED, null);
      checkClass(null, C6.class, INVISIBLE_MEMBER_CLASS_SKIPPED, null);
      checkClass(null, C7.class, INVISIBLE_MEMBER_CLASS_SKIPPED, null);

      checkClass(null, C1.class, CLASS_REMOVED, null);
      checkClass(null, C2.class, CLASS_REMOVED, null);

      checkClass(C2.class, C1.class, CLASS_VISIBILITY_DEGRADED, null);
      checkClass(C3.class, C1.class, CLASS_VISIBILITY_DEGRADED, null);
      checkClass(C3.class, C2.class, CLASS_VISIBILITY_DEGRADED, null);

      checkClass(C8.class, C1.class, CLASS_TYPE_CHANGED, null);

      checkClass(C9.class, C1.class, CLASS_EXTENSIBILITY_DEGRADED, null);

      checkClass(C4.class, C1.class, CLASS_VISIBILITY_DEGRADED, null);
      checkClass(C5.class, C2.class, CLASS_VISIBILITY_DEGRADED, null);
   }

   @Test
   public void testClassAdvanced() {
      // TODO check implemented interfaces
      // TODO check generic definition
   }

   @Test
   public void testMethodBasic() {
      checkMethod(M21.class, M1.class, METHOD_REMOVED, null);
      checkMethod(M22.class, M1.class, METHOD_REMOVED, null);

      checkMethod(M3.class, M1.class, METHOD_VISIBILITY_DEGRADED, null);

      checkMethod(M4.class, M1.class, METHOD_MEMBERSHIP_CHANGED, null);
   }

   @Test
   public void testFieldBasic() {
      checkField(F21.class, F1.class, FIELD_REMOVED, null);

      checkField(F3.class, F1.class, FIELD_VISIBILITY_DEGRADED, null);

      checkField(F4.class, F1.class, FIELD_MEMBERSHIP_CHANGED, null);
   }

   public static class M1 {
      public void a() {
      }
   }

   public static class M21 {
      public void a1() {
      }
   }

   public static class M22 {
      public void a(boolean p1) {
      }
   }

   public static class M3 {
      protected void a() {
      }
   }

   public static class M4 {
      public static void a() {
      }
   }

   public static class F1 {
      public int a;
   }

   public static class F21 {
      public int a1;
   }

   public static class F22 {
      public boolean a;
   }

   public static class F3 {
      protected int a;
   }

   public static class F4 {
      public static int a;
   }

   public static class C1 {
   }

   protected static class C2 {
   }

   private static class C3 {
   }

   public class C4 {
   }

   protected class C5 {
   }

   class C6 {
   }

   private class C7 {
   }

   public static interface C8 {
   }

   public final static class C9 {
   }
}
