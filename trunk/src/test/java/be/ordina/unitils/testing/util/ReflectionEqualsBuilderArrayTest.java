/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import junit.framework.TestCase;


/**
 * Test class for {@link ReflectionEquals}.
 * Contains tests with non-primitive array types.
 */
public class ReflectionEqualsBuilderArrayTest extends TestCase {

    /* Test array */
    private Element[] arrayA;

    /* Same as A but different instance */
    private Element[] arrayB;

    /* Same as A and B but different string value for element 2 */
    private Element[] arrayDifferentValue;

    /* Same as A and B but no 3rd element */
    private Element[] arrayDifferentSize;

    /* Test array with inner array for element 2 */
    private Element[] arrayInnerA;

    /* Same as innerA but different instance  */
    private Element[] arrayInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Element[] arrayInnerDifferentValue;

    /* Same as innerA and innerB but no 3rd inner element */
    private Element[] arrayInnerDifferentSize;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        arrayA = createArray("test 2", null, true);
        arrayB = createArray("test 2", null, true);
        arrayDifferentValue = createArray("XXXXXX", null, true);
        arrayDifferentSize = createArray("test 2", null, false);

        arrayInnerA = createArray(null, arrayA, true);
        arrayInnerB = createArray(null, arrayB, true);
        arrayInnerDifferentValue = createArray(null, arrayDifferentValue, true);
        arrayInnerDifferentSize = createArray(null, arrayDifferentSize, true);
    }


    /**
     * Test for two equal arrays.
     */
    public void testCheckEquals_equals() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayA, arrayB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two equal arrays as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayInnerA, arrayInnerB);

        assertTrue(reflectionEquals.isEquals());
        assertNull(reflectionEquals.getDifferenceFieldStack());
        assertNull(reflectionEquals.getDifferenceLeftValue());
        assertNull(reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two arrays that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayA, arrayDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for two arrays that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayA, arrayDifferentSize);

        assertFalse(reflectionEquals.isEquals());
        assertTrue(reflectionEquals.getDifferenceFieldStack().isEmpty());
        assertSame(arrayA, reflectionEquals.getDifferenceLeftValue());
        assertSame(arrayDifferentSize, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test for objects with inner arrays that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayInnerA, arrayInnerDifferentValue);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(1));
        assertEquals("test 2", reflectionEquals.getDifferenceLeftValue());
        assertEquals("XXXXXX", reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayInnerA, arrayInnerDifferentSize);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("1", reflectionEquals.getDifferenceFieldStack().get(0));
        assertSame(arrayA, reflectionEquals.getDifferenceLeftValue());
        assertSame(arrayDifferentSize, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Creates an array.
     *
     * @param stringValueElement2 the value for the 2nd element in the array
     * @param innerElement2       the value for the inner array of the 2nd element in the array
     * @param addElement3         true for an array of 3 elements, false for 2 elements
     * @return the test array
     */
    private Element[] createArray(String stringValueElement2, Element[] innerElement2, boolean addElement3) {
        Element[] array = new Element[addElement3 ? 3 : 2];
        array[0] = new Element("test 1", null);
        array[1] = new Element(stringValueElement2, innerElement2);
        if (addElement3) {
            array[2] = new Element("test 3", null);
        }
        return array;
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* A string value */
        private String string;

        /* An inner array */
        private Element[] inner;

        /**
         * Creates and initializes the element.
         *
         * @param string the string value
         * @param inner  the inner array
         */
        public Element(String string, Element[] inner) {
            this.string = string;
            this.inner = inner;
        }

        /**
         * Gets the string value
         *
         * @return the value
         */
        public String getString() {
            return string;
        }

        /**
         * Gets the inner array
         *
         * @return the array
         */
        public Element[] getInner() {
            return inner;
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