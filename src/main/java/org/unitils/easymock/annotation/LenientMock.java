package org.unitils.easymock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a mock object should be created and injected into the annotated field.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LenientMock {


    /**
     * Possible values for checking the order of method invocation on the mock.
     */
    public enum Order {


        /**
         * Defaults to the value of the org.unitils.easymock.annotation.Mock$Order configuration setting.
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
     * todo javadoc
     * Possible values for default return values for non-void method invocations on the mock.
     */
    public enum Dates {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.Mock$Returns configuration setting.
         */
        DEFAULT,

        /**
         * Return default values (null, 0…) when no return type is specified.
         */
        LENIENT,

        /**
         * Throw an exception when no return type is specified
         */
        STRICT
    }


    public enum Arguments {

        /**
         * Defaults to the value of the org.unitils.easymock.annotation.Mock$Arguments configuration setting.
         */
        DEFAULT,


        /**
         * Lenient argument matchers are used for all arguments. No specific argument matchers can be specified.
         * <p/>
         * All arguments that have default values as expected values will not be checked. E.g. if a null value is recorded
         * as argument it will not be checked when the actual invocation occurs. The same applies for inner-fields of
         * object arguments that contain default java values.
         * <p/>
         * Actual date values of arguments and inner fields of arguments are ignored. It will only check whether both
         * dates are null or both dates are not null. The actual date and hour do not matter.
         * <p/>
         * The actual order of collections and arrays arguments and inner fields of arguments are ignored. It will
         * only check whether they both contain the same elements.
         */
        LENIENT,

        /**
         * Argument matchers have to be set explicitly. If (and only if) no matcher was specified, a default matcher will be
         * used that calls the equals() method.
         */
        STRICT
    }

    /**
     * Determines whether the order of method calls on the mock object should be checked.
     */
    public LenientMock.Order order() default LenientMock.Order.DEFAULT;

    /**
     * Determines what to do when no return value is recorded for a method.
     */
    public LenientMock.Dates dates() default LenientMock.Dates.DEFAULT;

    /**
     * Determines how arguments of expected and actual method invocations should be compared.
     */
    public LenientMock.Arguments arguments() default LenientMock.Arguments.DEFAULT;

}
