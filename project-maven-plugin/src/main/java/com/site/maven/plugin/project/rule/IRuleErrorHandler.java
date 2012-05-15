package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;

public interface IRuleErrorHandler {
   public void onError(AnnotatedElement source, AnnotatedElement target, RuleFailure failure);
}
