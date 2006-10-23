/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.util;

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