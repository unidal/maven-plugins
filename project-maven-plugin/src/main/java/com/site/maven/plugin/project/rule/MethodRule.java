package com.site.maven.plugin.project.rule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public enum MethodRule implements IRule<Method> {
   METHOD_REMOVED {
      @Override
      public int compare(Method source, Method target) {
         int m2 = target.getModifiers();

         if (source == null) {
            if (Modifier.isPublic(m2) || Modifier.isProtected(m2)) {
               return RuleFailure.METHOD_REMOVED.getErrorCode();
            }
         }

         return 0;
      }
   },

   INVISIBLE_MEMBER_METHOD_SKIPPED {
      @Override
      public int compare(Method source, Method target) {
         int m2 = target.getModifiers();

         if (!Modifier.isPublic(m2) && !Modifier.isProtected(m2)) {
            return RuleExemption.INVISIBLE_METHOD_SKIPPED.getReasonCode();
         }

         return 0;
      }
   },

   METHOD_MEMBERSHIP_CHANGED {
      @Override
      public int compare(Method source, Method target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (Modifier.isStatic(m2) != Modifier.isStatic(m1)) {
            return RuleFailure.METHOD_MEMBERSHIP_CHANGED.getErrorCode();
         }

         return 0;
      }
   },

   METHOD_VISIBILITY_DEGRADED {
      @Override
      public int compare(Method source, Method target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (Modifier.isPublic(m2) && (!Modifier.isPublic(m1))) {
            return RuleFailure.METHOD_VISIBILITY_DEGRADED.getErrorCode();
         } else if (Modifier.isProtected(m2) && (!Modifier.isProtected(m1) && !Modifier.isPublic(m1))) {
            return RuleFailure.METHOD_VISIBILITY_DEGRADED.getErrorCode();
         }

         return 0;
      }
   };

   @Override
   public String getName() {
      return name();
   }

   @Override
   public RuleType getType() {
      return RuleType.METHOD;
   }
}
