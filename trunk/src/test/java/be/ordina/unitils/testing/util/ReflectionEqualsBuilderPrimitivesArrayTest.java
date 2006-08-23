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
 * Contains tests with primitive array types.
 */
public class ReflectionEqualsBuilderPrimitivesArrayTest extends TestCase {

    /* Test array */
    private int[] arrayA;

    /* Same as A but different instance */
    private int[] arrayB;

    /* Same as A and B but different int value for element 2 */
    private int[] arrayDifferentValue;

    /* Same as A and B but no 3rd element */
    private int[] arrayDifferentSize;

    /* Test element with inner array for element 2 */
    private Element arrayInnerA;

    /* Same as innerA but different instance */
    private Element arrayInnerB;

    /* Same as innerA and innerB but different int value for inner element 2 */
    private Element arrayInnerDifferentValue;

    /* Same as innerA and innerB but no 3rd inner element */
    private Element arrayInnerDifferentSize;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        arrayA = new int[]{1, 2, 3};
        arrayB = new int[]{1, 2, 3};
        arrayDifferentValue = new int[]{1, 9999, 3};
        arrayDifferentSize = new int[]{1, 2};

        arrayInnerA = new Element(arrayA);
        arrayInnerB = new Element(arrayB);
        arrayInnerDifferentValue = new Element(arrayDifferentValue);
        arrayInnerDifferentSize = new Element(arrayDifferentSize);
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
        assertTrue(reflectionEquals.getDifferenceFieldStack().isEmpty());
        assertSame(arrayA, reflectionEquals.getDifferenceLeftValue());
        assertSame(arrayDifferentValue, reflectionEquals.getDifferenceRightValue());
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
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(0));
        assertSame(arrayA, reflectionEquals.getDifferenceLeftValue());
        assertSame(arrayDifferentValue, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {

        ReflectionEquals reflectionEquals = ReflectionEquals.checkEquals(arrayInnerA, arrayInnerDifferentSize);

        assertFalse(reflectionEquals.isEquals());
        assertEquals("inner", reflectionEquals.getDifferenceFieldStack().get(0));
        assertSame(arrayA, reflectionEquals.getDifferenceLeftValue());
        assertSame(arrayDifferentSize, reflectionEquals.getDifferenceRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Element {

        /* An inner array */
        private int[] inner;

        /**
         * Creates and initializes the element.
         *
         * @param inner the inner array
         */
        public Element(int[] inner) {
            this.inner = inner;
        }

        /**
         * Gets the inner array
         *
         * @return the array
         */
        public int[] getInner() {
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