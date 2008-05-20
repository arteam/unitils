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

import junit.framework.TestCase;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import org.unitils.reflectionassert.difference.Difference;
import static org.unitils.reflectionassert.util.InnerDifferenceFinder.getInnerDifference;

import java.util.Arrays;
import java.util.Collection;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with non-primitive array types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
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
    private ReflectionComparator reflectionComparator, lenientOrderComparator;


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

        reflectionComparator = createRefectionComparator();
        lenientOrderComparator = createRefectionComparator(LENIENT_ORDER);
    }


    /**
     * Test for two equal arrays.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, arrayB);
        assertNull(result);
    }


    /**
     * Test for two equal arrays as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getAllDifferences(arrayInnerA, arrayInnerB);
        assertNull(result);
    }


    /**
     * Test for two arrays that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, arrayDifferentValue);

        Difference difference = getInnerDifference("string", getInnerDifference("1", result));
        assertEquals("test 2", difference.getLeftValue());
        assertEquals("XXXXXX", difference.getRightValue());
    }


    /**
     * Test for two arrays that have a different size.
     */
    public void testGetDifference_notEqualsDifferentSize() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, arrayDifferentSize);

        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentSize, result.getRightValue());
    }


    /**
     * Test for objects with inner arrays that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(arrayInnerA, arrayInnerDifferentValue);

        Difference difference = getInnerDifference("inner", getInnerDifference("1", result));
        Difference innerDifference = getInnerDifference("string", getInnerDifference("1", difference));
        assertEquals("test 2", innerDifference.getLeftValue());
        assertEquals("XXXXXX", innerDifference.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a different size.
     */
    public void testGetDifference_notEqualsInnerDifferentSize() {
        Difference result = reflectionComparator.getAllDifferences(arrayInnerA, arrayInnerDifferentSize);

        Difference difference = getInnerDifference("inner", getInnerDifference("1", result));
        assertSame(arrayA, difference.getLeftValue());
        assertSame(arrayDifferentSize, difference.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order.
     */
    public void testGetDifference_notEqualsDifferentOrderNotLenient() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, arrayDifferentOrder);

        Difference difference1 = getInnerDifference("string", getInnerDifference("0", result));
        assertEquals("test 1", difference1.getLeftValue());
        assertEquals("test 3", difference1.getRightValue());

        Difference difference2 = getInnerDifference("string", getInnerDifference("2", result));
        assertEquals("test 3", difference2.getLeftValue());
        assertEquals("test 1", difference2.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testGetDifference_equalsDifferentOrderLenient() {
        Difference result = lenientOrderComparator.getAllDifferences(arrayA, arrayDifferentOrder);
        assertNull(result);
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testGetDifference_notEqualsDifferentOrderLenientDifferentValues() {
        Difference result = lenientOrderComparator.getAllDifferences(arrayA, arrayDifferentOrderDifferentValue);

        assertSame(arrayA, result.getLeftValue());
        assertSame(arrayDifferentOrderDifferentValue, result.getRightValue());
    }


    /**
     * Tests for arrays but right value is not an array.
     */
    public void testGetDifference_notEqualsRightNotArray() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, "Test string");

        assertSame(arrayA, result.getLeftValue());
        assertEquals("Test string", result.getRightValue());
    }

    /**
     * Test for an array and a collection containing equal values  (array == collection).
     */
    public void testGetDifference_equalsLeftCollection() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, Arrays.asList(arrayA));
        assertNull(result);
    }

    /**
     * Test for an array and a collection containing equal values  (array == collection).
     */
    public void testGetDifference_equalsRightCollection() {
        Difference result = reflectionComparator.getAllDifferences(Arrays.asList(arrayA), arrayA);
        assertNull(result);
    }


    /**
     * Test for an array and a collection containing different values  (array != collection).
     */
    public void testGetDifference_notEqualsCollectionDifferentValues() {
        Difference result = reflectionComparator.getAllDifferences(arrayA, Arrays.asList(arrayDifferentValue));

        Difference difference = getInnerDifference("string", getInnerDifference("1", result));
        assertEquals("test 2", difference.getLeftValue());
        assertEquals("XXXXXX", difference.getRightValue());
    }


    /**
     * Test for an array and a collection having a different size (array != collection).
     */
    public void testGetDifference_notEqualsCollectionDifferentSize() {
        Collection<?> collectionDifferentSize = Arrays.asList(arrayDifferentSize);
        Difference result = reflectionComparator.getAllDifferences(arrayA, collectionDifferentSize);

        assertSame(arrayA, result.getLeftValue());
        assertSame(collectionDifferentSize, result.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testGetAllDifferences_equalsDifferentOrderLenient() {
        Difference result = lenientOrderComparator.getAllDifferences(arrayA, arrayDifferentOrder);
        assertNull(result);
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testGetAllDifferences_notEqualsDifferentOrderLenientDifferentValues() {
        Difference result = lenientOrderComparator.getAllDifferences(arrayA, arrayDifferentOrderDifferentValue);

        Difference innerDifference = getInnerDifference("string", getInnerDifference("1", result));
        assertEquals("test 2", innerDifference.getLeftValue());
        assertEquals("XXXXXX", innerDifference.getRightValue());
    }


    /**
     * Tests for objects with inner arrays that have a element order but with lenient order checking.
     */
    public void testGetAllDifferences_notEqualsDifferentOrderLenientMultipleDifferentValues() {
        arrayDifferentOrderDifferentValue[0].string = "XXXXXX";
        Difference result = lenientOrderComparator.getAllDifferences(arrayA, arrayDifferentOrderDifferentValue);

        Difference innerDifference1 = getInnerDifference("string", getInnerDifference("1", result));
        assertEquals("test 2", innerDifference1.getLeftValue());
        assertEquals("XXXXXX", innerDifference1.getRightValue());

        Difference innerDifference2 = getInnerDifference("string", getInnerDifference("2", result));
        assertEquals("test 3", innerDifference2.getLeftValue());
        assertEquals("XXXXXX", innerDifference2.getRightValue());
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
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }


}