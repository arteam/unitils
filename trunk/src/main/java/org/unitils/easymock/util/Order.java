/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.easymock.util;

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

