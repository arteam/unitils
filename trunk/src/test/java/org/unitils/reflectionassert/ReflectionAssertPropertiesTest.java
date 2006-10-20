package org.unitils.reflectionassert;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionComparatorModes.IGNORE_DEFAULTS;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods.
 */
public class ReflectionAssertPropertiesTest extends TestCase {

    /* Test object */
    private TestObject testObject;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        testObject = new TestObject(1, "stringValue");
    }


    /**
     * Test for equal property value.
     */
    public void testAssertPropertyRefEquals_equals() {

        ReflectionAssert.assertPropertyRefEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyRefEquals_equalsMessage() {

        ReflectionAssert.assertPropertyRefEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value.
     */
    public void testAssertPropertyLenEquals_equals() {

        ReflectionAssert.assertPropertyLenEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyLenEquals_equalsMessage() {

        ReflectionAssert.assertPropertyLenEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal primitive property value.
     */
    public void testAssertPropertyRefEquals_equalsPrimitive() {

        ReflectionAssert.assertPropertyRefEquals("primitiveProperty", 1L, testObject);
    }


    /**
     * Test for different property value.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentValues() {

        try {
            ReflectionAssert.assertPropertyRefEquals("stringProperty", "xxxxxx", testObject);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }

    /**
     * Test case for a null left-argument.
     */
    public void testAssertPropertyRefEquals_leftNull() {

        try {
            ReflectionAssert.assertPropertyRefEquals("stringProperty", null, testObject);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for a null right-argument.
     */
    public void testAssertPropertyRefEquals_rightNull() {

        testObject.setStringProperty(null);
        try {
            ReflectionAssert.assertPropertyRefEquals("stringProperty", "stringValue", testObject);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for both null arguments.
     */
    public void testAssertPropertyRefEquals_null() {

        testObject.setStringProperty(null);
        ReflectionAssert.assertPropertyRefEquals("stringProperty", null, testObject);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyRefEquals_equalsIgnoredDefault() {

        ReflectionAssert.assertPropertyRefEquals("a message", "stringProperty", null, testObject, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyLenEquals_equalsIgnoredDefault() {

        ReflectionAssert.assertPropertyLenEquals("stringProperty", null, testObject);
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
