package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annocation are run once before the tapestry registry is created. 
 * 
 * The methods must be static if injection into static fields is required by your test, otherwise
 * the methods may be non static.
 * 
 * The methods are run in hierarchical class order. Methods defined in base classes are executed
 * before methods in derived classes. If non static methods are overwritten only the overwritten
 * method is executed once.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RunBeforeTapestryRegistryIsCreated {
}
