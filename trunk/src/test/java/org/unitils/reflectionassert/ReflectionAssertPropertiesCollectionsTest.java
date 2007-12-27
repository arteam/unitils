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

import static java.util.Arrays.asList;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyRefEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.List;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link org.unitils.reflectionassert.ReflectionAssert} tests for with
 * assertProperty methods with collection arguments.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertPropertiesCollectionsTest {


    /* A test collection */
    List<TestObject> list;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        list = asList(new TestObject(1L, "el1"), new TestObject(2L, "el2"));
    }


    /**
     * Test for equal property values.
     */
    @Test
    public void testAssertPropertyRefEquals() {
        assertPropertyRefEquals("stringProperty", asList("el1", "el2"), list);
    }


    /**
     * Test for equal property values but of different types (int versus long).
     */
    @Test
    public void testAssertPropertyRefEquals_differentTypes() {
        assertPropertyRefEquals("primitiveProperty", asList(1L, 2L), list);
    }


    /**
     * Test for different property values.
     */
    @Test
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
    @Test
    public void testAssertPropertyRefEquals_equalsDifferentOrder() {
        assertPropertyRefEquals("stringProperty", asList("el1", "el2"), list, LENIENT_ORDER);
    }


    /**
     * Test for property values with different order.
     */
    @Test
    public void testAssertPropertyLenEquals_equalsDifferentOrder() {
        assertPropertyLenEquals("stringProperty", asList("el1", "el2"), list);
    }


    /**
     * Test for property values with different order.
     */
    @Test
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
    @Test
    public void testAssertPropertyRefEquals_equalsPrimitivesList() {
        assertPropertyLenEquals("primitiveProperty", asList(2, 1), list);
    }


    /**
     * Test for different primitive property values. Using ints instead of longs.
     */
    @Test
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
    @Test
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
