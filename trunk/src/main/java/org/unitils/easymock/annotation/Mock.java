package org.unitils.easymock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * todo javadoc
 * <p/>
 * Annotation indicating that a mock object should be injected into the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {

    public enum Order {

        None,
        Strict,
        Default
    }

    public enum Returns {

        Nice,
        Strict,
        Default
    }

    public enum Arguments {

        None,
        Lenient,
        Default
    }


    public Order order() default Order.Default;

    public Returns returns() default Returns.Default;

    public Arguments arguments() default Arguments.Default;

}
