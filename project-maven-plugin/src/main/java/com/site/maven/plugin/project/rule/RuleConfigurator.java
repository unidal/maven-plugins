package com.site.maven.plugin.project.rule;

public class RuleConfigurator {
   public void configure(RuleRegistry registry) {
      for (ClassRule rule : ClassRule.values()) {
         registry.register(rule);
      }

      for (FieldRule rule : FieldRule.values()) {
         registry.register(rule);
      }

      for (ConstructorRule rule : ConstructorRule.values()) {
         registry.register(rule);
      }

      for (MethodRule rule : MethodRule.values()) {
         registry.register(rule);
      }
   }
}
