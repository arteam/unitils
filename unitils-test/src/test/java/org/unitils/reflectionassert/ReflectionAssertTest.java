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
import org.unitils.UnitilsJUnit4;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import static org.unitils.util.CollectionUtils.asSet;


/**
 * Test class for {@link ReflectionAssert}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertTest extends UnitilsJUnit4 {

    /* Test object */
    private TestObjectString testObjectAString;

    /* Same as A but different instance */
    private TestObjectString testObjectBString;

    /* Same as A and B but different string value for stringValue2 */
    private TestObjectString testObjectDifferentValueString;

    /* Test object */
    private TestObjectIntString testObjectAIntString;

    /* Same as A but different instance */
    private TestObjectIntString testObjectBIntString;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        testObjectAString = new TestObjectString("test 1", "test 2");
        testObjectBString = new TestObjectString("test 1", "test 2");
        testObjectDifferentValueString = new TestObjectString("test 1", "XXXXXX");
        testObjectAIntString = new TestObjectIntString(1, "test");
        testObjectBIntString = new TestObjectIntString(1, "test");
    }


    /**
     * Test for two equal objects.
     */
    @Test
    public void testAssertReflectionEquals_equals() {
        assertReflectionEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects (message version).
     */
    @Test
    public void testAssertReflectionEquals_equalsMessage() {
        assertReflectionEquals("a message", testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects.
     */
    @Test
    public void testAssertLenientEquals_equals() {
        assertLenientEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects (message version).
     */
    @Test
    public void testAssertLenientEquals_equalsMessage() {
        assertLenientEquals("a message", testObjectAString, testObjectBString);
    }


    /**
     * Test for two objects that contain different values.
     */
    @Test(expected = AssertionError.class)
    public void testAssertReflectionEquals_notEqualsDifferentValues() {
        assertReflectionEquals(testObjectAString, testObjectDifferentValueString);
    }


    /**
     * Test case for a null left-argument.
     */
    @Test(expected = AssertionError.class)
    public void testAssertReflectionEquals_leftNull() {
        assertReflectionEquals(null, testObjectAString);
    }


    /**
     * Test case for a null right-argument.
     */
    @Test(expected = AssertionError.class)
    public void testAssertReflectionEquals_rightNull() {
        assertReflectionEquals(testObjectAString, null);
    }


    /**
     * Test case for both null arguments.
     */
    @Test
    public void testAssertReflectionEquals_null() {
        assertReflectionEquals(null, null);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertReflectionEquals_equalsLenientOrder() {
        assertReflectionEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"), LENIENT_ORDER);
    }


    /**
     * Test for two equal sets but with different order.
     */
    @Test
    public void testAssertReflectionEquals_equalsLenientOrderSet() {
        assertReflectionEquals(asSet(testObjectAString, testObjectAIntString), asSet(testObjectBIntString, testObjectBString), LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertLenientEquals_equalsLenientOrder() {
        assertLenientEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"));
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertReflectionEquals_equalsIgnoredDefault() {
        testObjectAString.setString1(null);
        testObjectBString.setString1("xxxxxx");

        assertReflectionEquals(testObjectAString, testObjectBString, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertLenientEquals_equalsIgnoredDefault() {
        testObjectAString.setString1(null);
        testObjectBString.setString1("xxxxxx");

        assertLenientEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for message of 2 not equal arrays. Should return return actual content instead of something like String[234
     */
    @Test
    public void testAssertLenientEquals_formatArraysMessage() {
        try {
            assertLenientEquals(new String[]{"test1", "test2"}, new Integer[]{1, 2});
        } catch (AssertionError a) {
            // expected
            assertTrue(a.getMessage().contains("[\"test1\", \"test2\"]"));
            assertTrue(a.getMessage().contains("[1, 2]"));
        }
    }


    @Test
    public void assertPropertiesNotNullTest_fullySetObject() {
        ReflectionAssert.assertPropertiesNotNull("properties parentObject ar not fully set", new TestObjectString("", ""));
    }

    @Test(expected = AssertionError.class)
    public void assertPropertiesNotNullTestFail() {
        ReflectionAssert.assertPropertiesNotNull("properties childobject ar not fully set", new TestObjectString(null, ""));
    }


    /**
     * Test class with 2 string fields and a failing equals.
     */
    private class TestObjectString {

        private String string1;

        private String string2;

        public TestObjectString(String stringValue1, String stringValue2) {
            this.string1 = stringValue1;
            this.string2 = stringValue2;
        }

        public String getString1() {
            return string1;
        }

        public void setString1(String string1) {
            this.string1 = string1;
        }

        public String getString2() {
            return string2;
        }

        public void setString2(String string2) {
            this.string2 = string2;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }


    /**
     * Test class with int and string field.
     */
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private class TestObjectIntString {

        private int intValue;

        private String stringValue;

        public TestObjectIntString(int intValue, String stringValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
        }
    }
}
