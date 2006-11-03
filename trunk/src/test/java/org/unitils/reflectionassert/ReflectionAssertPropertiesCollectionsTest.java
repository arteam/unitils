package org.unitils.reflectionassert;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionAssert} tests for with
 * assertProperty methods with collection arguments.
 */
public class ReflectionAssertPropertiesCollectionsTest extends TestCase {


    /* A test collection */
    private List<TestObject> list;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        list = Arrays.asList(new TestObject(1, "el1"), new TestObject(2, "el2"));
    }


    /**
     * Test for equal property values.
     */
    public void testAssertPropertyRefEquals() {

        ReflectionAssert.assertPropertyRefEquals("stringProperty", Arrays.asList("el1", "el2"), list);
    }


    /**
     * Test for different property values.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentValues() {

        try {
            ReflectionAssert.assertPropertyRefEquals("stringProperty", Arrays.asList("xxxxx", "xxxxx"), list);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyRefEquals_equalsDifferentOrder() {

        ReflectionAssert.assertPropertyRefEquals("stringProperty", Arrays.asList("el1", "el2"), list, LENIENT_ORDER);
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyLenEquals_equalsDifferentOrder() {

        ReflectionAssert.assertPropertyLenEquals("stringProperty", Arrays.asList("el1", "el2"), list);
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentOrder() {

        try {
            ReflectionAssert.assertPropertyRefEquals("stringProperty", Arrays.asList("el2", "el1"), list);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for equal primitive property values.
     */
    public void testAssertPropertyRefEquals_equalsPrimitivesList() {

        ReflectionAssert.assertPropertyLenEquals("primitiveProperty", Arrays.asList(2L, 1L), list);
    }


    /**
     * Test for different primitive property values.
     */
    public void testAssertPropertyRefEquals_notEqualsPrimitivesList() {

        try {
            ReflectionAssert.assertPropertyLenEquals("primitiveProperty", Arrays.asList(999L, 1L), list);
            Assert.fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test class with failing equals containing test properties.
     */
    public class TestObject {

        private long primitiveProperty;

        private String stringProperty;

        public TestObject(long primitiveProperty, String stringProperty) {
            this.primitiveProperty = primitiveProperty;
            this.stringProperty = stringProperty;
        }

        public long getPrimitiveProperty() {
            return primitiveProperty;
        }

        public void setPrimitiveProperty(long primitiveProperty) {
            this.primitiveProperty = primitiveProperty;
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }
    }

}
