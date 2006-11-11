/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.reflectionassert;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import org.unitils.reflectionassert.ReflectionComparator.Difference;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;


/**
 * A class for asserting that 2 objects/collections are equal by comparing properties and fields of the
 * objects/collections using reflection.
 * <p/>
 * The (combination of) comparator modes specify how strict the comparison must be:<ul>
 * <li>ignore defaults: compare only arguments (and inner values) that have a non default value (eg null) as exepected value</li>
 * <li>lenient dates: do not compare actual date values, just that they both have a value or not</li>
 * <li>lenient order: order is not important when comparing collections or arrays</li>
 * </ul>
 * <p/>
 * There are 2 versions of each method: a len and a ref verion.
 * With the ref versions you can set the comparator modes explicitly (note: no modes means strict comparisson). The len
 * versions are the same as the ref versions but have lenient order and ignore defaults set by default.
 * <p/>
 * The name assert..RefEquals is chosen instead of assert..Equals so it can be added as a static import
 * without naming collisions.
 *
 * @see ReflectionComparator
 * @see ReflectionComparatorMode
 */
public class ReflectionAssert {


    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * This is identical to {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertLenEquals(Object expected, Object actual) throws AssertionFailedError {

        assertLenEquals(null, expected, actual);
    }


    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param expected the expected object
     * @param actual   the given object
     * @param modes    the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertRefEquals(Object expected, Object actual, ReflectionComparatorMode... modes) throws AssertionFailedError {

        assertRefEquals(null, expected, actual, modes);
    }


    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * This is identical to {@link #assertRefEquals(String,Object,Object,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param message  a message for when the assertion fails
     * @param expected the expected object
     * @param actual   the given object
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertLenEquals(String message, Object expected, Object actual) throws AssertionFailedError {

        assertRefEquals(message, expected, actual, LENIENT_ORDER, IGNORE_DEFAULTS);
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
     * @param modes    the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertRefEquals(String message, Object expected, Object actual, ReflectionComparatorMode... modes) throws AssertionFailedError {

        ReflectionComparator reflectionComparator = new ReflectionComparator(modes);
        Difference difference = reflectionComparator.getDifference(expected, actual);
        if (difference != null) {
            Assert.fail(formatMessage(message, difference));
        }
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,Object,Object,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property, not null
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String propertyName, Object expectedPropertyValue, Object actualObject) throws AssertionFailedError {

        assertPropertyLenEquals(null, propertyName, expectedPropertyValue, actualObject);
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property, not null
     * @param modes                 the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String propertyName, Object expectedPropertyValue, Object actualObject, ReflectionComparatorMode... modes) throws AssertionFailedError {

        assertPropertyRefEquals(null, propertyName, expectedPropertyValue, actualObject, modes);
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,String,Object,Object,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param message               a message for when the assertion fails
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property, not null
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String message, String propertyName, Object expectedPropertyValue, Object actualObject) throws AssertionFailedError {

        assertPropertyRefEquals(message, propertyName, expectedPropertyValue, actualObject, LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param message               a message for when the assertion fails
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property, not null
     * @param modes                 the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String message, String propertyName, Object expectedPropertyValue, Object actualObject, ReflectionComparatorMode... modes) throws AssertionFailedError {

        try {
            Object propertyValue = PropertyUtils.getProperty(actualObject, propertyName);
            String formattedMessage = formatMessage(message, "Incorrect value for property: " + propertyName);
            assertRefEquals(formattedMessage, expectedPropertyValue, propertyValue, modes);

        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);

        } catch (InvocationTargetException e) {
            throw new UnitilsException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Error while accessing property: " + propertyName + " of object: " + actualObject, e);
        }
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,Collection,Collection,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values
     * @param actualObjects          the objects that contain the property, not null
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String propertyName, Collection expectedPropertyValues, Collection actualObjects) throws AssertionFailedError {

        assertPropertyLenEquals(null, propertyName, expectedPropertyValues, actualObjects);
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values
     * @param actualObjects          the objects that contain the property, not null
     * @param modes                  the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String propertyName, Collection expectedPropertyValues, Collection actualObjects, ReflectionComparatorMode... modes) throws AssertionFailedError {

        assertPropertyRefEquals(null, propertyName, expectedPropertyValues, actualObjects, modes);
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,String,Collection,Collection,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param message                a message for when the assertion fails
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values, not null
     * @param actualObjects          the objects that contain the property, not null
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String message, String propertyName, Collection expectedPropertyValues, Collection actualObjects) throws AssertionFailedError {

        assertPropertyRefEquals(message, propertyName, expectedPropertyValues, actualObjects, LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object,ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param message                a message for when the assertion fails
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values, not null
     * @param actualObjects          the objects that contain the property, not null
     * @param modes                  the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String message, String propertyName, Collection expectedPropertyValues, Collection actualObjects, ReflectionComparatorMode... modes) throws AssertionFailedError {

        Collection actualPropertyValues = CollectionUtils.collect(actualObjects, new BeanToPropertyValueTransformer(propertyName));
        assertRefEquals(message, expectedPropertyValues, actualPropertyValues, modes);
    }


    /**
     * Formats the exception message.
     *
     * @param suppliedMessage the user supplied message
     * @param difference      the difference
     * @return the formatted message
     */
    private static String formatMessage(String suppliedMessage, Difference difference) {

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
    private static String formatMessage(String suppliedMessage, String specificMessage) {

        if (StringUtils.isEmpty(suppliedMessage)) {
            return specificMessage;
        }
        return suppliedMessage + "\n" + specificMessage;
    }

}