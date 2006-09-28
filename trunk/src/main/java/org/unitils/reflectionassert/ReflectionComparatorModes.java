package org.unitils.reflectionassert;

/**
 * Modes defining how to compare two values.
 * No mode means strict comparison. Each of the modes specify some form of leniency when
 * comparing the values: <ul>
 * <li>ignore defaults: compare only fields (and inner values) that have a non default value (eg null) as exepected value</li>
 * <li>lenient dates: do not compare actual date values, just that they both have a value or not</li>
 * <li>lenient order: order is not important when comparing collections or arrays</li>
 * </ul>
 * The modes can be combined if needed.
 *
 * @see org.unitils.reflectionassert.ReflectionComparator
 */
public enum ReflectionComparatorModes {

    /**
     * Ignore fields that do not have a default value for the left-hand (expected) side
     */
    IGNORE_DEFAULTS,

    /**
     * Do not compare the actual time/date value, just that both left-hand (expected) and right-hand side are null or not null.
     */
    LENIENT_DATES,

    /**
     * Do not compare the order of collections and arrays. Only check that all values of the left-hand (expected) side
     * collection or array are also contained in the right-hand (actual) side and vice versa.
     */
    LENIENT_ORDER;

}
