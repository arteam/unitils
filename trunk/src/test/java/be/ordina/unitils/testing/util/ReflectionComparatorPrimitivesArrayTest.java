/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.testing.util;

import be.ordina.unitils.testing.util.ReflectionComparator.Difference;
import junit.framework.TestCase;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with primitive array types.
 */
public class ReflectionComparatorPrimitivesArrayTest extends TestCase {

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

    /* Class under test */
    private ReflectionComparator reflectionComparator;

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

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal arrays.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayB);

        assertNull(result);
    }


    /**
     * Test for two equal arrays as an inner field of an object.
     */
    public void testCheckEquals_equalsInner() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerB);

        assertNull(result);
    }


    /**
     * Test for two arrays that contain different values.
     */
    public void testCheckEquals_notEqualsDifferentValues() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentValue);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentValue, result.getRightValue());
    }


    /**
     * Test for two arrays that have a different size.
     */
    public void testCheckEquals_notEqualsDifferentSize() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentSize);

        assertNotNull(result);
        assertTrue(result.getFieldStack().isEmpty());
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
    }


    /**
     * Test for objects with inner arrays that contain different values.
     */
    public void testCheckEquals_notEqualsInnerDifferentValues() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentValue);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentValue, result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentSize);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
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