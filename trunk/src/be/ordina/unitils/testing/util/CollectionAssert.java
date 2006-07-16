package be.ordina.unitils.testing.util;

import org.apache.commons.beanutils.BeanUtils;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import junit.framework.AssertionFailedError;

/**
 * A class offering specific assert statements for validating the contents of a collection
 */
public class CollectionAssert {

    public static void assertMatches(Object[] expected, Collection actual, String propertyName) {
        assertMatches(Arrays.asList(expected), actual, propertyName);
    }

    public static void assertMatches(Collection expected, Collection actual, String propertyName) {
        Set<String> expectedPropertyValues = new HashSet(expected);
        Set<String> actualPropertyValues = getPropertyValues(actual, propertyName);
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
                result.add(BeanUtils.getSimpleProperty(item, propertyName));
            } catch (Exception e) {
                throw new IllegalArgumentException("Error while accessing property " + propertyName + " of bean " + item, e);
            }
        }
        return result;
    }

}
