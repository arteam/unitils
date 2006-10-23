/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.util;

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