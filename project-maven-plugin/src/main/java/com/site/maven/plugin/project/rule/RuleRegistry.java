package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.Map;

public class RuleRegistry {
   private Map<String, IRule<AnnotatedElement>> m_classRules = new LinkedHashMap<String, IRule<AnnotatedElement>>();

   private Map<String, IRule<AnnotatedElement>> m_fieldRules = new LinkedHashMap<String, IRule<AnnotatedElement>>();

   private Map<String, IRule<AnnotatedElement>> m_constructorRules = new LinkedHashMap<String, IRule<AnnotatedElement>>();

   private Map<String, IRule<AnnotatedElement>> m_methodRules = new LinkedHashMap<String, IRule<AnnotatedElement>>();

   public Map<String, IRule<AnnotatedElement>> getClassRules() {
      return m_classRules;
   }

   public Map<String, IRule<AnnotatedElement>> getConstructorRules() {
      return m_constructorRules;
   }

   public Map<String, IRule<AnnotatedElement>> getFieldRules() {
      return m_fieldRules;
   }

   public Map<String, IRule<AnnotatedElement>> getMethodRules() {
      return m_methodRules;
   }

   @SuppressWarnings("unchecked")
   public void register(IRule<? extends AnnotatedElement> rule) {
      switch (rule.getType()) {
      case CLASS:
         m_classRules.put(rule.getName(), (IRule<AnnotatedElement>) rule);
         break;
      case FIELD:
         m_fieldRules.put(rule.getName(), (IRule<AnnotatedElement>) rule);
         break;
      case CONSTRUCTOR:
         m_constructorRules.put(rule.getName(), (IRule<AnnotatedElement>) rule);
         break;
      case METHOD:
         m_methodRules.put(rule.getName(), (IRule<AnnotatedElement>) rule);
         break;
      default:
         throw new UnsupportedOperationException("Unknown rule type: " + rule.getType());
      }
   }
}
