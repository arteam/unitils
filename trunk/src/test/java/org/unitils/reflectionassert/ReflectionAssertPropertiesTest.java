/*
 * Copyright 2008,  Unitils.org
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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
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
    public void testAssertPropertyReflectionEquals_equals() {
        assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyReflectionEquals_equalsMessage() {
        assertPropertyReflectionEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value.
     */
    public void testAssertPropertyLenientEquals_equals() {
        assertPropertyLenientEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyLenientEquals_equalsMessage() {
        assertPropertyLenientEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal primitive property value.
     */
    public void testAssertPropertyReflectionEquals_equalsPrimitive() {
        assertPropertyReflectionEquals("primitiveProperty", 1L, testObject);
    }


    /**
     * Test for different property value.
     */
    public void testAssertPropertyReflectionEquals_notEqualsDifferentValues() {
        try {
            assertPropertyReflectionEquals("stringProperty", "xxxxxx", testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError a) {
            // expected
        }
    }

    /**
     * Test case for a null left-argument.
     */
    public void testAssertPropertyReflectionEquals_leftNull() {
        try {
            assertPropertyReflectionEquals("stringProperty", null, testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for a null right-argument.
     */
    public void testAssertPropertyReflectionEquals_rightNull() {
        testObject.setStringProperty(null);
        try {
            assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for null as actual object argument.
     */
    public void testAssertPropertyReflectionEquals_actualObjectNull() {
        try {
            assertPropertyReflectionEquals("aProperty", "aValue", null);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for both null arguments.
     */
    public void testAssertPropertyReflectionEquals_null() {
        testObject.setStringProperty(null);
        assertPropertyReflectionEquals("stringProperty", null, testObject);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyReflectionEquals_equalsIgnoredDefault() {
        assertPropertyReflectionEquals("a message", "stringProperty", null, testObject, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyLenientEquals_equalsIgnoredDefault() {
        assertPropertyLenientEquals("stringProperty", null, testObject);
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
