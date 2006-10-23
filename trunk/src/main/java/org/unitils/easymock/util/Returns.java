/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.util;

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
