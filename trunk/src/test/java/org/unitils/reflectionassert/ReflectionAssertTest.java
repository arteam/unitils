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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import junit.framework.AssertionFailedError;


/**
 * Test class for {@link ReflectionAssert}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertTest {

    /* Test object */
    private TestObject testObjectA;

    /* Same as A but different instance */
    private TestObject testObjectB;

    /* Same as A and B but different string value for stringValue2 */
    private TestObject testObjectDifferentValue;


    /**
     * Initializes the test fixture.
     */
    public void setUp() throws Exception {
        testObjectA = new TestObject("test 1", "test 2");
        testObjectB = new TestObject("test 1", "test 2");
        testObjectDifferentValue = new TestObject("test 1", "XXXXXX");
    }


    /**
     * Test for two equal objects.
     */
    public void testAssertRefEquals_equals() {
        assertRefEquals(testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects (message version).
     */
    public void testAssertRefEquals_equalsMessage() {
        assertRefEquals("a message", testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects.
     */
    public void testAssertLenEquals_equals() {
        assertLenEquals(testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects (message version).
     */
    public void testAssertLenEquals_equalsMessage() {
        assertLenEquals("a message", testObjectA, testObjectB);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testAssertRefEquals_notEqualsDifferentValues() {
        String message = null;
        try {
            assertRefEquals(testObjectA, testObjectDifferentValue);

        } catch (AssertionFailedError a) {
            message = a.getMessage();
        }

        assertNotNull("An assertion exception should have been thrown", message);
        assertTrue(message.contains("string2"));
        assertTrue(message.contains("XXXXXX"));
        assertTrue(message.contains("test 2"));
    }


    /**
     * Test case for a null left-argument.
     */
    public void testAssertRefEquals_leftNull() {
        try {
            assertRefEquals(null, testObjectA);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for a null right-argument.
     */
    public void testAssertRefEquals_rightNull() {
        try {
            assertRefEquals(testObjectA, null);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for both null arguments.
     */
    public void testAssertRefEquals_null() {
        assertRefEquals(null, null);
    }


    /**
     * Test for two equal collections but with different order.
     */
    public void testAssertRefEquals_equalsLenientOrder() {
        assertRefEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"), LENIENT_ORDER);
    }


    /**
     * Test for two equal collections but with different order.
     */
    public void testAssertLenEquals_equalsLenientOrder() {
        assertLenEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"));
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertRefEquals_equalsIgnoredDefault() {
        testObjectA.setString1(null);
        testObjectB.setString1("xxxxxx");

        assertRefEquals(testObjectA, testObjectB, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertLenEquals_equalsIgnoredDefault() {
        testObjectA.setString1(null);
        testObjectB.setString1("xxxxxx");

        assertLenEquals(testObjectA, testObjectB);
    }


    /**
     * Test for message of 2 not equal arrays. Should return return actual content instead of something like String[234
     */
    public void testAssertLenEquals_formatArraysMessage() {
        try {
            assertLenEquals(new String[]{"test1", "test2"}, new Integer[]{1, 2});
        } catch (AssertionFailedError a) {
            // expected
            assertTrue(a.getMessage().contains("[test1, test2]"));
            assertTrue(a.getMessage().contains("[1, 2]"));
        }
    }


    /**
     * Test class with failing equals.
     */
    private class TestObject {

        private String string1;

        private String string2;

        public TestObject(String stringValue1, String stringValue2) {
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

}