package org.unitils.easymock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a lenient mock object (see {@link org.unitils.easymock.LenientMocksControl} should be created
 * and set intp the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LenientMock {


    /**
     * Possible values for checking the order of method invocation on the mock.
     */
    public enum InvocationOrder {


        /**
         * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$InvocationOrder configuration setting.
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
         * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Returns configuration setting.
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
     * Possible values for checking arguments with date values.
     */
    public enum Dates {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Dates configuration setting.
         */
        DEFAULT,

        /**
         * Actual date values of arguments and inner fields of arguments are ignored. It will only check whether both
         * dates are null or both dates are not null. The actual date and hour do not matter.
         */
        LENIENT,

        /**
         * Date values will also be compared.
         */
        STRICT
    }


    /**
     * Possible values for checking arguments with default values.
     */
    public enum Defaults {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Defaults configuration setting.
         */
        DEFAULT,


        /**
         * All arguments that have default values as expected values will not be checked. E.g. if a null value is recorded
         * as argument it will not be checked when the actual invocation occurs. The same applies for inner-fields of
         * object arguments that contain default java values.
         */
        IGNORE_DEFAULTS,

        /**
         * Arguments with default values will also be checked.
         */
        STRICT
    }


    public enum Order {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.LenientMock$Order configuration setting.
         */
        DEFAULT,


        /**
         * The actual order of collections and arrays arguments and inner fields of arguments are ignored. It will
         * only check whether they both contain the same elements.
         */
        LENIENT,

        /**
         * The order of collectios will be checked.
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


    /**
     * Determines whether the order of collection elements should be checked.
     *
     * @return the order setting.
     */
    public Order order() default Order.DEFAULT;


    /**
     * Determines whether the actual value of a date argument should be checked.
     *
     * @return the dates setting.
     */
    public Dates dates() default Dates.DEFAULT;


    /**
     * Determines whether default values of arguments should be checked.
     *
     * @return the default arguments setting.
     */
    public Defaults defaults() default Defaults.DEFAULT;

}
