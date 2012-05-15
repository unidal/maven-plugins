package com.site.maven.plugin.project.rule;

import java.lang.reflect.AnnotatedElement;
import java.util.Comparator;

public interface IRule<T extends AnnotatedElement> extends Comparator<T> {
   public String getName();

   public RuleType getType();

   /**
    * Compare the two annotated elements(Class, Field, Constructor and Method) to determine the back compatibility.
    * 
    * @param source
    *            current annotated element to compare
    * @param target
    *            the baseline annotated element to compare
    * @return an int < 0 is error code if source is NOT compatible with target, 0 means they are compatible or skipped,
    *         and > 0 is reason code when the comparison is stopped
    */
   public int compare(T source, T target);

}
