package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If put on a test class, then one registry is created per test - class.
 * 
 * If put on a test method, then a dedicated registry is created for this test method. Note that
 * in this case any static fields with injected services stay untouched!
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TapestryRegistry {

	/**
	 * Array of Tapestry modules that should be loaded for a test case.
	 */
	Class<?>[] value();

}
