/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.util;


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
