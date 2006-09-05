/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import be.ordina.unitils.testing.util.ReflectionComparator.Difference;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;


/**
 * A class for asserting that 2 objects are equal by comparing all fields of the objects using reflection.
 * //todo javadoc
 */
public class ReflectionAssert {


    //todo javadoc
    private ReflectionComparator reflectionComparator;


    //todo javadoc
    public ReflectionAssert(ReflectionComparatorModes... modes) {
        this.reflectionComparator = new ReflectionComparator(modes);
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
    public void assertEquals(Object expected, Object actual) throws AssertionFailedError {

        assertEquals(null, expected, actual);
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
    public void assertEquals(String message, Object expected, Object actual) throws AssertionFailedError {

        Difference difference = reflectionComparator.getDifference(expected, actual);
        if (difference != null) {
            Assert.fail(formatMessage(message, difference));
        }
    }


    // Property equals

    // todo javadoc
    public void assertPropertyEquals(String propertyName, Object expectedPropertyValue, Object actualObject) {

        assertPropertyEquals(null, propertyName, expectedPropertyValue, actualObject);
    }

    //todo javadoc
    public void assertPropertyEquals(String message, String propertyName, Object expectedPropertyValue, Object actualObject) {
        try {
            Object propertyValue = PropertyUtils.getProperty(actualObject, propertyName);
            assertEquals(formatMessage(message, "Incorrect value for property: " + propertyName), expectedPropertyValue, propertyValue);

        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);

        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);
        }
    }

    //todo javadoc
    public void assertPropertyEquals(String propertyName, Collection expectedPropertyValues, Collection actualObjects) {

        assertPropertyEquals(null, propertyName, expectedPropertyValues, actualObjects);
    }

    //todo javadoc
    public void assertPropertyEquals(String message, String propertyName, Collection expectedPropertyValues, Collection actualObjects) {

        Collection actualPropertyValues = CollectionUtils.collect(actualObjects, new BeanToPropertyValueTransformer(propertyName));
        assertEquals(message, expectedPropertyValues, actualPropertyValues);
    }


    /**
     * Formats the exception message.
     *
     * @param difference the difference
     * @return a message containing these 3 items
     */
    private String formatMessage(String suppliedMessage, Difference difference) {

        String result = formatMessage(suppliedMessage, difference.getMessage());

        //todo implement
        String fieldString = difference.getFieldStackAsString();
        if (StringUtils.isEmpty(fieldString)) {
            fieldString = "<top-level>";
        }

        result += "\nField: <" + fieldString;
        result += "> expected: <" + difference.getLeftValue();
        result += "> but was: <" + difference.getRightValue() + ">";
        return result;
    }


    // todo javadoc
    private String formatMessage(String suppliedMessage, String specificMessage) {

        if (StringUtils.isEmpty(suppliedMessage)) {
            return specificMessage;
        }
        return suppliedMessage + "\n" + specificMessage;
    }

}