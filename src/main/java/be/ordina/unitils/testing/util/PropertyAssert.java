package be.ordina.unitils.testing.util;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import junit.framework.Assert;

/**
 * @author Filip Neven
 */
public class PropertyAssert {

    public static void assertPropertyEquals(Object expectedPropertyValue, Object actualObject, String nestedPropertyName) {
        assertPropertyEquals(null, expectedPropertyValue, actualObject, nestedPropertyName);
    }

    public static void assertPropertyEquals(String message, Object expectedPropertyValue, Object actualObject,
            String nestedPropertyName) {
        try {
            Object propertyValue = PropertyUtils.getProperty(actualObject, nestedPropertyName);
            Assert.assertEquals(MessageFormatUtil.formatMessage(message, "Incorrect value for property " +
                    nestedPropertyName), expectedPropertyValue, propertyValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while accessing property " + nestedPropertyName + " of object " + actualObject, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error while accessing property " + nestedPropertyName + " of object " + actualObject, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Error while accessing property " + nestedPropertyName + " of object " + actualObject, e);
        }
    }

}
