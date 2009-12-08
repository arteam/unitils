package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.tapestry5.ioc.Registry;

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
	
	/**
	 * Public method to create and initialize the Tapestry {@link Registry}.
	 * 
	 * The method must be static if this annotation is put on a class. If its put
	 * on a method, the method may be non sdtatic.
	 * 
	 * The following method signatures are applicable:
	 * <ul>
	 * <li>String, Class<?>[] modules</li>
	 * <li>Class<?>[] modules<li>
	 * </ul>
	 */
	String registryFactoryMethodName() default "";
	
	/**
	 * Value to be passed as first argument to the method 
	 * defined by {@link #registryFactoryMethodName()}.
	 */
	String registryFactoryMethodParameter() default "";
	
}
