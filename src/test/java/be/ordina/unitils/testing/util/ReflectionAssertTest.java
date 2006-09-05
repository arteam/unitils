/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.framework.StringAssert;


/**
 * Test class for {@link ReflectionAssert}.
 */
public class ReflectionAssertTest extends TestCase {

    /* Test object */
    private Objects objectsA;

    /* Same as A but different instance */
    private Objects objectsB;

    /* Same as A and B but different string value for stringValue2 */
    private Objects objectsDifferentValue;

    /* Class under test */
    private ReflectionAssert reflectionAssert;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        objectsA = new Objects("test 1", "test 2");
        objectsB = new Objects("test 1", "test 2");
        objectsDifferentValue = new Objects("test 1", "XXXXXX");

        reflectionAssert = new ReflectionAssert();
    }

    //todo tests for lenient stuff

    /**
     * Test for two equal objects.
     */
    public void testAssertEquals_equals() {

        reflectionAssert.assertEquals(objectsA, objectsB);
    }


    /**
     * Test for two objects that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        String message = null;
        try {
            reflectionAssert.assertEquals(objectsA, objectsDifferentValue);

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
    public void testCheckEquals_leftNull() {

        String message = null;
        try {
            reflectionAssert.assertEquals(null, objectsA);

        } catch (AssertionFailedError a) {
            message = a.getMessage();
        }

        assertNotNull("An assertion exception should have been thrown", message);
        StringAssert.assertContains("top-level", message);
        StringAssert.assertContains("null", message);
        StringAssert.assertContains("be.ordina.unitils.testing.util.ReflectionAssertTest$Objects", message);
    }


    /**
     * Test case for a null right-argument.
     */
    public void testCheckEquals_rightNull() {

        String message = null;
        try {
            reflectionAssert.assertEquals(objectsA, null);

        } catch (AssertionFailedError a) {
            message = a.getMessage();
        }

        assertNotNull("An assertion exception should have been thrown", message);
        StringAssert.assertContains("null", message);
        StringAssert.assertContains("top-level", message);
        StringAssert.assertContains("be.ordina.unitils.testing.util.ReflectionAssertTest$Objects", message);
    }


    /**
     * Test case for both null arguments.
     */
    public void testCheckEquals_null() {

        reflectionAssert.assertEquals(null, null);
    }


    /**
     * Test class with failing equals.
     */
    private class Objects {

        /* A fist object value */
        private String string1;

        /* A second object value */
        private String string2;


        /**
         * Creates and initializes the objects instance.
         *
         * @param stringValue1 the first object value
         * @param stringValue2 the second object value
         */
        public Objects(String stringValue1, String stringValue2) {
            this.string1 = stringValue1;
            this.string2 = stringValue2;
        }

        /**
         * Gets the first object value
         *
         * @return the value
         */
        public String getString1() {
            return string1;
        }

        /**
         * Gets the second object value
         *
         * @return the value
         */
        public String getString2() {
            return string2;
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