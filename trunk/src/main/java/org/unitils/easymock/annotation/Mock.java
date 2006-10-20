package org.unitils.easymock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a mock object should be created and set in the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mock {


    /**
     * Possible values for checking the order of method invocation on the mock.
     */
    public enum InvocationOrder {


        /**
         * Defaults to the value of the org.unitils.easymock.annotation.Mock$InvocationOrder configuration setting.
         */
        DEFAULT,

        /**
         * No order checking of method invocations.
         */
        NONE,

        /**
         * Strict order checking of method invocations.
         */
        STRICT

    }

    /**
     * Possible values for default return values for non-void method invocations on the mock.
     */
    public enum Returns {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.Mock$Returns configuration setting.
         */
        DEFAULT,

        /**
         * Return default values (null, 0…) when no return type is specified.
         */
        NICE,

        /**
         * Throw an exception when no return type is specified
         */
        STRICT
    }


    /**
     * Determines whether the order of method calls on the mock object should be checked.
     *
     * @return the invocation order setting.
     */
    public InvocationOrder invocationOrder() default InvocationOrder.DEFAULT;


    /**
     * Determines what to do when no return value is recorded for a method.
     *
     * @return the returns setting.
     */
    public Returns returns() default Returns.DEFAULT;


}
