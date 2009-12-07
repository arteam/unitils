package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Static methods annotated with this annotation are run once before the tapestry
 * registry is created. This can be used e.g. to initialize any system properties
 * that to configure the used services for unit or integration testing. 
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RunBeforeTapestryRegistryIsCreated {
}
