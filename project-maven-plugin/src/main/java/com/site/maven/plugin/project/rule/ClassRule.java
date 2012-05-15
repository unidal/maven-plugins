package com.site.maven.plugin.project.rule;

import java.lang.reflect.Modifier;

public enum ClassRule implements IRule<Class<?>> {
   CLASS_REMOVED {
      @Override
      public int compare(Class<?> source, Class<?> target) {
         int m2 = target.getModifiers();

         if (source == null) {
            if (!target.isMemberClass() && Modifier.isPublic(m2)) {
               return RuleFailure.CLASS_REMOVED.getErrorCode();
            } else if (target.isMemberClass() && Modifier.isStatic(m2) && (Modifier.isPublic(m2) || Modifier.isProtected(m2))) {
               return RuleFailure.CLASS_REMOVED.getErrorCode();
            }
         }

         return 0;
      }
   },

   INVISIBLE_MEMBER_CLASS_SKIPPED {
      @Override
      public int compare(Class<?> source, Class<?> target) {
         int m2 = target.getModifiers();

         if (target.isMemberClass()) {
            if (!Modifier.isStatic(m2)) {
               return RuleExemption.INVISIBLE_MEMBER_CLASS_SKIPPED.getReasonCode();
            } else if (!Modifier.isPublic(m2) && !Modifier.isProtected(m2)) {
               return RuleExemption.INVISIBLE_MEMBER_CLASS_SKIPPED.getReasonCode();
            }
         }

         return 0;
      }
   },

   CLASS_VISIBILITY_DEGRADED {
      @Override
      public int compare(Class<?> source, Class<?> target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (!target.isMemberClass()) {
            if (Modifier.isPublic(m2) && (!Modifier.isPublic(m1))) {
               return RuleFailure.CLASS_VISIBILITY_DEGRADED.getErrorCode();
            } else if (Modifier.isProtected(m2) && (!Modifier.isProtected(m1) && !Modifier.isPublic(m1))) {
               return RuleFailure.CLASS_VISIBILITY_DEGRADED.getErrorCode();
            }
         } else if (Modifier.isStatic(m2)) {
            if (Modifier.isPublic(m2) && (!Modifier.isPublic(m1) || !Modifier.isStatic(m1))) {
               return RuleFailure.CLASS_VISIBILITY_DEGRADED.getErrorCode();
            } else if (Modifier.isProtected(m2) && (!Modifier.isProtected(m1) && !Modifier.isPublic(m1) || !Modifier.isStatic(m1))) {
               return RuleFailure.CLASS_VISIBILITY_DEGRADED.getErrorCode();
            }
         }

         return 0;
      }
   },

   CLASS_EXTENSIBILITY_DEGRADED {
      @Override
      public int compare(Class<?> source, Class<?> target) {
         int m2 = target.getModifiers();
         int m1 = source.getModifiers();

         if (!target.isMemberClass()) {
            if (!Modifier.isFinal(m2) && Modifier.isFinal(m1)) {
               return RuleFailure.CLASS_EXTENSIBILITY_DEGRADED.getErrorCode();
            }
         } else if (Modifier.isStatic(m2)) {
            if (!Modifier.isFinal(m2) && Modifier.isFinal(m1)) {
               return RuleFailure.CLASS_EXTENSIBILITY_DEGRADED.getErrorCode();
            }
         }

         return 0;
      }
   },

   CLASS_TYPE_CHANGED {
      @Override
      public int compare(Class<?> source, Class<?> target) {
         int m2 = target.getModifiers();

         if (!target.isMemberClass()) {
            if (target.isInterface() != source.isInterface()) {
               return RuleFailure.CLASS_TYPE_CHANGED.getErrorCode();
            }
         } else if (Modifier.isStatic(m2)) {
            if (target.isInterface() != source.isInterface()) {
               return RuleFailure.CLASS_TYPE_CHANGED.getErrorCode();
            }
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
      return RuleType.CLASS;
   }
}
