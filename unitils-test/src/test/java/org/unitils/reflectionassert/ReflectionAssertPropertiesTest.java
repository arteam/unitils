/*
 * Copyright 2013,  Unitils.org
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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;

/**
 * Test class for {@link ReflectionAssert} tests for with assertProperty methods.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertPropertiesTest {

    private TestObject testObject;


    @Before
    public void initialize() {
        testObject = new TestObject(1, "stringValue");
    }


    /**
     * Test for equal property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equals() {
        assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsMessage() {
        assertPropertyReflectionEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value.
     */
    @Test
    public void testAssertPropertyLenientEquals_equals() {
        assertPropertyLenientEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    @Test
    public void testAssertPropertyLenientEquals_equalsMessage() {
        assertPropertyLenientEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal primitive property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsPrimitive() {
        assertPropertyReflectionEquals("primitiveProperty", 1L, testObject);
    }


    /**
     * Test for different property value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_notEqualsDifferentValues() {
        try {
            assertPropertyReflectionEquals("stringProperty", "xxxxxx", testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionError a) {
            // expected
        }
    }

    /**
     * Test case for a null left-argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_leftNull() {
        try {
            assertPropertyReflectionEquals("stringProperty", null, testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionError a) {
            // expected
        }
    }


    /**
     * Test case for a null right-argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_rightNull() {
        testObject.setStringProperty(null);
        try {
            assertPropertyReflectionEquals("stringProperty", "stringValue", testObject);
            fail("Expected AssertionFailedError");
        } catch (AssertionError a) {
            // expected
        }
    }


    /**
     * Test case for null as actual object argument.
     */
    @Test
    public void testAssertPropertyReflectionEquals_actualObjectNull() {
        try {
            assertPropertyReflectionEquals("aProperty", "aValue", null);
            fail("Expected AssertionFailedError");
        } catch (AssertionError a) {
            // expected
        }
    }


    /**
     * Test case for both null arguments.
     */
    @Test
    public void testAssertPropertyReflectionEquals_null() {
        testObject.setStringProperty(null);
        assertPropertyReflectionEquals("stringProperty", null, testObject);
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertPropertyReflectionEquals_equalsIgnoredDefault() {
        assertPropertyReflectionEquals("a message", "stringProperty", null, testObject, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertPropertyLenientEquals_equalsIgnoredDefault() {
        assertPropertyLenientEquals("stringProperty", null, testObject);
    }


    /**
     * Test class with failing equals containing test properties.
     */
    public static class TestObject {

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
