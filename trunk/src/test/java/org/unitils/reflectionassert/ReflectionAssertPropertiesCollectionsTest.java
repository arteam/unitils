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
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyRefEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import static java.util.Arrays.asList;
import java.util.List;

/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionAssert} tests for with
 * assertProperty methods with collection arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertPropertiesCollectionsTest extends TestCase {


    /* A test collection */
    private List<TestObject> list;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        list = asList(new TestObject(1L, "el1"), new TestObject(2L, "el2"));
    }


    /**
     * Test for equal property values.
     */
    public void testAssertPropertyRefEquals() {
        assertPropertyRefEquals("stringProperty", asList("el1", "el2"), list);
    }


    /**
     * Test for equal property values but of different types (int versus long).
     */
    public void testAssertPropertyRefEquals_differentTypes() {
        assertPropertyRefEquals("primitiveProperty", asList(1L, 2L), list);
    }


    /**
     * Test for different property values.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentValues() {
        try {
            assertPropertyRefEquals("stringProperty", asList("xxxxx", "xxxxx"), list);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyRefEquals_equalsDifferentOrder() {
        assertPropertyRefEquals("stringProperty", asList("el1", "el2"), list, LENIENT_ORDER);
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyLenEquals_equalsDifferentOrder() {
        assertPropertyLenEquals("stringProperty", asList("el1", "el2"), list);
    }


    /**
     * Test for property values with different order.
     */
    public void testAssertPropertyRefEquals_notEqualsDifferentOrder() {
        try {
            assertPropertyRefEquals("stringProperty", asList("el2", "el1"), list);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test for equal primitive property values. Using ints instead of longs.
     */
    public void testAssertPropertyRefEquals_equalsPrimitivesList() {
        assertPropertyLenEquals("primitiveProperty", asList(2, 1), list);
    }


    /**
     * Test for different primitive property values. Using ints instead of longs.
     */
    public void testAssertPropertyRefEquals_notEqualsPrimitivesList() {
        try {
            assertPropertyLenEquals("primitiveProperty", asList(999, 1), list);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError e) {
            // Expected
        }
    }


    /**
     * Test case for null as actual object argument.
     */
    public void testAssertPropertyRefEquals_actualObjectNull() {
        try {
            assertPropertyLenEquals("stringProperty", asList(1, 2), null);
            fail("Expected AssertionFailedError");
        } catch (AssertionFailedError a) {
            // expected
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
