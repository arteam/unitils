package be.ordina.unitils.testing.mock.inject;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
