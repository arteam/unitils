package org.unitils.inject.util;

/**
 * The type of accessing properties: by field or by setter. If default is chosen, the property access type is defined
 * in the Unitils configuration file.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public enum Restore {

    /**
     * OLD_VALUE, NO_RESTORE or NULL_VALUE as defined by the default value in the configuration.
     */
    DEFAULT,

    /**
     * Reset the injected field back to its original value
     */
    OLD_VALUE,

    /**
     * Leave the injected field
     */
    NO_RESTORE,

    /**
     * Set the injected field to the null or 0 value
     */
    NULL_OR_0_VALUE

}
