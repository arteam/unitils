/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.annotation;

import org.unitils.easymock.util.*;

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
