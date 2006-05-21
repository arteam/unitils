/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.apache.commons.lang.StringUtils;


/**
 * A class for asserting that 2 objects are equals by comparing all fields of the objects using reflection.
 */
public class ReflectionAssert {

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     * Reflection is used to compare all fields of the given objects
     *
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertEquals(Object expected, Object actual) throws AssertionFailedError {
        assertEquals(null, actual, expected, false, false);
    }


    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     * Reflection is used to compare all fields of the given objects
     *
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertEquals(Object expected, Object actual, boolean ignoreDefaults, boolean lenientDates) throws AssertionFailedError {
        assertEquals(null, actual, expected, ignoreDefaults, lenientDates);
    }


    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     * Reflection is used to compare all fields of the given objects
     *
     * @param message  the message for the AssertionFailedError when not equals
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertEquals(String message, Object expected, Object actual) throws AssertionFailedError {
        assertEquals(message, actual, expected, false, false);
    }

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown with the given message.
     * Reflection is used to compare all fields of the given objects
     *
     * @param message  the message for the AssertionFailedError when not equals
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertEquals(String message, Object expected, Object actual, boolean ignoreDefaults, boolean lenientDates) throws AssertionFailedError {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(expected, actual, ignoreDefaults, lenientDates);
        if (!reflectionEquals.isEquals()) {
            Assert.fail(formatMessage(message, reflectionEquals));
        }
    }


    /**
     * Formats the exception message.
     *
     * @param message          the user message
     * @param reflectionEquals the difference
     * @return a message containing these 3 items
     */
    private static String formatMessage(String message, ReflectionEquals reflectionEquals) {
        String result = "";
        if (message != null) {
            result = message + " ";
        }

        String fieldString = reflectionEquals.getDifferenceFieldStackAsString();
        if (StringUtils.isEmpty(fieldString)) {
            fieldString = "<top-level>";
        }

        result += "field: " + fieldString;
        result += " expected: <" + reflectionEquals.getDifferenceLeftValue();
        result += "> but was: <" + reflectionEquals.getDifferenceRightValue() + ">";
        return result;
    }

}