package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TapestryModule {

	/**
	 * Array of Tapestry modules that should be loaded for a test case.
	 */
	Class<?>[] value();

}
