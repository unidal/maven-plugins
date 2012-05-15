package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleEngine {
   private RuleRegistry m_registry = new RuleRegistry();

   protected <T extends Member> String asString(T member) {
      if (member instanceof Field) {
         return member.getName();
      } else if (member instanceof Method) {
         return member.getName() + ":" + Arrays.asList(((Method) member).getGenericParameterTypes());
      } else if (member instanceof Constructor) {
         return member.getName() + ":" + Arrays.asList(((Constructor<?>) member).getGenericParameterTypes());
      } else {
         throw new UnsupportedOperationException();
      }
   }

   protected <T extends Member> Map<String, List<T>> buildMemberMap(T[] members) {
      Map<String, List<T>> map = new HashMap<String, List<T>>();

      for (T member : members) {
         List<T> list = map.get(member.getName());

         if (list == null) {
            list = new ArrayList<T>(3);
            map.put(member.getName(), list);
         }

         list.add(member);
      }

      return map;
   }

   public boolean execute(Class<?> source, Class<?> target, IRuleErrorHandler errorHandler, IRuleExemptionHandler exemptionHandler) {
      if (!processClass(source, target, errorHandler, exemptionHandler)) {
         return false;
      }

      if (source != null) {
         Map<String, List<Field>> fieldMap = buildMemberMap(source.getDeclaredFields());

         for (Field targetField : target.getDeclaredFields()) {
            Field sourceField = findMember(fieldMap, targetField);

            if (!processField(sourceField, targetField, errorHandler, exemptionHandler)) {
               return false;
            }
         }

         Map<String, List<Method>> methodMap = buildMemberMap(source.getDeclaredMethods());

         for (Method targetMethod : target.getDeclaredMethods()) {
            Method sourceMethod = findMember(methodMap, targetMethod);

            if (!processMethod(sourceMethod, targetMethod, errorHandler, exemptionHandler)) {
               return false;
            }
         }

         Map<String, List<Constructor<?>>> constructorMap = buildMemberMap(source.getDeclaredConstructors());

         for (Constructor<?> targetConstructor : target.getDeclaredConstructors()) {
            Constructor<?> sourceConstructor = findMember(constructorMap, targetConstructor);

            if (!processConstructor(sourceConstructor, targetConstructor, errorHandler, exemptionHandler)) {
               return false;
            }
         }
      }

      return true;
   }

   protected <T extends Member> T findMember(Map<String, List<T>> map, T target) {
      T source = null;
      List<T> list = map.get(target.getName());

      if (list != null) {
         int len = list.size();

         for (int i = 0; i < len; i++) {
            T member = list.get(i);

            if (asString(member).equals(asString(target))) { // they come from different classloaders
               list.remove(i);
               source = member;
               break;
            }
         }
      }

      return source;
   }

   public RuleRegistry getRegistry() {
      return m_registry;
   }

   protected boolean processClass(Class<?> source, Class<?> target, IRuleErrorHandler errorHandler,
         IRuleExemptionHandler exemptionHandler) {
      Map<String, IRule<AnnotatedElement>> rules = m_registry.getClassRules();

      for (IRule<AnnotatedElement> rule : rules.values()) {
         int value = rule.compare(source, target);

         if (value < 0) { // error code
            RuleFailure failure = RuleFailure.getByErrorCode(value);

            errorHandler.onError(source, target, failure);
            return false;
         } else if (value > 0) { // reason code
            RuleExemption exemption = RuleExemption.getByReasonCode(value);

            exemptionHandler.onExemption(source, target, exemption);
            return false;
         }
      }

      return true;
   }

   protected boolean processConstructor(Constructor<?> source, Constructor<?> target, IRuleErrorHandler errorHandler,
         IRuleExemptionHandler exemptionHandler) {
      Map<String, IRule<AnnotatedElement>> rules = m_registry.getConstructorRules();

      for (IRule<AnnotatedElement> rule : rules.values()) {
         int value = rule.compare(source, target);

         if (value < 0) { // error code
            RuleFailure failure = RuleFailure.getByErrorCode(value);

            errorHandler.onError(source, target, failure);
            return false;
         } else if (value > 0) { // reason code
            RuleExemption exemption = RuleExemption.getByReasonCode(value);

            exemptionHandler.onExemption(source, target, exemption);
            return false;
         }
      }

      return true;
   }

   protected boolean processField(Field source, Field target, IRuleErrorHandler errorHandler, IRuleExemptionHandler exemptionHandler) {
      Map<String, IRule<AnnotatedElement>> rules = m_registry.getFieldRules();

      for (IRule<AnnotatedElement> rule : rules.values()) {
         int value = rule.compare(source, target);

         if (value < 0) { // error code
            RuleFailure failure = RuleFailure.getByErrorCode(value);

            errorHandler.onError(source, target, failure);
            return false;
         } else if (value > 0) { // reason code
            RuleExemption exemption = RuleExemption.getByReasonCode(value);

            exemptionHandler.onExemption(source, target, exemption);
            return false;
         }
      }

      return true;
   }

   protected boolean processMethod(Method source, Method target, IRuleErrorHandler errorHandler,
         IRuleExemptionHandler exemptionHandler) {
      Map<String, IRule<AnnotatedElement>> rules = m_registry.getMethodRules();

      for (IRule<AnnotatedElement> rule : rules.values()) {
         int value = rule.compare(source, target);

         if (value < 0) { // error code
            RuleFailure failure = RuleFailure.getByErrorCode(value);

            errorHandler.onError(source, target, failure);
            return false;
         } else if (value > 0) { // reason code
            RuleExemption exemption = RuleExemption.getByReasonCode(value);

            exemptionHandler.onExemption(source, target, exemption);
            return false;
         }
      }

      return true;
   }
}