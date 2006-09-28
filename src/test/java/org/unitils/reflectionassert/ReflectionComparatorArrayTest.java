/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.reflectionassert;

import org.unitils.reflectionassert.ReflectionComparator.Difference;
import static org.unitils.reflectionassert.ReflectionComparatorModes.LENIENT_ORDER;
import junit.framework.TestCase;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with non-primitive array types.
 */
public class ReflectionComparatorArrayTest extends TestCase {

    /* Test array */
    private Element[] arrayA;

    /* Same as A but different instance */
    private Element[] arrayB;

    /* Same as A and B but different string value for element 2 */
    private Element[] arrayDifferentValue;

    /* Same as A and B but no 3rd element */
    private Element[] arrayDifferentSize;

    /* Same as A and B but different order of elements */
    private Element[] arrayDifferentOrder;

    /* Same as A and B but different order of elements and different string value */
    private Element[] arrayDifferentOrderDifferentValue;

    /* Test array with inner array for element 2 */
    private Element[] arrayInnerA;

    /* Same as innerA but different instance  */
    private Element[] arrayInnerB;

    /* Same as innerA and innerB but different string value for inner element 2 */
    private Element[] arrayInnerDifferentValue;

    /* Same as innerA and innerB but no 3rd inner element */
    private Element[] arrayInnerDifferentSize;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        arrayA = createArray("test 2", null, true);
        arrayB = createArray("test 2", null, true);
        arrayDifferentValue = createArray("XXXXXX", null, true);
        arrayDifferentSize = createArray("test 2", null, false);
        arrayDifferentOrder = createReverseArray("test 2");
        arrayDifferentOrderDifferentValue = createReverseArray("XXXXXX");

        arrayInnerA = createArray(null, arrayA, true);
        arrayInnerB = createArray(null, arrayB, true);
        arrayInnerDifferentValue = createArray(null, arrayDifferentValue, true);
        arrayInnerDifferentSize = createArray(null, arrayDifferentSize, true);

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal arrays.
     */
    public void testCheckEquals_equals() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayB);

        assertNull(result);
    }

    //todo test isequal


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
        assertEquals("1", result.getFieldStack().get(0));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
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
        assertEquals("1", result.getFieldStack().get(0));
        assertEquals("inner", result.getFieldStack().get(1));
        assertEquals("test 2", result.getLeftValue());
        assertEquals("XXXXXX", result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testCheckEquals_notEqualsInnerDifferentSize() {

        Difference result = reflectionComparator.getDifference(arrayInnerA, arrayInnerDifferentSize);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order.
     */
    public void testCheckEquals_notEqualsDifferentOrderNotLenient() {

        Difference result = reflectionComparator.getDifference(arrayA, arrayDifferentOrder);

        assertNotNull(result);
        assertEquals("0", result.getFieldStack().get(0));
        assertEquals("test 1", result.getLeftValue());
        assertEquals("test 3", result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testCheckEquals_equalsDifferentOrderLenient() {

        Difference result = new ReflectionComparator(LENIENT_ORDER).getDifference(arrayA, arrayDifferentOrder);

        assertNull(result);
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testCheckEquals_notEqualsDifferentOrderLenientDifferentValues() {

        Difference result = new ReflectionComparator(LENIENT_ORDER).getDifference(arrayA, arrayDifferentOrderDifferentValue);

        assertNotNull(result);
        assertEquals("1", result.getFieldStack().get(0));
        assertSame(arrayA[1], result.getLeftValue());
        assertNull(result.getRightValue());
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
     * Creates an array and reverses the elements.
     *
     * @param stringValueElement2 the value for the 2nd element in the array
     * @return the test array
     */
    private Element[] createReverseArray(String stringValueElement2) {
        Element[] array = createArray(stringValueElement2, null, true);
        Element temp = array[2];
        array[2] = array[0];
        array[0] = temp;
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