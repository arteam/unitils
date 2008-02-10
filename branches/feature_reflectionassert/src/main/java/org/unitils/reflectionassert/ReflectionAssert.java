/*
 * Copyright 2006-2007,  Unitils.org
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
import static junit.framework.Assert.assertNotNull;
import junit.framework.AssertionFailedError;
import ognl.DefaultMemberAccess;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.formatter.DifferenceReport;

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
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see ReflectionComparator
 * @see ReflectionComparatorMode
 */
public class ReflectionAssert {


    /**
     * Asserts that two objects are equal. Reflection is used to compare all fields of these values.
     * If they are not equal an AssertionFailedError is thrown.
     * <p/>
     * This is identical to {@link #assertRefEquals(Object,Object, ReflectionComparatorMode...)} with
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
        ReflectionComparator reflectionComparator = createRefectionComparator(modes);
        Difference difference = reflectionComparator.getDifference(expected, actual);
        if (difference != null) {
            Assert.fail(DifferenceReport.createReport(message, difference));
        }
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object, ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,Object,Object,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property
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
     * @param actualObject          the object that contains the property
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
     * @param actualObject          the object that contains the property
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String message, String propertyName, Object expectedPropertyValue, Object actualObject) throws AssertionFailedError {
        assertPropertyRefEquals(message, propertyName, expectedPropertyValue, actualObject, LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Asserts that the value of a property of an object is equal to the given value.
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object, ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param message               a message for when the assertion fails
     * @param propertyName          the property, not null
     * @param expectedPropertyValue the expected value
     * @param actualObject          the object that contains the property
     * @param modes                 the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String message, String propertyName, Object expectedPropertyValue, Object actualObject, ReflectionComparatorMode... modes) throws AssertionFailedError {
        assertNotNull("Actual object is null.", actualObject);
        Object propertyValue = getProperty(actualObject, propertyName);
        String formattedMessage = formatMessage(message, "Incorrect value for property: " + propertyName);
        assertRefEquals(formattedMessage, expectedPropertyValue, propertyValue, modes);
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object, ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * This is identical to {@link #assertPropertyRefEquals(String,Collection,Collection,ReflectionComparatorMode...)} with
     * lenient order and ignore defaults set as comparator modes.
     *
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values
     * @param actualObjects          the objects that contain the property
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String propertyName, Collection<?> expectedPropertyValues, Collection<?> actualObjects) throws AssertionFailedError {
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
     * @param actualObjects          the objects that contain the property
     * @param modes                  the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String propertyName, Collection<?> expectedPropertyValues, Collection<?> actualObjects, ReflectionComparatorMode... modes) throws AssertionFailedError {
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
     * @param actualObjects          the objects that contain the property
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyLenEquals(String message, String propertyName, Collection<?> expectedPropertyValues, Collection<?> actualObjects) throws AssertionFailedError {
        assertPropertyRefEquals(message, propertyName, expectedPropertyValues, actualObjects, LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Asserts that a property of all objects in the collection are equal to the given values.
     * <p/>
     * Example:  assertPropertyEquals("id", myIdCollection, myObjectCollection) checks whether all values of the
     * id field of the myObjectCollection elements matches the values in the myIdCollection
     * <p/>
     * Bean notation can be used to specify inner properties. Eg myArray[2].innerValue.
     * {@link #assertRefEquals(Object,Object, ReflectionComparatorMode...)} is used to check whether both values are equal.
     * <p/>
     * The comparator modes determine how strict to compare the values.
     *
     * @param message                a message for when the assertion fails
     * @param propertyName           the property, not null
     * @param expectedPropertyValues the expected values, not null
     * @param actualObjects          the objects that contain the property
     * @param modes                  the comparator modes
     * @throws AssertionFailedError when both objects are not equals
     */
    public static void assertPropertyRefEquals(String message, String propertyName, Collection<?> expectedPropertyValues, Collection<?> actualObjects, ReflectionComparatorMode... modes) throws AssertionFailedError {
        assertNotNull("Actual object list is null.", actualObjects);
        Collection<?> actualPropertyValues = CollectionUtils.collect(actualObjects, new OgnlTransformer(propertyName));
        assertRefEquals(message, expectedPropertyValues, actualPropertyValues, modes);
    }


    /**
     * Formats the exception message.
     *
     * @param suppliedMessage the user supplied message
     * @param specificMessage the reason
     * @return the formatted message
     */
    protected static String formatMessage(String suppliedMessage, String specificMessage) {
        if (StringUtils.isEmpty(suppliedMessage)) {
            return specificMessage;
        }
        return suppliedMessage + "\n" + specificMessage;
    }


    /**
     * Evaluates the given OGNL expression, and returns the corresponding property value from the given object.
     *
     * @param object         The object on which the expression is evaluated
     * @param ognlExpression The OGNL expression that is evaluated
     * @return The value for the given OGNL expression
     */
    protected static Object getProperty(Object object, String ognlExpression) {
        try {
            OgnlContext ognlContext = new OgnlContext();
            ognlContext.setMemberAccess(new DefaultMemberAccess(true));
            Object ognlExprObj = Ognl.parseExpression(ognlExpression);
            return Ognl.getValue(ognlExprObj, ognlContext, object);
        } catch (OgnlException e) {
            throw new UnitilsException("Failed to get property value using OGNL expression " + ognlExpression, e);
        }
    }


    /**
     * A commons collections transformer that takes an object and returns the value of the property that is
     * specified by the given ognl expression.
     */
    protected static class OgnlTransformer implements Transformer {

        /* The ognl expression */
        private String ognlExpression;

        /**
         * Creates  a transformer with the given ognl expression.
         *
         * @param ognlExpression The expression, not null
         */
        public OgnlTransformer(String ognlExpression) {
            this.ognlExpression = ognlExpression;
        }

        /**
         * Transforms the given object in the value of the property that is specified by the ognl expression.
         *
         * @param object The object
         * @return The value, null if object was null
         */
        public Object transform(Object object) {
            if (object == null) {
                return null;
            }
            return getProperty(object, ognlExpression);
        }
    }

}