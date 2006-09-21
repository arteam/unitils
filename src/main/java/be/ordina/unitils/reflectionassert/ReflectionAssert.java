/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.reflectionassert;

import be.ordina.unitils.reflectionassert.ReflectionComparator.Difference;
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
 * <p/>
 * The (combination of) comparator modes specify how strict the comparison must be:<ul>
 * <li>ignore defaults: compare only arguments (and inner values) that have a non default value (eg null) as exepected value</li>
 * <li>lenient dates: do not compare actual date values, just that they both have a value or not</li>
 * <li>lenient order: order is not important when comparing collections or arrays</li>
 * </ul>
 *
 * @see ReflectionComparator
 * @see ReflectionComparatorModes
 */
public class ReflectionAssert {


    /* The comparator for lenient comparing expected and actual values */
    private ReflectionComparator reflectionComparator;


    /**
     * Creates a reflection assert instance.
     * The modes specify how to compare the expected value with the actual value.
     *
     * @param modes the comparator modes
     */
    public ReflectionAssert(ReflectionComparatorModes... modes) {

        this.reflectionComparator = new ReflectionComparator(modes);
    }

    // Reflection comparator

    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public void assertEquals(Object expected, Object actual) throws AssertionFailedError {

        assertEquals(null, expected, actual);
    }


    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param message  a message for when the assertion fails
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

    /**
     * Asserts that the value of the property with the given name contained in the given object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertEquals(Object, Object)} is used to check whether both values are equal. The comparator modes
     * determine how strict to compare the values.
     *
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property
     */
    public void assertPropertyEquals(String propertyName, Object expectedPropertyValue, Object actualObject) {

        assertPropertyEquals(null, propertyName, expectedPropertyValue, actualObject);
    }

    /**
     * Asserts that the value of the property with the given name contained in the given object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertEquals(Object, Object)} is used to check whether both values are equal. The comparator modes
     * determine how strict to compare the values.
     *
     * @param message               a message for when the assertion fails
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property
     */
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


    /**
     * Asserts that the values of the property with the given name contained each element of the given collection are
     * equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertEquals(Object, Object)} is used to check whether both values are equal. The comparator modes
     * determine how strict to compare the values.
     *
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values
     * @param actualObjects          the objects that contain the property
     */
    public void assertPropertyEquals(String propertyName, Collection expectedPropertyValues, Collection actualObjects) {

        assertPropertyEquals(null, propertyName, expectedPropertyValues, actualObjects);
    }


    /**
     * Asserts that the values of the property with the given name contained each element of the given collection are
     * equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertEquals(Object, Object)} is used to check whether both values are equal. The comparator modes
     * determine how strict to compare the values.
     *
     * @param message                a message for when the assertion fails
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values
     * @param actualObjects          the objects that contain the property
     */
    public void assertPropertyEquals(String message, String propertyName, Collection expectedPropertyValues, Collection actualObjects) {

        Collection actualPropertyValues = CollectionUtils.collect(actualObjects, new BeanToPropertyValueTransformer(propertyName));
        assertEquals(message, expectedPropertyValues, actualPropertyValues);
    }


    /**
     * Formats the exception message.
     *
     * @param suppliedMessage the user supplied message
     * @param difference      the difference
     * @return the formatted message
     */
    private String formatMessage(String suppliedMessage, Difference difference) {

        String result = formatMessage(suppliedMessage, difference.getMessage());

        String fieldString = difference.getFieldStackAsString();
        if (StringUtils.isEmpty(fieldString)) {
            fieldString = "<top-level>";
        }

        result += "\nField: <" + fieldString;
        result += "> expected: <" + difference.getLeftValue();
        result += "> but was: <" + difference.getRightValue() + ">";
        return result;
    }


    /**
     * Formats the exception message.
     *
     * @param suppliedMessage the user supplied message
     * @param specificMessage the reason
     * @return the formatted message
     */
    private String formatMessage(String suppliedMessage, String specificMessage) {

        if (StringUtils.isEmpty(suppliedMessage)) {
            return specificMessage;
        }
        return suppliedMessage + "\n" + specificMessage;
    }

}