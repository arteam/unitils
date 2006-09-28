package be.ordina.unitils.inject.annotation;

import be.ordina.unitils.inject.PropertyAccessType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Filip Neven
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    String target() default "";

    String property();

    PropertyAccessType propertyAccessType() default PropertyAccessType.DEFAULT;

}
