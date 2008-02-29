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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyRefEquals;
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
    public void testAssertPropertyRefEquals_equals() {
        assertPropertyRefEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyRefEquals_equalsMessage() {
        assertPropertyRefEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value.
     */
    public void testAssertPropertyLenEquals_equals() {
        assertPropertyLenEquals("stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal property value (message version).
     */
    public void testAssertPropertyLenEquals_equalsMessage() {
        assertPropertyLenEquals("a message", "stringProperty", "stringValue", testObject);
    }


    /**
     * Test for equal primitive property value.
     */
    public void testAssertPropertyRefEquals_equalsPrimitive() {
        assertPropertyRefEquals("primitiveProperty", 1L, testObject);
    }


    /**
     * Test for different property value.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentValues() {
        try {
            assertPropertyRefEquals("stringProperty", "xxxxxx", testObject);
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
            assertPropertyRefEquals("stringProperty", null, testObject);
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
            assertPropertyRefEquals("stringProperty", "stringValue", testObject);
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
        assertPropertyRefEquals("stringProperty", null, testObject);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyRefEquals_equalsIgnoredDefault() {
        assertPropertyRefEquals("a message", "stringProperty", null, testObject, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertPropertyLenEquals_equalsIgnoredDefault() {
        assertPropertyLenEquals("stringProperty", null, testObject);
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