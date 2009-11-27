package org.unitils.tapestry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TapestryService {

	/**
	 * The service type. If unspecified, the type of type of the field where
	 * the service should be injected is used.
	 */
	Class<?> type() default Constants.UseFieldTypeAsServiceType.class;

	/**
	 * The service id. If unspecified only the type information is used
	 * to locate the service.
	 */
	String id() default Constants.NO_SERVICE_ID;
	
}
