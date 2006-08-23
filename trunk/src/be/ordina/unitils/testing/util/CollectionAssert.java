package be.ordina.unitils.testing.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.bag.HashBag;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.AssertionFailedError;
import junit.framework.Assert;

/**
 * A class offering specific assert statements for validating the contents of a collection
 */
public class CollectionAssert {

    /**
     * @deprecated Use assertPropertyEquals instead
     */
    public static void assertMatches(Object[] expected, Collection actual, String propertyName) {
        assertMatches(Arrays.asList(expected), actual, propertyName);
    }

    /**
     * @deprecated Use assertPropertyEquals instead
     */
    public static void assertMatches(Collection expected, Collection actual, String propertyName) {
        Set expectedPropertyValues = new HashSet(expected);
        Set actualPropertyValues = getPropertyValues(actual, propertyName);
        Set missingValues = new HashSet(expectedPropertyValues);
        missingValues.removeAll(actualPropertyValues);
        if (!missingValues.isEmpty()) {
            throw new AssertionFailedError("Objects with values " + missingValues + " for property " + propertyName + " expected, but not found");
        }
        Set excessValues = new HashSet(actualPropertyValues);
        excessValues.removeAll(expectedPropertyValues);
        if (!excessValues.isEmpty()) {
            throw new AssertionFailedError("Objects with values " + excessValues + " for property " + propertyName + " found, which were not expected");
        }
    }

    private static Set<String> getPropertyValues(Collection collection, String propertyName) {
        Set<String> result = new HashSet<String>();
        for (Object item : collection) {
            try {
                result.add(BeanUtils.getProperty(item, propertyName));
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while accessing property " + propertyName + " of bean " + item, e);
            }
        }
        return result;
    }

    public static void assertPropertyEquals(Collection expectedPropertyValues, Collection actualObjects,
                                     String nestedPropertyName) {
        assertPropertyEquals(null, expectedPropertyValues, actualObjects, nestedPropertyName);
    }

    public static void assertPropertyEquals(Collection expectedPropertyValues, Collection actualObjects,
                                     String nestedPropertyName, boolean strictSequence) {
        assertPropertyEquals(null, expectedPropertyValues, actualObjects, nestedPropertyName, strictSequence);
    }

    public static void assertPropertyEquals(String message, Collection expectedPropertyValues, Collection actualObjects,
                                     String nestedPropertyName) {
        assertPropertyEquals(message, expectedPropertyValues, actualObjects, nestedPropertyName, false);
    }

    public static void assertPropertyEquals(String message, Collection expectedPropertyValues, Collection actualObjects,
                                     String nestedPropertyName, boolean strictSequence) {
        Collection actualPropertyValues = CollectionUtils.collect(actualObjects,
                new BeanToPropertyValueTransformer(nestedPropertyName));
        assertEquals(message, expectedPropertyValues, actualPropertyValues, strictSequence);
    }

    public static void assertEquals(Collection expected, Collection actual) {
        assertEquals(null, expected, actual);
    }

    public static void assertEquals(Collection expected, Collection actual, boolean strictSequence) {
        assertEquals(null, expected, actual, strictSequence);
    }

    public static void assertEquals(String message, Collection expected, Collection actual) {
        assertEquals(message, expected, actual, false);
    }

    public static void assertEquals(String message, Collection expected, Collection actual,
                             boolean strictSequence) {
        if (strictSequence) {
            assertEqualsStrictSequence(message, expected, actual);
        } else {
            assertEqualsLenientSequence(message, expected, actual);
        }
    }

    private static void assertEqualsStrictSequence(String message, Collection expected, Collection actual) {
        Assert.assertEquals(MessageFormatUtil.formatMessage(message, "Expected and actual do not have the same size"), expected.size(), actual.size());
        int index = 0;
        Iterator actualIt = actual.iterator();
        for (Object exp : expected) {
            Object act = actualIt.next();
            Assert.assertEquals(MessageFormatUtil.formatMessage(message, "Different objects at index " + index),
                    exp, act);
            index++;
        }
    }

    private static void assertEqualsLenientSequence(String message, Collection expected, Collection actual) {
        // Check for missing values in the actual collection
        Bag missingValues = new HashBag(expected);
        missingValues.removeAll(actual);
        if (!missingValues.isEmpty()) {
            throw new AssertionFailedError(MessageFormatUtil.formatMessage(message, "Objects  " + missingValues +
                    " expected, but not found"));
        }
        // Check for excess values in the expected collection
        Bag excessValues = new HashBag(actual);
        excessValues.removeAll(expected);
        if (!excessValues.isEmpty()) {
            throw new AssertionFailedError(MessageFormatUtil.formatMessage(message, "Objects " + excessValues +
                    " found, which were not expected"));
        }
    }

}
