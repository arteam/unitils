/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.inject.util;

/**
 * The type of accessing properties: by field or by setter. If default is chosen, the property access type is defined
 * in the Unitils configuration file.
 */
public enum PropertyAccessType {

    FIELD, SETTER, DEFAULT;

}
