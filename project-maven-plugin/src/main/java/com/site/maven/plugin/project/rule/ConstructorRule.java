package com.site.maven.plugin.project.rule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public enum ConstructorRule implements IRule<Constructor<?>> {
   CONSTRUCTOR_REMOVED {
      @Override
      public int compare(Constructor<?> source, Constructor<?> target) {
         int m2 = target.getModifiers();

         if (source == null) {
            if (Modifier.isPublic(m2) || Modifier.isProtected(m2)) {
               return RuleFailure.CONSTRUCTOR_REMOVED.getErrorCode();
            }
         }

         return 0;
      }
   },

   INVISIBLE_MEMBER_CONSTRUCTOR_SKIPPED {
      @Override
      public int compare(Constructor<?> source, Constructor<?> target) {
         int m2 = target.getModifiers();

         if (!Modifier.isPublic(m2) && !Modifier.isProtected(m2)) {
            return RuleExemption.INVISIBLE_CONSTRUCTOR_SKIPPED.getReasonCode();
         }

         return 0;
      }
   },

   CONSTRUCTOR_MEMBERSHIP_CHANGED {
      @Override
      public int compare(Constructor<?> source, Constructor<?> target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (Modifier.isStatic(m2) != Modifier.isStatic(m1)) {
            return RuleFailure.CONSTRUCTOR_MEMBERSHIP_CHANGED.getErrorCode();
         }

         return 0;
      }
   },

   CONSTRUCTOR_VISIBILITY_DEGRADED {
      @Override
      public int compare(Constructor<?> source, Constructor<?> target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (Modifier.isPublic(m2) && (!Modifier.isPublic(m1))) {
            return RuleFailure.CONSTRUCTOR_VISIBILITY_DEGRADED.getErrorCode();
         } else if (Modifier.isProtected(m2) && (!Modifier.isProtected(m1) && !Modifier.isPublic(m1))) {
            return RuleFailure.CONSTRUCTOR_VISIBILITY_DEGRADED.getErrorCode();
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
      return RuleType.CONSTRUCTOR;
   }
}
