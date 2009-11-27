package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TapestrySymbol {

	/**
	 * The symbol name to be injected into the annotated field.
	 */
	String value();

	/**
	 * If the symbol is not found, null is injected.
	 */
	boolean optional() default false;

}
