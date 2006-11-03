/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.reflectionassert;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.framework.StringAssert;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

import java.util.Arrays;


/**
 * Test class for {@link ReflectionAssert}.
 */
public class ReflectionAssertTest extends TestCase {

    /* Test object */
    private TestObject testObjectA;

    /* Same as A but different instance */
    private TestObject testObjectB;

    /* Same as A and B but different string value for stringValue2 */
    private TestObject testObjectDifferentValue;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        testObjectA = new TestObject("test 1", "test 2");
        testObjectB = new TestObject("test 1", "test 2");
        testObjectDifferentValue = new TestObject("test 1", "XXXXXX");
    }


    /**
     * Test for two equal objects.
     */
    public void testAssertRefEquals_equals() {

        ReflectionAssert.assertRefEquals(testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects (message version).
     */
    public void testAssertRefEquals_equalsMessage() {

        ReflectionAssert.assertRefEquals("a message", testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects.
     */
    public void testAssertLenEquals_equals() {

        ReflectionAssert.assertLenEquals(testObjectA, testObjectB);
    }


    /**
     * Test for two equal objects (message version).
     */
    public void testAssertLenEquals_equalsMessage() {

        ReflectionAssert.assertLenEquals("a message", testObjectA, testObjectB);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testAssertRefEquals_notEqualsDifferentValues() {

        String message = null;
        try {
            ReflectionAssert.assertRefEquals(testObjectA, testObjectDifferentValue);

        } catch (AssertionFailedError a) {
            message = a.getMessage();
        }

        assertNotNull("An assertion exception should have been thrown", message);
        StringAssert.assertContains("string2", message);
        StringAssert.assertContains("XXXXXX", message);
        StringAssert.assertContains("test 2", message);
    }


    /**
     * Test case for a null left-argument.
     */
    public void testAssertRefEquals_leftNull() {

        try {
            ReflectionAssert.assertRefEquals(null, testObjectA);
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
            ReflectionAssert.assertRefEquals(testObjectA, null);
            fail("Expected AssertionFailedError");

        } catch (AssertionFailedError a) {
            // expected
        }
    }


    /**
     * Test case for both null arguments.
     */
    public void testAssertRefEquals_null() {

        ReflectionAssert.assertRefEquals(null, null);
    }


    /**
     * Test for two equal collections but with different order.
     */
    public void testAssertRefEquals_equalsLenientOrder() {

        ReflectionAssert.assertRefEquals(Arrays.asList("element1", "element2", "element3"), Arrays.asList("element3", "element1", "element2"), LENIENT_ORDER);
    }


    /**
     * Test for two equal collections but with different order.
     */
    public void testAssertLenEquals_equalsLenientOrder() {

        ReflectionAssert.assertLenEquals(Arrays.asList("element1", "element2", "element3"), Arrays.asList("element3", "element1", "element2"));
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertRefEquals_equalsIgnoredDefault() {

        testObjectA.setString1(null);
        testObjectB.setString1("xxxxxx");

        ReflectionAssert.assertRefEquals(testObjectA, testObjectB, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    public void testAssertLenEquals_equalsIgnoredDefault() {

        testObjectA.setString1(null);
        testObjectB.setString1("xxxxxx");

        ReflectionAssert.assertLenEquals(testObjectA, testObjectB);
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
        public boolean equals(Object o) {
            return false;
        }
    }

}